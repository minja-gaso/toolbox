package org.sw.marketing.servlet.params;

import javax.servlet.http.HttpServletRequest;

import org.sw.marketing.data.form.Data.Form;

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
		if(parameterMap.get("FORM_SKIN_SELECTOR") != null)
		{
			form.setSkinSelector(parameterMap.get("FORM_SKIN_SELECTOR")[0]);
		}

		return form;
	}
}
