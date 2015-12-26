package org.sw.marketing.servlet.survey;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sw.marketing.dao.DAOFactory;
import org.sw.marketing.dao.form.FormDAO;
import org.sw.marketing.dao.question.QuestionDAO;
import org.sw.marketing.data.form.Data;
import org.sw.marketing.data.form.Data.Form;
import org.sw.marketing.data.form.Data.Form.Question;
import org.sw.marketing.transformation.TransformerHelper;
import org.sw.marketing.util.ReadFile;

/**
 * Servlet implementation class SurveyQuestionListServlet
 */
@WebServlet("/surveyQuestions")
public class SurveyQuestionListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		@SuppressWarnings("unchecked")
		java.util.Map<String, String[]> parameterMap = (java.util.HashMap<String, String[]>) request.getAttribute("parameterMap");

		FormDAO formDAO = DAOFactory.getFormDAO();
		QuestionDAO questionDAO = DAOFactory.getQuestionDAO();
		
		String paramFormId = null;
		if(parameterMap.get("FORM_ID") != null)
		{
			paramFormId = parameterMap.get("FORM_ID")[0];
		}
		else if(request.getAttribute("surveyId") != null)
		{
			paramFormId = (String) request.getAttribute("surveyId");
		}
		long id = Long.parseLong(paramFormId);
		
		if(parameterMap.get("ACTION") != null && !parameterMap.get("ACTION").equals(""))
		{			
			String paramAction = parameterMap.get("ACTION")[0];

			int questionId = 0;
			
			if(paramAction.equals("CREATE_QUESTION"))
			{
				int nextNumber = questionDAO.getNextNumber(id) + 1;
				int latestPage = questionDAO.getLatestPage(id);
				if(latestPage == 0)
				{
					latestPage = 1;
				}
				questionDAO.insertQuestion(nextNumber, latestPage, id);
			}
			else if(paramAction.equals("DELETE_QUESTION"))
			{
				questionId = Integer.parseInt(parameterMap.get("QUESTION_ID")[0]);
				Question question = questionDAO.getQuestion(questionId);
				int count = questionDAO.getQuestionPageCount(question.getPage());
				if(count == 1)
				{
					questionDAO.removePageBreak(question.getNumber(), id);
				}
				questionDAO.deleteQuestion(questionId);
				questionDAO.moveUpQuestions(question.getNumber(), id);
			}
			else if(paramAction.equals("INSERT_QUESTION"))
			{
				questionId = Integer.parseInt(parameterMap.get("QUESTION_ID")[0]);
				Question question = questionDAO.getQuestion(questionId);
				int questionNumber = question.getNumber();
				int lastQuestionNumber = questionDAO.getMostRecentQuestionNumber(id);
				if(questionNumber < lastQuestionNumber)
				{
					questionDAO.moveDownQuestions(questionNumber, id);
				}
				questionDAO.insertQuestion(question.getNumber() + 1, question.getPage(), id);
			}
			else if(paramAction.equals("INSERT_PAGE_BREAK"))
			{
				int questionNumber = Integer.parseInt(parameterMap.get("QUESTION_NUMBER")[0]);
				Question question = questionDAO.getQuestionByNumber(questionNumber);
				questionDAO.insertPageBreak(question.getNumber(), id);
			}
			else if(paramAction.equals("DELETE_PAGE_BREAK"))
			{
				int pageNumber = Integer.parseInt(parameterMap.get("PAGE_BREAK_ID")[0]);
				questionDAO.deletePageBreak(pageNumber);
			}
			/*
			 * Move a question up or down
			 */
			else if(paramAction.equals("SWAP_UP") || paramAction.equals("SWAP_DOWN"))
			{				
				int questionNumber = Integer.parseInt(parameterMap.get("QUESTION_NUMBER")[0]);
				Question question = questionDAO.getQuestionByNumber(questionNumber);
				Question secondaryQuestion = null;

				if(paramAction.equals("SWAP_UP"))
				{
					secondaryQuestion = questionDAO.getQuestionByNumber(question.getNumber() - 1);
					if(question.getPage() > secondaryQuestion.getPage())
					{
						question.setPage(question.getPage() - 1);
						secondaryQuestion.setPage(secondaryQuestion.getPage() + 1);
					}
					question.setNumber(question.getNumber() - 1);
					secondaryQuestion.setNumber(secondaryQuestion.getNumber() + 1);
				}
				else
				{
					secondaryQuestion = questionDAO.getQuestionByNumber(question.getNumber() + 1);
					if(question.getPage() < secondaryQuestion.getPage())
					{
						question.setPage(question.getPage() + 1);
						secondaryQuestion.setPage(secondaryQuestion.getPage() - 1);
					}
					question.setNumber(question.getNumber() + 1);
					secondaryQuestion.setNumber(secondaryQuestion.getNumber() - 1);
				}
				questionDAO.updateQuestion(question);
				questionDAO.updateQuestion(secondaryQuestion);
			}
		}
		
		Data data = new Data();		
		Form form = formDAO.getForm(id);
		java.util.List<Question> questionList = questionDAO.getQuestions(form.getId());
		if(questionList != null)
		{
			form.getQuestion().addAll(questionList);
		}
		data.getForm().add(form);
		
		String xmlStr = TransformerHelper.getXmlStr("org.sw.marketing.data.form", data);
		String htmlStr = TransformerHelper.getHtmlStr(xmlStr, getServletContext().getResourceAsStream("/question_list.xsl"));
		
		String toolboxSkinPath = getServletContext().getInitParameter("assetPath") + "toolbox.html";
		String skinHtmlStr = ReadFile.getSkin(toolboxSkinPath);
		skinHtmlStr = skinHtmlStr.replace("{NAME}", form.getTitle());
		skinHtmlStr = skinHtmlStr.replace("{CONTENT}", htmlStr);
		
		System.out.println(xmlStr);
		response.getWriter().print(skinHtmlStr);
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
