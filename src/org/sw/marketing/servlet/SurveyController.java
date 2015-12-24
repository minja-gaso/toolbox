package org.sw.marketing.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sw.marketing.dao.DAOFactory;
import org.sw.marketing.dao.form.FormDAO;

@WebServlet("/survey")
public class SurveyController extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	protected static final String LIST_SCREEN = "/surveyList";
	protected static final String CREATE_SCREEN = "/surveyCreate";
	protected static final String GENERAL_SCREEN = "/surveyGeneral";
	protected static final String QUESTION_LIST_SCREEN = "/surveyQuestions";
	protected static final String QUESTION_TYPE_TEXT_SCREEN = "/surveyQuestionTypeText";
	protected static final String QUESTION_TYPE_TEXTAREA_SCREEN = "/surveyQuestionTypeTextarea";
	protected static final String QUESTION_TYPE_RADIO_SCREEN = "/surveyQuestionTypeRadio";
	protected static final String QUESTION_TYPE_CHECKBOX_SCREEN = "/surveyQuestionTypeCheckbox";

	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		@SuppressWarnings("unchecked")
		java.util.Map<String, String[]> parameterMap = (java.util.HashMap<String, String[]>) request.getAttribute("parameterMap");
		
		if(parameterMap.get("ACTION") != null)
		{
			String paramAction = parameterMap.get("ACTION")[0];
			if(paramAction.equals("CREATE_FORM"))
			{
				request.getRequestDispatcher(CREATE_SCREEN).forward(request, response);
				return;
			}
			else if(paramAction.equals("DELETE_FORM"))
			{
				FormDAO formDAO = DAOFactory.getFormDAO();
				int formId = Integer.parseInt(parameterMap.get("FORM_ID")[0]);
				formDAO.deleteForm(formId);
			}
		}
		if(parameterMap.get("SCREEN") != null)
		{
			String paramScreen = parameterMap.get("SCREEN")[0];
			if(paramScreen.equals("GENERAL"))
			{
				request.getRequestDispatcher(GENERAL_SCREEN).forward(request, response);
			}
			else if(paramScreen.equals("QUESTION_LIST"))
			{
				request.getRequestDispatcher(QUESTION_LIST_SCREEN).forward(request, response);
			}
			else if(paramScreen.equals("QUESTION_TYPE_TEXT"))
			{
				request.getRequestDispatcher(QUESTION_TYPE_TEXT_SCREEN).forward(request, response);
			}
			else if(paramScreen.equals("QUESTION_TYPE_TEXTAREA"))
			{
				request.getRequestDispatcher(QUESTION_TYPE_TEXTAREA_SCREEN).forward(request, response);
			}
			else if(paramScreen.equals("QUESTION_TYPE_RADIO"))
			{
				request.getRequestDispatcher(QUESTION_TYPE_RADIO_SCREEN).forward(request, response);
			}
			else if(paramScreen.equals("QUESTION_TYPE_CHECKBOX"))
			{
				request.getRequestDispatcher(QUESTION_TYPE_CHECKBOX_SCREEN).forward(request, response);
			}
			else
			{
				request.getRequestDispatcher(LIST_SCREEN).forward(request, response);
			}
		}
		else
		{
			request.getRequestDispatcher(LIST_SCREEN).forward(request, response);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		process(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		process(request, response);
	}
}
