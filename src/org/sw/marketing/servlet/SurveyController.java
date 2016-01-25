package org.sw.marketing.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.sw.marketing.dao.DAOFactory;
import org.sw.marketing.dao.answer.AnswerDAO;
import org.sw.marketing.dao.form.FormDAO;
import org.sw.marketing.dao.question.QuestionDAO;
import org.sw.marketing.dao.submission.SubmissionAnswerDAO;
import org.sw.marketing.dao.submission.SubmissionDAO;
import org.sw.marketing.dao.user.UserDAO;
import org.sw.marketing.data.form.Data;
import org.sw.marketing.data.form.Data.Environment;
import org.sw.marketing.data.form.Data.Form;
import org.sw.marketing.data.form.Data.Form.Question;
import org.sw.marketing.data.form.Data.Form.Question.PossibleAnswer;
import org.sw.marketing.data.form.Data.Message;
import org.sw.marketing.data.form.Data.Submission;
import org.sw.marketing.data.form.Data.Submission.Answer;
import org.sw.marketing.servlet.params.QuestionParameters;
import org.sw.marketing.servlet.params.SurveyParameters;
import org.sw.marketing.transformation.TransformerHelper;
import org.sw.marketing.util.ReadFile;
import org.sw.marketing.data.form.Data.User;

@WebServlet("/survey")
public class SurveyController extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	//
	java.util.List<String> innerScreenList = new java.util.ArrayList<String>();
//	protected static final String LIST_SCREEN = "/surveyList";
//	protected static final String CREATE_SCREEN = "/surveyCreate";
//	protected static final String GENERAL_SCREEN = "/surveyGeneral";
//	protected static final String QUESTION_LIST_SCREEN = "/surveyQuestions";
//	protected static final String QUESTION_TYPE_TEXT_SCREEN = "/surveyQuestionTypeText";
//	protected static final String QUESTION_TYPE_TEXTAREA_SCREEN = "/surveyQuestionTypeTextarea";
//	protected static final String QUESTION_TYPE_RADIO_SCREEN = "/surveyQuestionTypeRadio";
//	protected static final String QUESTION_TYPE_CHECKBOX_SCREEN = "/surveyQuestionTypeCheckbox";
	
	public void init()
	{
		innerScreenList.add("GENERAL");
		innerScreenList.add("QUESTION_LIST");
		innerScreenList.add("QUESTION_TYPE_TEXT");
		innerScreenList.add("QUESTION_TYPE_TEXTAREA");
		innerScreenList.add("QUESTION_TYPE_RADIO");
		innerScreenList.add("QUESTION_TYPE_CHECKBOX");
		innerScreenList.add("QUESTION_TYPE_PULLDOWN");
		innerScreenList.add("REPORTS");
		innerScreenList.add("ANALYTICS");
	}

	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		HttpSession httpSession = request.getSession();
		
		/*
		 * DAO Initialization
		 */
		UserDAO userDAO = DAOFactory.getUserDAO();
		FormDAO formDAO = DAOFactory.getFormDAO();
		QuestionDAO questionDAO = DAOFactory.getQuestionDAO();
		AnswerDAO answerDAO = DAOFactory.getPossibleAnswerDAO();
//		SubmissionDAO submissionDAO = DAOFactory.getSubmissionDAO();
//		SubmissionAnswerDAO submissionAnswerDAO = DAOFactory.getSubmissionAnswerDAO();
		
		/*
		 * Data Initialization
		 */
		Data data = new Data();
		java.util.List<Form> formList = null;
		Form form = null;
		java.util.List<Question> questionList = null;
		Question question = null;
		java.util.List<PossibleAnswer> possibleAnswerList = null;
