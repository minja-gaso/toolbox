package org.sw.marketing.servlet.params.survey;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.XMLGregorianCalendar;

import org.sw.marketing.data.form.Data.Form;
import org.sw.marketing.util.DateToXmlGregorianCalendar;

public class SurveyParameters
{
	public static Form process(HttpServletRequest request, Form form)
	{		
		@SuppressWarnings("unchecked")
		java.util.Map<String, String[]> parameterMap = (java.util.HashMap<String, String[]>) request.getAttribute("parameterMap");

		if(parameterMap.get("FORM_TITLE") != null)
		{
			form.setTitle(parameterMap.get("FORM_TITLE")[0]);
		}
		if(parameterMap.get("FORM_STATUS") != null)
		{
			form.setStatus(parameterMap.get("FORM_STATUS")[0]);
		}
		if(parameterMap.get("FORM_PRETTY_URL") != null)
		{
			String prettyUrl = parameterMap.get("FORM_PRETTY_URL")[0];
			prettyUrl = prettyUrl.replaceAll("[^A-Za-z0-9\\-\\.\\_\\~\\s]", "").replaceAll("\\s", "-");
			prettyUrl = prettyUrl.toLowerCase();
			
			form.setPrettyUrl(prettyUrl);
			
		}
		if(parameterMap.get("FORM_SKIN_URL") != null)
		{
			form.setSkinUrl(parameterMap.get("FORM_SKIN_URL")[0]);
		}
		if(parameterMap.get("FORM_START_DATE") != null)
		{
			String startDateStr = parameterMap.get("FORM_START_DATE")[0];
			DateFormat format = new SimpleDateFormat("MM-dd-yyyy");
			Date startDate;
			try
			{
				startDate = format.parse(startDateStr);
				XMLGregorianCalendar startDateCal = DateToXmlGregorianCalendar.convert(startDate, false);
				form.setStartDate(startDateCal);
			}
			catch (ParseException e)
			{
				form.setStartDate(form.getStartDate());
			}
		}
		if(parameterMap.get("FORM_END_DATE") != null)
		{
			String endDateStr = parameterMap.get("FORM_END_DATE")[0];
			DateFormat format = new SimpleDateFormat("MM-dd-yyyy");
			Date endDate;
			try
			{
				endDate = format.parse(endDateStr);
				XMLGregorianCalendar endDateCal = DateToXmlGregorianCalendar.convert(endDate, false);
				form.setEndDate(endDateCal);
			}
			catch (ParseException e)
			{
				form.setEndDate(form.getStartDate());
			}
		}
		if(parameterMap.get("FORM_SKIN_SELECTOR") != null)
		{
			form.setSkinSelector(parameterMap.get("FORM_SKIN_SELECTOR")[0]);
		}
		if(parameterMap.get("FORM_MAX_SUBMISSIONS") != null)
		{
			int maxSubmissions = 0;
			try
			{
				maxSubmissions = Integer.parseInt(parameterMap.get("FORM_MAX_SUBMISSIONS")[0]);
			}
			catch(NumberFormatException e)
			{
				maxSubmissions = form.getMaxSubmissions();
			}
			form.setMaxSubmissions(maxSubmissions);
		}

		return form;
	}
}
