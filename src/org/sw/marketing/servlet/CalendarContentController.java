package org.sw.marketing.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.sw.marketing.dao.calendar.CalendarDAO;
import org.sw.marketing.dao.calendar.DAOFactory;
import org.sw.marketing.dao.calendar.category.CalendarCategoryDAO;
import org.sw.marketing.dao.calendar.event.CalendarEventDAO;
import org.sw.marketing.dao.calendar.event.CalendarEventTagDAO;
import org.sw.marketing.dao.calendar.user.UserDAO;
import org.sw.marketing.data.calendar.Data;
import org.sw.marketing.data.calendar.Data.Calendar;
import org.sw.marketing.data.calendar.Data.Calendar.Category;
import org.sw.marketing.data.calendar.Data.Calendar.Event;
import org.sw.marketing.data.calendar.Data.Calendar.Event.EventRecurrence;
import org.sw.marketing.data.calendar.Data.Calendar.Event.Tag;
import org.sw.marketing.data.calendar.Data.Environment;
import org.sw.marketing.data.calendar.Data.User;
import org.sw.marketing.servlet.params.calendar.CalendarEventParameters;
import org.sw.marketing.transformation.TransformerHelper;
import org.sw.marketing.util.DateToXmlGregorianCalendar;
import org.sw.marketing.util.ReadFile;
import org.sw.marketing.util.Recursion;

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
		innerScreenList.add("CATEGORIES");
		innerScreenList.add("EVENT_RECURRENCE");
		innerScreenList.add("EVENT_IMAGE_UPLOAD");		
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
		CalendarCategoryDAO categoryDAO = DAOFactory.getCalendarCategoryDAO();

		/*
		 * Data Initialization
		 */
		Data data = new Data();
		Environment environment = new Environment();
		java.util.List<Calendar> calendars = null;
		Calendar calendar = null;
		Event event = null;
		User user = null;
		
		/*
		 * Get user session information
		 */
		if(httpSession.getAttribute("EMAIL_ADDRESS") != null)
		{
			String email = (String) httpSession.getAttribute("EMAIL_ADDRESS");
			user = userDAO.getUserByEmail(email);
			if(user == null)
			{
				response.getWriter().println("Email not recognized.");
				return;
			}
			data.setUser(user);
		}
		
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
				else if(paramAction.equals("ADD_CATEGORY"))
				{
					String paramCategoryLabel = parameterMap.get("CALENDAR_ADD_CATEGORY")[0];
					
					Category category = new Category();
					category.setLabel(paramCategoryLabel);
					category.setFkCalendarId(calendarID);
					
					categoryDAO.insert(category);
					
//					Role uniqueRole = roleDAO.getUniqueRole(role);
//					if(uniqueRole == null)
//					{
//						roleDAO.insert(role);
//					}
//					else
//					{
//						Message message = new Message();
//						message.setType("error");
//						message.setLabel("The role/email combination already exists.");
//						data.getMessage().add(message);
//					}
				}
				else if(paramAction.equals("DELETE_CATEGORY"))
				{
					String paramCategoryID = parameterMap.get("CATEGORY_ID")[0];
					long categoryID = Long.parseLong(paramCategoryID);
					
					categoryDAO.delete(categoryID);
				}
			}			
			else if(eventID > 0)
			{
				event = eventDAO.getCalendarEvent(eventID);
				
				if(paramAction.equals("SAVE_EVENT"))
				{
					event = CalendarEventParameters.process(request, event);
					event.setParentId(0);
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
					
					copyEvents(event.getId());
					eventDAO.updateCalendarRecurringEvent(event);
				}
				else if(paramAction.equals("DELETE_EVENT_IMAGE"))
				{
					String uploadPath = getServletContext().getInitParameter("calendarUploadsPath");					
					String calendarDirectoryPath = uploadPath + request.getParameter("CALENDAR_ID");
					String eventDirectoryPath = calendarDirectoryPath + java.io.File.separator + request.getParameter("EVENT_ID");
					String fileUploadPath = eventDirectoryPath + java.io.File.separator + event.getFileName();
					
					java.io.File uploadedFile = new java.io.File(fileUploadPath);
					if(uploadedFile.exists())
					{
						uploadedFile.delete();
						
						java.io.File eventDirectory = new java.io.File(eventDirectoryPath);
						if(eventDirectory.exists())
						{
							eventDirectory.delete();
						}
					}
					
					event.setFileName("");
					event.setFileDescription("");
					eventDAO.updateCalendarEvent(event);
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
				else if(paramScreen.equals("EVENT_IMAGE_UPLOAD"))
				{
					xslScreen = "calendar_event_image_upload.xsl";
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
			else if(paramScreen.equals("CATEGORIES"))
			{				
				xslScreen = "calendar_categories.xsl";
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
				
				java.util.List<Category> categories = categoryDAO.getCategories(calendarID);
				if(categories != null)
				{
					calendar.getCategory().addAll(categories);
				}
				
				data.getCalendar().add(calendar);
			}
		}
		else
		{
			xslScreen = "calendar_list.xsl";
			
			calendars = calendarDAO.getCalendarsManage(user);
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
		
//		System.out.println(xmlStr);
		response.getWriter().print(skinHtmlStr);
	}
	
	private void copyEvents(long eventID)
	{
		CalendarEventDAO eventDAO = DAOFactory.getCalendarEventDAO();
		Event event = eventDAO.getCalendarEvent(eventID);
		
		EventRecurrence recurrence = event.getEventRecurrence();
		if(recurrence.isRecurring())
		{						
			Recursion recursion = new Recursion();
			recursion.setStartDate(LocalDate.parse(event.getStartDate().toString().substring(0, 10)));
			recursion.setCurrentDate(recursion.getStartDate());
			recursion.setEndDate(LocalDate.parse(event.getEndDate().toString().substring(0, 10)));
			if(recurrence.getType().equals("interval"))
			{
				recursion.setLimitEnabled(true);
				recursion.setEndDate(LocalDate.parse("2099-12-31"));
			}
			else
			{
				recursion.setLimitEnabled(false);
			}
			recursion.setLimit(recurrence.getLimit());
			recursion.setInterval(recurrence.getInterval());
			
			java.util.List<String> dates = null;
			boolean recurringMonthly = recurrence.isRecurringMonthly();
			if(recurringMonthly)
			{
				dates = recursion.printMonthly(recursion);
			}
			else
			{
				recursion.setStartDate(recursion.getStartDate().plusDays(1));
				java.util.List<String> recursions = new java.util.ArrayList<String>();
				if(recurrence.isMonday())
				{
					recursions.add("MONDAY");
				}
				if(recurrence.isTuesday())
				{
					recursions.add("TUESDAY");
				}
				if(recurrence.isWednesday())
				{
					recursions.add("WEDNESDAY");
				}
				if(recurrence.isThursday())
				{
					recursions.add("THURSDAY");
				}
				if(recurrence.isFriday())
				{
					recursions.add("FRIDAY");
				}
				if(recurrence.isSaturday())
				{
					recursions.add("SATURDAY");
				}
				if(recurrence.isSunday())
				{
					recursions.add("SUNDAY");
				}
				recursion.setRecursions(recursions);
				
				dates = recursion.printWeekly(recursion);
			}
			
			if(dates != null)
			{
				processDates(dates, event);
			}
		}
	}
	
	private static void processDates(java.util.List<String> dates, Event event)
	{
		CalendarEventDAO eventDAO = DAOFactory.getCalendarEventDAO();
		
		/*
		 * delete existing recurring events
		 */
		eventDAO.deleteRecurring(event.getId());
		
		long parentId = event.getId();
		java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");		
		
		java.util.Iterator<String> datesIt = dates.iterator();
		while(datesIt.hasNext())
		{
			String date = datesIt.next();
			java.util.Date recurringEventDate;
			try
			{
				recurringEventDate = dateFormat.parse(date);
				GregorianCalendar gregorianCalendar = new GregorianCalendar();
				gregorianCalendar.setTime(recurringEventDate);
				XMLGregorianCalendar xmlDate = DateToXmlGregorianCalendar.convert(recurringEventDate, false);

				long newId = eventDAO.copyEvent(event);
				Event newEvent = eventDAO.getCalendarEvent(newId);
				newEvent.setStartDate(xmlDate);
				newEvent.setEndDate(xmlDate);
				eventDAO.updateCalendarEvent(newEvent);
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
		}
		eventDAO.deleteFirstRecurring(event.getParentId());
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
