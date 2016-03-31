package org.sw.marketing.servlet.params.blog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.sw.marketing.data.blog.Data.Blog.Topic;

public class BlogTopicParameters
{
	public static Topic process(HttpServletRequest request, Topic topic)
	{
		@SuppressWarnings("unchecked")
		java.util.Map<String, String[]> parameterMap = (java.util.HashMap<String, String[]>) request.getAttribute("parameterMap");

		DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
		DateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy");

		if(parameterMap.get("TOPIC_PUBLISHED") != null)
		{
			topic.setPublished(Boolean.parseBoolean(parameterMap.get("TOPIC_PUBLISHED")[0]));
		}
		if(parameterMap.get("TOPIC_PUBLISH_DATE") != null)
		{
			String paramStartDate = parameterMap.get("TOPIC_PUBLISH_DATE")[0];			
			try
			{
				java.sql.Date sqlDate = new java.sql.Date(dateFormatter.parse(paramStartDate).getTime());
				GregorianCalendar gregorianCalendar = new GregorianCalendar();
				gregorianCalendar.setTime(sqlDate);
				try
				{
					XMLGregorianCalendar xmlStartDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
					topic.setPublishDate(xmlStartDate);
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
		if(parameterMap.get("TOPIC_PUBLISH_TIME") != null)
		{
			String paramTime = parameterMap.get("TOPIC_PUBLISH_TIME")[0];
			try
			{
				java.sql.Date sqlDate = new java.sql.Date(timeFormatter.parse(paramTime).getTime());
				GregorianCalendar gregorianCalendar = new GregorianCalendar();
				gregorianCalendar.setTime(sqlDate);
				try
				{
					XMLGregorianCalendar xmlTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
					topic.setPublishTime(xmlTime);
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
		if(parameterMap.get("TOPIC_TITLE") != null)
		{
			topic.setTitle(parameterMap.get("TOPIC_TITLE")[0]);
		}
		if(parameterMap.get("TOPIC_SUMMARY") != null)
		{
			topic.setSummary(parameterMap.get("TOPIC_SUMMARY")[0]);
		}
		if(parameterMap.get("TOPIC_ARTICLE") != null)
		{
			topic.setArticle(parameterMap.get("TOPIC_ARTICLE")[0]);
		}
		
		return topic;
	}
}
