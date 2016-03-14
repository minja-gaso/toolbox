package org.sw.marketing.servlet.params.calendar;

import javax.servlet.http.HttpServletRequest;

import org.sw.marketing.data.calendar.Data.Calendar;

public class CalendarParameters
{
	public static Calendar process(HttpServletRequest request, Calendar calendar)
	{
		@SuppressWarnings("unchecked")
		java.util.Map<String, String[]> parameterMap = (java.util.HashMap<String, String[]>) request.getAttribute("parameterMap");

		if(parameterMap.get("CALENDAR_TITLE") != null)
		{
			calendar.setTitle(parameterMap.get("CALENDAR_TITLE")[0]);
		}
		if(parameterMap.get("CALENDAR_PRETTY_URL") != null)
		{
			String prettyUrl = parameterMap.get("CALENDAR_PRETTY_URL")[0];
			prettyUrl = prettyUrl.replaceAll("[^A-Za-z0-9\\-\\.\\_\\~\\s]", "").replaceAll("\\s", "-");
			prettyUrl = prettyUrl.toLowerCase();
			
			calendar.setPrettyUrl(prettyUrl);			
		}
//		if(parameterMap.get("CALENDAR_SKIN_URL") != null)
//		{
//			calendar.setSkinUrl(parameterMap.get("CALENDAR_SKIN_URL")[0]);
//		}		
//		if(parameterMap.get("CALENDAR_SKIN_SELECTOR") != null)
//		{
//			calendar.setSkinSelector(parameterMap.get("CALENDAR_SKIN_SELECTOR")[0]);
//		}
		if(parameterMap.get("CALENDAR_SKIN_ID") != null)
		{
			calendar.setFkSkinId(Long.parseLong(parameterMap.get("CALENDAR_SKIN_ID")[0]));
		}
//		if(parameterMap.get("CALENDAR_CSS") != null)
//		{
//			calendar.setSkinCssOverrides(parameterMap.get("CALENDAR_CSS")[0]);
//		}

		return calendar;
	}
}
