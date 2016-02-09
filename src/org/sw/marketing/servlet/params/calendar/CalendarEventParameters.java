package org.sw.marketing.servlet.params.calendar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.sw.marketing.data.calendar.Data.Calendar.Event;

public class CalendarEventParameters
{
	public static Event process(HttpServletRequest request, Event event)
	{
		@SuppressWarnings("unchecked")
		java.util.Map<String, String[]> parameterMap = (java.util.HashMap<String, String[]>) request.getAttribute("parameterMap");

		DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
		DateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy");
		
		if(parameterMap.get("EVENT_START_DATE") != null)
		{
			String paramStartDate = parameterMap.get("EVENT_START_DATE")[0];			
			try
			{
				java.sql.Date sqlDate = new java.sql.Date(dateFormatter.parse(paramStartDate).getTime());
				GregorianCalendar gregorianCalendar = new GregorianCalendar();
				gregorianCalendar.setTime(sqlDate);
				try
				{
					XMLGregorianCalendar xmlStartDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
					event.setStartDate(xmlStartDate);
				}
				catch (DatatypeConfigurationException e)
				{
					e.printStackTrace();
				}
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
		}
		if(parameterMap.get("EVENT_START_TIME") != null)
		{
			String paramTime = parameterMap.get("EVENT_START_TIME")[0];
			try
			{
				java.sql.Date sqlDate = new java.sql.Date(timeFormatter.parse(paramTime).getTime());
				GregorianCalendar gregorianCalendar = new GregorianCalendar();
				gregorianCalendar.setTime(sqlDate);
				try
				{
					XMLGregorianCalendar xmlTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
					event.setStartTime(xmlTime);
				}
				catch (DatatypeConfigurationException e)
				{
					e.printStackTrace();
				}
			}
			catch (ParseException e1)
			{
				e1.printStackTrace();
			}			
		}
		if(parameterMap.get("EVENT_END_DATE") != null)
		{
			String paramDate = parameterMap.get("EVENT_END_DATE")[0];			
			try
			{
				java.sql.Date sqlDate = new java.sql.Date(dateFormatter.parse(paramDate).getTime());
				GregorianCalendar gregorianCalendar = new GregorianCalendar();
				gregorianCalendar.setTime(sqlDate);
				try
				{
					XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
					event.setEndDate(xmlDate);
				}
				catch (DatatypeConfigurationException e)
				{
					e.printStackTrace();
				}
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
		}
		if(parameterMap.get("EVENT_END_TIME") != null)
		{
			String paramTime = parameterMap.get("EVENT_END_TIME")[0];
			try
			{
				java.sql.Date sqlDate = new java.sql.Date(timeFormatter.parse(paramTime).getTime());
				GregorianCalendar gregorianCalendar = new GregorianCalendar();
				gregorianCalendar.setTime(sqlDate);
				try
				{
					XMLGregorianCalendar xmlTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
					event.setEndTime(xmlTime);
				}
				catch (DatatypeConfigurationException e)
				{
					e.printStackTrace();
				}
			}
			catch (ParseException e1)
			{
				e1.printStackTrace();
			}			
		}
		if(parameterMap.get("EVENT_TITLE") != null)
		{
			event.setTitle(parameterMap.get("EVENT_TITLE")[0]);
		}
		if(parameterMap.get("EVENT_DESCRIPTION") != null)
		{
			event.setDescription(parameterMap.get("EVENT_DESCRIPTION")[0]);
		}
		if(parameterMap.get("IS_EVENT_LOCATION_OWNED") != null)
		{
			event.setLocationOwned(Boolean.parseBoolean(parameterMap.get("IS_EVENT_LOCATION_OWNED")[0]));
		}
		if(parameterMap.get("EVENT_LOCATION") != null)
		{
			event.setLocation(parameterMap.get("EVENT_LOCATION")[0]);
		}
		if(parameterMap.get("EVENT_LOCATION_ADDITIONAL") != null)
		{
			event.setLocationAdditional(parameterMap.get("EVENT_LOCATION_ADDITIONAL")[0]);
		}
		if(parameterMap.get("EVENT_SPEAKER") != null)
		{
			event.setSpeaker(parameterMap.get("EVENT_SPEAKER")[0]);
		}
		if(parameterMap.get("EVENT_REGISTRATION_LABEL") != null)
		{
			event.setRegistrationLabel(parameterMap.get("EVENT_REGISTRATION_LABEL")[0]);
		}
		if(parameterMap.get("EVENT_REGISTRATION_URL") != null)
		{
			event.setRegistrationUrl(parameterMap.get("EVENT_REGISTRATION_URL")[0]);
		}
		if(parameterMap.get("EVENT_CONTACT_NAME") != null)
		{
			event.setContactName(parameterMap.get("EVENT_CONTACT_NAME")[0]);
		}
		if(parameterMap.get("EVENT_CONTACT_PHONE") != null)
		{
			event.setContactPhone(parameterMap.get("EVENT_CONTACT_PHONE")[0]);
		}
		if(parameterMap.get("EVENT_CONTACT_EMAIL") != null)
		{
			event.setContactEmail(parameterMap.get("EVENT_CONTACT_EMAIL")[0]);
		}
		if(parameterMap.get("EVENT_COST") != null)
		{
			event.setCost(parameterMap.get("EVENT_COST")[0]);
		}

		return event;
	}
}
