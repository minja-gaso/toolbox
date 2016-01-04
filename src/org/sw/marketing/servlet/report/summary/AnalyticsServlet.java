package org.sw.marketing.servlet.report.summary;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sw.marketing.dao.DAOFactory;
import org.sw.marketing.dao.answer.AnswerDAO;
import org.sw.marketing.dao.form.FormDAO;
import org.sw.marketing.dao.question.QuestionDAO;
import org.sw.marketing.dao.submission.SubmissionAnswerDAO;
import org.sw.marketing.dao.submission.SubmissionDAO;
import org.sw.marketing.dao.user.UserDAO;
import org.sw.marketing.data.form.Data;
import org.sw.marketing.data.form.Data.Form;
import org.sw.marketing.data.form.Data.Submission;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

@WebServlet("/analytics/*")
public class AnalyticsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		/*
		 * DAO Initialization
		 */
		UserDAO userDAO = DAOFactory.getUserDAO();
		FormDAO formDAO = DAOFactory.getFormDAO();
		QuestionDAO questionDAO = DAOFactory.getQuestionDAO();
		AnswerDAO answerDAO = DAOFactory.getPossibleAnswerDAO();
		SubmissionDAO submissionDAO = DAOFactory.getSubmissionDAO();
		SubmissionAnswerDAO submissionAnswerDAO = DAOFactory.getSubmissionAnswerDAO();
		
		/*
		 * Get parameters
		 */
		ListMultimap<String, String> parameterMap = ArrayListMultimap.create();
		java.util.Enumeration<String> parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements())
		{
			String parameterName = (String) parameterNames.nextElement();
			String[] parameterValue = request.getParameterValues(parameterName);
			for(int index = 0; index < parameterValue.length; index++)
			{
				parameterMap.put(parameterName, parameterValue[index]);
			}
		}
		
		/*
		 * Form ID param
		 */
		long formID = 0;
		try
		{
			if(parameterMap.get("FORM_ID") != null && parameterMap.get("FORM_ID").size() > 0)
			{
				formID = Long.parseLong(parameterMap.get("FORM_ID").get(0));
			}
			else
			{
				String pathInfo = request.getPathInfo().substring(1);
				if(pathInfo != null && !pathInfo.equals(""))
				{
					formID = Long.parseLong(request.getPathInfo().substring(1));
				}
			}
		}
		catch(NumberFormatException e)
		{
			e.printStackTrace();
		}
		
		if(formID > 0)
		{
			String startDateParam = null;
			if(parameterMap.get("START_DATE") != null && parameterMap.get("START_DATE").size() > 0)
			{
				startDateParam = parameterMap.get("START_DATE").get(0);
			}
			
			String endDateParam = null;
			if(parameterMap.get("END_DATE") != null && parameterMap.get("END_DATE").size() > 0)
			{
				endDateParam = parameterMap.get("END_DATE").get(0);
			}
			
			java.util.List<Submission> submissionList = null;
			if(startDateParam != null && endDateParam != null)
			{
				submissionList = submissionDAO.getSubmissionsFromStartToEndDate(formID, startDateParam, endDateParam);
			}
			else
			{
				submissionList = submissionDAO.getSubmissions(formID);
			}
			
			Data data = new Data();
			Form form = formDAO.getForm(formID);
			
		}
		
		response.getWriter().print("Sorry, no FORM ID was specified.  Please review the URL.");
	}
}
