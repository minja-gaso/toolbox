package org.sw.marketing.servlet.survey;

import java.io.IOException;
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
import org.sw.marketing.servlet.params.QuestionParameters;
import org.sw.marketing.transformation.TransformerHelper;
import org.sw.marketing.util.ReadFile;

/**
 * Servlet implementation class SurveyQuestionTypeTextServlet
 */
@WebServlet("/surveyQuestionTypeText")
public class SurveyQuestionTypeTextServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		@SuppressWarnings("unchecked")
		java.util.Map<String, String[]> parameterMap = (java.util.HashMap<String, String[]>) request.getAttribute("parameterMap");

		FormDAO formDAO = DAOFactory.getFormDAO();
		QuestionDAO questionDAO = DAOFactory.getQuestionDAO();
		Data data = null;
		Form form = null;
		Question question = null;
		
		String paramFormId = null;
		if(parameterMap.get("FORM_ID") != null)
		{
			paramFormId = parameterMap.get("FORM_ID")[0];
		}
		int id = Integer.parseInt(paramFormId);

		String paramQuestionId = null;
		if(parameterMap.get("QUESTION_ID") != null)
		{
			paramQuestionId = parameterMap.get("QUESTION_ID")[0];
		}
		int questionId = Integer.parseInt(paramQuestionId);
		
		String paramAction = null;
		if(parameterMap.get("ACTION") != null)
		{
			paramAction = parameterMap.get("ACTION")[0];
			if(paramAction.equals("SAVE_QUESTION"))
			{
				question = QuestionParameters.process(request, questionDAO.getQuestion(questionId));
				questionDAO.updateQuestion(question);
			}
		}
		
		data = new Data();		
		form = formDAO.getForm(id);
		if(question == null)
		{
			question = questionDAO.getQuestion(questionId);
		}
		form.getQuestion().add(question);
		data.getForm().add(form);
		
		String xmlStr = TransformerHelper.getXmlStr("org.sw.marketing.data.form", data);
		String htmlStr = TransformerHelper.getHtmlStr(xmlStr, getServletContext().getResourceAsStream("/question_type_text.xsl"));
		
		String toolboxSkinPath = getServletContext().getInitParameter("assetPath") + "toolbox.html";
		String skinHtmlStr = ReadFile.getSkin(toolboxSkinPath);
		skinHtmlStr = skinHtmlStr.replace("{NAME}", "Text");
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
