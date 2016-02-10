package org.sw.marketing.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.sw.marketing.dao.calendar.DAOFactory;
import org.sw.marketing.dao.calendar.CalendarDAO;
import org.sw.marketing.dao.calendar.user.UserDAO;
import org.sw.marketing.data.calendar.*;
import org.sw.marketing.data.calendar.Data.*;
import org.sw.marketing.data.calendar.Data.Message;
import org.sw.marketing.servlet.params.calendar.CalendarParameters;
import org.sw.marketing.servlet.params.survey.QuestionParameters;
import org.sw.marketing.servlet.params.survey.SurveyParameters;
import org.sw.marketing.transformation.TransformerHelper;
import org.sw.marketing.util.ReadFile;

@WebServlet("/calendarAdmin")
public class CalendarAdminController extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	//
	java.util.List<String> innerScreenList = new java.util.ArrayList<String>();
	
	public void init()
	{
		innerScreenList.add("GENERAL");
	}
	
	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		HttpSession httpSession = request.getSession();
		
		/*
		 * DAO Initialization
		 */
		UserDAO userDAO = DAOFactory.getUserDAO();
		CalendarDAO calendarDAO = DAOFactory.getCalendarDAO();

		/*
		 * Data Initialization
		 */
		Data data = new Data();
		Environment environment = new Environment();
		java.util.List<Calendar> calendars = null;
		Calendar calendar = null;
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
		 * Screen filePath
		 */
		String xslScreen = null;
		
		/*
		 * Process Actions like Create, Update and Delete
		 */
		if(parameterMap.get("ACTION") != null)
		{
			String paramAction = parameterMap.get("ACTION")[0];
			if(calendarID == 0)
			{
				if(paramAction.equals("CREATE_CALENDAR"))
				{
					calendarID = calendarDAO.createCalendar(user);
					calendar = calendarDAO.getCalendar(calendarID);
				}
			}
			else
			{
				calendar = calendarDAO.getCalendar(calendarID);
				
				if(paramAction.equals("SAVE_CALENDAR"))
				{
					calendar = CalendarParameters.process(request, calendar);
					
					Calendar tempCalendar = calendarDAO.getCalendarByPrettyUrl(calendar.getPrettyUrl());
					if(tempCalendar != null && tempCalendar.getId() != calendar.getId() && tempCalendar.getPrettyUrl().equals(calendar.getPrettyUrl()))
					{
						Message message = new Message();
						message.setType("error");
						message.setLabel("The pretty URL is already in use.  Please choose a unique one.");
						data.getMessage().add(message);
					}
					else if(parameterMap.get("CALENDAR_PRETTY_URL") != null && parameterMap.get("CALENDAR_PRETTY_URL")[0].trim().equals(""))
					{
						Message message = new Message();
						message.setType("error");
						message.setLabel("Please enter a pretty URL.");
						data.getMessage().add(message);
					}
					else
					{
						calendarDAO.updateCalendar(calendar);
						
						Message message = new Message();
						message.setType("success");
						message.setLabel("The calendar has been saved.");
						data.getMessage().add(message);
					}
				}
				else if(paramAction.equals("DELETE_CALENDAR"))
				{
					calendarDAO.deleteCalendar(calendar.getId());
					
					Message message = new Message();
					message.setType("success");
					message.setLabel("The calendar has been deleted.");
					data.getMessage().add(message);
					
					calendarID = 0;
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
				
			if(paramScreen.equals("ROLES"))
			{
				xslScreen = "calendar_roles.xsl";
			}
			else
			{
				xslScreen = "calendar_general.xsl";
			}
			
			if(calendar != null)
			{
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
		
		environment.setComponentId(3);
		environment.setServerName(getBaseUrl(request));
		data.setEnvironment(environment);
		
		TransformerHelper transformerHelper = new TransformerHelper();
		transformerHelper.setUrlResolverBaseUrl(getServletContext().getInitParameter("assetXslCalendarsUrl"));
		
		String xmlStr = transformerHelper.getXmlStr("org.sw.marketing.data.calendar", data);
		xslScreen = getServletContext().getInitParameter("assetXslCalendarsPath") + xslScreen;
		String xslStr = ReadFile.getSkin(xslScreen);
		String htmlStr = transformerHelper.getHtmlStr(xmlStr, new ByteArrayInputStream(xslStr.getBytes()));
		
		String toolboxSkinPath = getServletContext().getInitParameter("assetPath") + "toolbox.html";
		String skinHtmlStr = ReadFile.getSkin(toolboxSkinPath);
		skinHtmlStr = skinHtmlStr.replace("{NAME}", "List of Calendars");
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
