package org.sw.marketing.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.sw.marketing.dao.calendar.CalendarDAO;
import org.sw.marketing.dao.calendar.DAOFactory;
import org.sw.marketing.dao.calendar.event.CalendarEventDAO;
import org.sw.marketing.dao.calendar.event.CalendarEventTagDAO;
import org.sw.marketing.dao.calendar.user.UserDAO;
import org.sw.marketing.data.calendar.Data;
import org.sw.marketing.data.calendar.Data.Calendar;
import org.sw.marketing.data.calendar.Data.Calendar.Event;
import org.sw.marketing.data.calendar.Data.Calendar.Event.Tag;
import org.sw.marketing.data.calendar.Data.Environment;
import org.sw.marketing.data.calendar.Data.Message;
import org.sw.marketing.data.calendar.Data.User;
import org.sw.marketing.servlet.params.calendar.CalendarEventParameters;
import org.sw.marketing.servlet.params.calendar.CalendarParameters;
import org.sw.marketing.transformation.TransformerHelper;
import org.sw.marketing.util.ReadFile;

@WebServlet("/calendarContent")
public class CalendarContentController extends HttpServlet
{
	private static final long serialVersionUID = 1L;	
	//
	java.util.List<String> innerScreenList = new java.util.ArrayList<String>();
	
	public void init()
	{
		innerScreenList.add("EVENTS");
		innerScreenList.add("EVENT");
	}
	
	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		HttpSession httpSession = request.getSession();
		
		/*
		 * DAO Initialization
		 */
		UserDAO userDAO = DAOFactory.getUserDAO();
		CalendarDAO calendarDAO = DAOFactory.getCalendarDAO();
		CalendarEventDAO eventDAO = DAOFactory.getCalendarEventDAO();
		CalendarEventTagDAO eventTagDAO = DAOFactory.getCalendarEventTagDAO();

		/*
		 * Data Initialization
		 */
		Data data = new Data();
		Environment environment = new Environment();
		java.util.List<Calendar> calendars = null;
		Calendar calendar = null;
		Event event = null;
		User user = userDAO.getUserByEmail("gaso@illinois.edu");
		
		/*
		 * Add parameters to HashMap
		 */
		@SuppressWarnings("unchecked")
		java.util.Map<String, String[]> parameterMap = (java.util.HashMap<String, String[]>) request.getAttribute("parameterMap");

		/*
		 * Calendar ID
		 */
		long calendarID = 0;
		if(parameterMap.get("CALENDAR_ID") != null)
		{
			try
			{
				calendarID = Long.parseLong(parameterMap.get("CALENDAR_ID")[0]);
			}
			catch(NumberFormatException e)
			{
				calendarID = 0;
			}
		}
		
		/*
		 * Event ID
		 */
		long eventID = 0;
		if(parameterMap.get("EVENT_ID") != null)
		{
			try
			{
				eventID = Long.parseLong(parameterMap.get("EVENT_ID")[0]);
			}
			catch(NumberFormatException e)
			{
				eventID = 0;
			}
		}
		
		/*
		 * Screen filePath
		 */
		String xslScreen = null;
		
		/*
		 * Process Actions like Create, Update and Delete
		 */
		if(parameterMap.get("ACTION") != null)
		{
			String paramAction = parameterMap.get("ACTION")[0];
			
			if(eventID == 0)
			{
				if(paramAction.equals("CREATE_EVENT"))
				{
					eventID = eventDAO.createCalendarEvent(calendarID);
					event = eventDAO.getCalendarEvent(eventID);
				}
			}			
			else if(eventID > 0)
			{
				event = eventDAO.getCalendarEvent(eventID);
				
				if(paramAction.equals("SAVE_EVENT"))
				{
					event = CalendarEventParameters.process(request, event);
					eventDAO.updateCalendarEvent(event);
					
					String[] eventTags = parameterMap.get("EVENT_TAGS");
					if(eventTags != null && eventTags.length > 0)
					{
						eventTagDAO.deleteTags(eventID);
						for(int index = 0; index < eventTags.length; index++)
						{
							String eventTag = eventTags[index].trim();
							eventTagDAO.addTag(eventTag, eventID);
						}
					}
				}
			}
		}
		
		/*
		 * Determine which screen to display
		 */
		if(parameterMap.get("SCREEN") != null && calendarID > 0)
		{
			String paramScreen = parameterMap.get("SCREEN")[0];
			
			if(innerScreenList.contains(paramScreen))
			{
				calendar = calendarDAO.getCalendar(calendarID);
			}
			
			if(event != null)
			{
				if(paramScreen.equals("EVENT_RECURRENCE"))
				{
					xslScreen = "calendar_event_recurrence.xsl";
				}
				else
				{
					java.util.List<Tag> tags = eventTagDAO.getTags(eventID);
					if(tags != null)
					{
						event.getTag().addAll(tags);
					}
					xslScreen = "calendar_event.xsl";
				}
			}
			else if(paramScreen.equals("EVENTS"))
			{
				java.util.List<Event> events = eventDAO.getCalendarEvents(calendarID);
				if(events != null)
				{
					calendar.getEvent().addAll(events);
				}
				
				xslScreen = "calendar_events.xsl";
			}
			else
			{
				xslScreen = "calendar_list.xsl";
			}
			
			if(calendar != null)
			{
				if(event != null)
				{
					calendar.getEvent().add(event);
				}
				
				data.getCalendar().add(calendar);
			}
		}
		else
		{
			xslScreen = "calendar_list.xsl";
			
			calendars = calendarDAO.getCalendars();
			if(calendars != null)
			{
				data.getCalendar().addAll(calendars);
			}
		}
		
		environment.setComponentId(4);
		environment.setServerName(getBaseUrl(request));
		data.setEnvironment(environment);
		
		TransformerHelper transformerHelper = new TransformerHelper();
		transformerHelper.setUrlResolverBaseUrl(getServletContext().getInitParameter("assetXslCalendarsContentUrl"));
		
		String xmlStr = transformerHelper.getXmlStr("org.sw.marketing.data.calendar", data);
		xslScreen = getServletContext().getInitParameter("assetXslCalendarsContentPath") + xslScreen;
		String xslStr = ReadFile.getSkin(xslScreen);
		String htmlStr = transformerHelper.getHtmlStr(xmlStr, new ByteArrayInputStream(xslStr.getBytes()));
		
		String toolboxSkinPath = getServletContext().getInitParameter("assetPath") + "toolbox.html";
		String skinHtmlStr = ReadFile.getSkin(toolboxSkinPath);
		skinHtmlStr = skinHtmlStr.replace("{NAME}", "Calendar Content");
		skinHtmlStr = skinHtmlStr.replace("{CONTENT}", htmlStr);
		
		System.out.println(xmlStr);
		response.getWriter().print(skinHtmlStr);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		process(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		process(request, response);
	}
	
	/**
	 * NOT UNIT TESTED Returns the base url (e.g, <tt>http://myhost:8080/myapp</tt>) suitable for
	 * using in a base tag or building reliable urls.
	 */
	public static String getBaseUrl(HttpServletRequest request) {
		if ((request.getServerPort() == 80) || (request.getServerPort() == 443))
		{
			return request.getScheme() + "://" + request.getServerName();	
		}
		else
		{

			return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		}
	}
}
