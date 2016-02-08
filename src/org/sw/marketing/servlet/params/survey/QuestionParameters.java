package org.sw.marketing.servlet.params.survey;

import javax.servlet.http.HttpServletRequest;

import org.sw.marketing.data.form.Data.Form.Question;

public class QuestionParameters
{
	public static Question process(HttpServletRequest request, Question question)
	{
		@SuppressWarnings("unchecked")
		java.util.Map<String, String[]> parameterMap = (java.util.HashMap<String, String[]>) request.getAttribute("parameterMap");

		if(parameterMap.get("QUESTION_TYPE") != null)
		{
			question.setType(parameterMap.get("QUESTION_TYPE")[0]);
		}
		if(parameterMap.get("QUESTION_HEADER") != null)
		{
			question.setHeader(parameterMap.get("QUESTION_HEADER")[0]);
		}
		if(parameterMap.get("QUESTION_LABEL") != null)
		{
			question.setLabel(parameterMap.get("QUESTION_LABEL")[0]);
		}
		if(parameterMap.get("QUESTION_DEFAULT_ANSWER") != null)
		{
			question.setDefaultAnswer(parameterMap.get("QUESTION_DEFAULT_ANSWER")[0]);
		}
		if(parameterMap.get("QUESTION_REQUIRED") != null)
		{
			question.setRequired(Boolean.parseBoolean(parameterMap.get("QUESTION_REQUIRED")[0]));
		}
		if(parameterMap.get("QUESTION_FILTER") != null)
		{
			question.setFilter(parameterMap.get("QUESTION_FILTER")[0]);
		}
		
		return question;
	}
}
