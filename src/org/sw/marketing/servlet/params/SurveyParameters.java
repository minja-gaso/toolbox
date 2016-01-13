package org.sw.marketing.servlet.params;

import javax.servlet.http.HttpServletRequest;

import org.sw.marketing.dao.DAOFactory;
import org.sw.marketing.dao.form.FormDAO;
import org.sw.marketing.data.form.Data.Form;

public class SurveyParameters
{
	public static Form process(HttpServletRequest request, Form form)
	{
		FormDAO formDAO = DAOFactory.getFormDAO();
		
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
			
			/*
			 * ensure prettyURL is unique - if not, prepend form ID
			 */
			Form tempForm = formDAO.getFormByPrettyUrl(prettyUrl);
			if(tempForm != null && tempForm.getId() != form.getId() && tempForm.getPrettyUrl().equals(prettyUrl))
			{
				form.setPrettyUrl(form.getId() + "-" + prettyUrl);
			}
			else
			{
				form.setPrettyUrl(prettyUrl);
			}
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