//		java.util.List<Answer> answerList = null;
//		Answer answer = null;
//		java.util.List<Submission> submissionList = null;
//		Submission submission = null;
		User user = null;
		
		/*
		 * Get user session information
		 */
		if(httpSession.getAttribute("user") != null)
		{
			user = (User) httpSession.getAttribute("user");
			user = userDAO.getUserByEmail(user.getEmailAddress());
			data.setUser(user);
		}
		
		/*
		 * Add parameters to HashMap
		 */
		@SuppressWarnings("unchecked")
		java.util.Map<String, String[]> parameterMap = (java.util.HashMap<String, String[]>) request.getAttribute("parameterMap");
		
		/*
		 * Form ID
		 */
		String formIdStr = null;
		long formID = 0;
		if(parameterMap.get("FORM_ID") != null)
		{
			formIdStr = parameterMap.get("FORM_ID")[0];
			try
			{
				formID = Long.parseLong(formIdStr);
			}
			catch(NumberFormatException e)
			{
				formID = 0;
			}
		}
		else if (request.getAttribute("surveyId") != null)
		{
			formID = (long) request.getAttribute("surveyId");
			request.removeAttribute("surveyId");
		}
		
		/*
		 * Question ID
		 */
		String questionIdStr = null;
		Long questionID = null;
		if(parameterMap.get("QUESTION_ID") != null)
		{
			questionIdStr = parameterMap.get("QUESTION_ID")[0];
			try
			{
				questionID = Long.parseLong(questionIdStr);
			}
			catch(NumberFormatException e)
			{
				questionID = null;
			}
		}
		
		/*
		 * Screen filePath
		 */
		String xslScreen = null;
		
		/*
		 * Process Actions like Create, Update and Delete
		 */
		if(parameterMap.get("ACTION") != null)
		{
			String paramAction = parameterMap.get("ACTION")[0];
			if(paramAction.equals("CREATE_FORM"))
			{
				formID = formDAO.createForm(user);
				form = formDAO.getForm(formID);
			}
			else if(paramAction.equals("DELETE_FORM"))
			{				
				Message message = new Message();
				message.setType("error");
				message.setLabel("The form has been deleted.");
				data.getMessage().add(message);
				
				formDAO.deleteForm(formID);
				formID = 0;
			}
			else if(paramAction.equals("SAVE_FORM"))
			{
				form = SurveyParameters.process(request, formDAO.getForm(formID));
				
				Form tempForm = formDAO.getFormByPrettyUrl(form.getPrettyUrl());
				if(tempForm != null && tempForm.getId() != form.getId() && tempForm.getPrettyUrl().equals(form.getPrettyUrl()))
				{
					Message message = new Message();
					message.setType("error");
					message.setLabel("The pretty URL is already in use.  Please choose a unique one.");
					data.getMessage().add(message);
				}
				else if(parameterMap.get("FORM_PRETTY_URL") != null && parameterMap.get("FORM_PRETTY_URL")[0].trim().equals(""))
				{
					Message message = new Message();
					message.setType("error");
					message.setLabel("Please enter a pretty URL.");
					data.getMessage().add(message);
				}
				else
				{
					formDAO.updateForm(form);
					
					Message message = new Message();
					message.setType("success");
					message.setLabel("The form has been saved.");
					data.getMessage().add(message);
				}
			}
			else if(paramAction.equals("CREATE_QUESTION"))
			{
				int nextNumber = questionDAO.getNextNumber(formID) + 1;
				int latestPage = questionDAO.getLatestPage(formID);
				if(latestPage == 0)
				{
					latestPage = 1;
				}
				questionDAO.insertQuestion(nextNumber, latestPage, formID);
			}
			else if(paramAction.equals("INSERT_QUESTION"))
			{
				question = questionDAO.getQuestion(questionID);
				int questionNumber = question.getNumber();
				int lastQuestionNumber = questionDAO.getMostRecentQuestionNumber(formID);
				if(questionNumber < lastQuestionNumber)
				{
					questionDAO.moveDownQuestions(questionNumber, formID);
				}
				questionDAO.insertQuestion(question.getNumber() + 1, question.getPage(), formID);
			}
			else if(paramAction.equals("SAVE_QUESTION"))
			{
				question = QuestionParameters.process(request, questionDAO.getQuestion(questionID));
				questionDAO.updateQuestion(question);
				
				Message message = new Message();
				message.setType("success");
				message.setLabel("The question has been saved.");
				data.getMessage().add(message);
			}
			else if(paramAction.equals("DELETE_QUESTION"))
			{
				question = questionDAO.getQuestion(questionID);
				int count = questionDAO.getQuestionPageCount(question.getPage());
				if(count == 1)
				{
					questionDAO.removePageBreak(question.getNumber(), formID);
				}
				questionDAO.deleteQuestion(questionID);
				questionDAO.moveUpQuestions(question.getNumber(), formID);
				
				Message message = new Message();
				message.setType("success");
				message.setLabel("The question has been deleted.");
				data.getMessage().add(message);
			}
			else if(paramAction.equals("INSERT_PAGE_BREAK"))
			{
				int questionNumber = Integer.parseInt(parameterMap.get("QUESTION_NUMBER")[0]);
				question = questionDAO.getQuestionByNumber(questionNumber);
				questionDAO.insertPageBreak(question.getNumber(), formID);
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
				question = questionDAO.getQuestionByNumber(questionNumber);
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
			else if(parameterMap.get("ANSWER_ADD") != null && paramAction.equals("ADD_ANSWERS"))
			{
				/*
				 * process filter
				 */
				String filterStr = null;
				String paramFilter = parameterMap.get("ANSWER_ADD_FILTER")[0];
				if(paramFilter.equals("carriage"))
				{
					filterStr = "\r\n";				
				}
				
				/*
				 * process answers
				 */
				String paramAnswerToAddList = parameterMap.get("ANSWER_ADD")[0];
				paramAnswerToAddList = paramAnswerToAddList.trim();
				java.util.List<String> tempPossibleAnswerList = new java.util.ArrayList<String>();
				tempPossibleAnswerList.addAll(Arrays.asList(paramAnswerToAddList.split(filterStr)));
				
				java.util.Iterator<String> answerIter = tempPossibleAnswerList.iterator();
				PossibleAnswer possibleAnswer = null;
				while(answerIter.hasNext())
				{
					String answerStr = answerIter.next();
					possibleAnswer = new PossibleAnswer();
					possibleAnswer.setLabel(answerStr);
					answerDAO.insertAnswerToQuestion(questionID, possibleAnswer);
				}
				
				question = QuestionParameters.process(request, questionDAO.getQuestion(questionID));
				questionDAO.updateQuestion(question);
				
				Message message = new Message();
				message.setType("success");
				message.setLabel("The question and answer(s) have been been saved.");
				data.getMessage().add(message);
			}
			else if(paramAction.equals("DELETE_ANSWER"))
			{
				long answerID = Long.parseLong(parameterMap.get("ANSWER_ID")[0]);
				answerDAO.deleteAnswer(answerID);
			}
		}
		
		/*
		 * Determine which screen to display
		 */
		if(parameterMap.get("SCREEN") != null && formID > 0)
		{
			String paramScreen = parameterMap.get("SCREEN")[0];
			
			if(innerScreenList.contains(paramScreen))
			{
				form = formDAO.getForm(formID);
			}
				
			if(paramScreen.equals("GENERAL"))
			{
				xslScreen = "general.xsl";
			}
			else if(paramScreen.equals("QUESTION_LIST"))
			{
				questionList = questionDAO.getQuestions(formID);
				xslScreen = "question_list.xsl";
				if(questionList != null)
				{
					form.getQuestion().addAll(questionList);
				}
			}
			else if(paramScreen.equals("QUESTION_TYPE_TEXT") || paramScreen.equals("QUESTION_TYPE_TEXTAREA"))
			{
				question = questionDAO.getQuestion(questionID);
				if(paramScreen.equals("QUESTION_TYPE_TEXT"))
				{
					question.setType("text");
				}
				else if(paramScreen.equals("QUESTION_TYPE_TEXTAREA"))
				{
					question.setType("textarea");
				}
				questionDAO.updateQuestion(question);
				xslScreen = "question_type_standard.xsl";
				if(question != null)
				{
					form.getQuestion().add(question);
				}
			}
			else if(paramScreen.equals("QUESTION_TYPE_RADIO") || 
					paramScreen.equals("QUESTION_TYPE_CHECKBOX") || 
					paramScreen.equals("QUESTION_TYPE_PULLDOWN"))
			{
				question = questionDAO.getQuestion(questionID);
				if(paramScreen.equals("QUESTION_TYPE_RADIO"))
				{
					question.setType("radio");
				}
				else if(paramScreen.equals("QUESTION_TYPE_CHECKBOX"))
				{
					question.setType("checkbox");
				}
				else if(paramScreen.equals("QUESTION_TYPE_PULLDOWN"))
				{
					question.setType("pulldown");
				}
				questionDAO.updateQuestion(question);
				
				possibleAnswerList = answerDAO.getPossibleAnswers(questionID);
				if(possibleAnswerList != null)
				{
					question.getPossibleAnswer().addAll(possibleAnswerList);
				}
				
				xslScreen = "question_type_multiple_choice.xsl";
				if(question != null)
				{
					form.getQuestion().add(question);
				}
			}
			else if(paramScreen.equals("REPORTS"))
			{
				xslScreen = "reports.xsl";
			}
			else if(paramScreen.equals("ANALYTICS"))
			{
				xslScreen = "analytics.xsl";
			}
			else
			{
				xslScreen = "general.xsl";
			}
			
			if(form != null)
			{
				data.getForm().add(form);
			}
		}
		else
		{
			formList = formDAO.getForms(data);
			xslScreen = "list.xsl";
			if(formList != null)
			{
				data.getForm().addAll(formList);
			}
		}

		Environment environment = new Environment();
		environment.setComponentId(1);
		environment.setServerName(getBaseUrl(request));
		data.setEnvironment(environment);
		
		TransformerHelper transformerHelper = new TransformerHelper();
		transformerHelper.setUrlResolverBaseUrl(getServletContext().getInitParameter("assetXslFormsPath"));
		
		String xmlStr = transformerHelper.getXmlStr("org.sw.marketing.data.form", data);
		xslScreen = getServletContext().getInitParameter("assetXslFormsPath") + xslScreen;
		String xslStr = ReadFile.getSkin(xslScreen);
		String htmlStr = transformerHelper.getHtmlStr(xmlStr, new ByteArrayInputStream(xslStr.getBytes()));
		
		String toolboxSkinPath = getServletContext().getInitParameter("assetPath") + "toolbox.html";
		String skinHtmlStr = ReadFile.getSkin(toolboxSkinPath);
		skinHtmlStr = skinHtmlStr.replace("{NAME}", "List of Surveys");
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
	
	/**
	 * NOT UNIT TESTED Returns the base url (e.g, <tt>http://myhost:8080/myapp</tt>) suitable for
	 * using in a base tag or building reliable urls.
	 */
	public static String getBaseUrl(HttpServletRequest request) {
		if ((request.getServerPort() == 80) || (request.getServerPort() == 443))
		{
			return request.getScheme() + "://" + request.getServerName();	
		}
		else
		{

			return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		}
	}
}
