package org.sw.marketing.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.sw.marketing.dao.DAOFactory;
import org.sw.marketing.dao.form.FormDAO;
import org.sw.marketing.dao.form.answer.AnswerDAO;
import org.sw.marketing.dao.form.question.QuestionDAO;
import org.sw.marketing.dao.form.score.ScoreDAO;
import org.sw.marketing.dao.form.skin.FormSkinDAO;
import org.sw.marketing.dao.form.submission.SubmissionDAO;
import org.sw.marketing.dao.form.user.UserDAO;
import org.sw.marketing.data.form.Data;
import org.sw.marketing.data.form.Data.Form;
import org.sw.marketing.data.form.Data.Form.Question;
import org.sw.marketing.data.form.Data.Form.Question.PossibleAnswer;
import org.sw.marketing.data.form.Data.Score;
import org.sw.marketing.data.form.Data.Submission;
import org.sw.marketing.data.form.Environment;
import org.sw.marketing.data.form.Message;
import org.sw.marketing.data.form.Skin;
import org.sw.marketing.data.form.User;
import org.sw.marketing.servlet.params.survey.ScoreParameters;
import org.sw.marketing.servlet.params.survey.SurveyParameters;
import org.sw.marketing.transformation.TransformerHelper;
import org.sw.marketing.util.ReadFile;

public class SelfAssessmentController extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	java.util.List<String> innerScreenList = new java.util.ArrayList<String>();
	
	public void init()
	{
		innerScreenList.add("GENERAL");
		innerScreenList.add("QUESTIONS_AND_ANSWERS");
		innerScreenList.add("ANSWERS");
		innerScreenList.add("SCORES");
		innerScreenList.add("EDIT_SCORE");
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
		ScoreDAO scoreDAO = DAOFactory.getScoreDAO();
		SubmissionDAO submissionDAO = DAOFactory.getSubmissionDAO();
		
		/*
		 * Data Initialization
		 */
		Data data = new Data();
		java.util.List<Form> formList = null;
		Form form = null;
		java.util.List<Question> questionList = null;		
		Question question = null;
		java.util.List<Data.PossibleAnswer> possibleAnswers = null;				
		java.util.List<Score> scores = null;
		Score score = null;
		User user = null;
		
		/*
		 * Get user session information
		 */
		if(httpSession.getAttribute("EMAIL_ADDRESS") != null)
		{
			String email = (String) httpSession.getAttribute("EMAIL_ADDRESS");
			user = userDAO.getUserByEmail(email);
			if(user == null)
			{
				response.getWriter().println("Email not recognized.");
				return;
			}
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
			formID = (Long) request.getAttribute("surveyId");
			request.removeAttribute("surveyId");
		}
		if(formID > 0)
		{
			form = formDAO.getForm(formID);
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
		 * Score ID
		 */
		String scoreIdStr = null;
		long scoreID = 0;
		if(parameterMap.get("SCORE_ID") != null)
		{
			scoreIdStr = parameterMap.get("SCORE_ID")[0];
			try
			{
				scoreID = Long.parseLong(scoreIdStr);
			}
			catch(NumberFormatException e)
			{
				scoreID = 0;
			}
		}
		if(scoreID > 0)
		{
			score = scoreDAO.getScore(scoreID);
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
				formID = formDAO.createFormSelfAssessment(user);
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
				form = SurveyParameters.process(request, form);
				formDAO.updateForm(form);
			}
			else if(paramAction.equals("CREATE_QUESTION"))
			{
				int nextNumber = questionDAO.getNextNumber(formID) + 1;
				int latestPage = questionDAO.getLatestPage(formID);
				if(latestPage == 0)
				{
					latestPage = 1;
				}
				questionDAO.insertQuestionSelfAssessment(nextNumber, latestPage, formID);
				
				Message message = new Message();
				message.setType("success");
				message.setLabel("The question has been added.");
				data.getMessage().add(message);
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
				questionDAO.insertQuestionSelfAssessment(question.getNumber() + 1, question.getPage(), formID);
				
				Message message = new Message();
				message.setType("success");
				message.setLabel("The question has been added.");
				data.getMessage().add(message);
			}
			else if(paramAction.equals("SAVE_QUESTIONS"))
			{
				for(Entry<String, String[]> entry : parameterMap.entrySet())
				{
					String key = entry.getKey();
					String[] values = entry.getValue();
					
					if(key.contains("QUESTION_ENTRY_"))
					{
						questionID = Long.parseLong(key.split("QUESTION_ENTRY_")[1]);
						String value = values[0];
						
						question = questionDAO.getQuestion(questionID);
						question.setLabel(value);
						questionDAO.updateQuestion(question);
					}
				}
			}
			else if(paramAction.equals("SAVE_ANSWERS"))
			{
				for(Entry<String, String[]> entry : parameterMap.entrySet())
				{
					String key = entry.getKey();
					String[] values = entry.getValue();
					
					if(key.contains("ANSWER_ENTRY_"))
					{
						long answerID = Long.parseLong(key.split("ANSWER_ENTRY_")[1]);
						String labelStr = values[0];
						
						PossibleAnswer answer = answerDAO.getPossibleAnswerForForm(answerID);
						answer.setLabel(labelStr);
						
						String valueStr = request.getParameter("ANSWER_VALUE_" + answerID);
						if(valueStr != null)
						{
							int valueInt = Integer.parseInt(valueStr);
							answer.setValue(valueInt);
						}
						answerDAO.updatePossibleAnswer(answer);
					}
				}
			}
			else if(paramAction.equals("DELETE_QUESTION"))
			{
				java.util.List<Submission> submissions = submissionDAO.getSubmissions(formID);
				if(submissions != null)
				{
					Message message = new Message();
					message.setType("error");
					message.setLabel("You cannot delete questions that have submissions. Consider creating a new self-assessment.");
					data.getMessage().add(message);
				}
				else
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
					String[] answerArr = answerStr.split(",");
					possibleAnswer = new PossibleAnswer();
					possibleAnswer.setLabel(answerArr[0].trim());
					int value = 0;
					if(answerArr.length > 1)
					{
						try
						{
							value = Integer.parseInt(answerArr[1].trim());
						}
						catch(NumberFormatException e)
						{
							value = 0;
						}
					}
					possibleAnswer.setValue(value);
					answerDAO.insertAnswerToForm(formID, possibleAnswer);
				}
				
				Message message = new Message();
				message.setType("success");
				message.setLabel("The answer(s) have been been saved.");
				data.getMessage().add(message);
			}
			else if(paramAction.equals("DELETE_ANSWER"))
			{
				long answerID = Long.parseLong(parameterMap.get("ANSWER_ID")[0]);
				answerDAO.deleteAnswerForForm(answerID);
			}
			else if(paramAction.equals("CREATE_SCORE"))
			{
				scoreID = scoreDAO.insertScore(formID);
				score = scoreDAO.getScore(scoreID);
			}
			else if(paramAction.equals("SAVE_SCORE"))
			{
				score = ScoreParameters.process(request, score);
				scoreDAO.updateScore(score);
				
				String beginScoreStr = parameterMap.get("SCORE_BEGIN")[0];
				int beginScore = Integer.parseInt(beginScoreStr);
				String endScoreStr = parameterMap.get("SCORE_END")[0];
				int endScore = Integer.parseInt(endScoreStr);
				
				if(beginScore > endScore)
				{
					Message message = new Message();
					message.setType("error");
					message.setLabel("The low score must be smaller than or equal to the high score.  The values have been swapped.");
					data.getMessage().add(message);
				}
				else
				{
					Message message = new Message();
					message.setType("success");
					message.setLabel("The score entry has been saved.");
					data.getMessage().add(message);
				}
			}
			else if(paramAction.equals("DELETE_SCORE"))
			{
				scoreDAO.deleteScore(scoreID);
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
				
			if(paramScreen.equals("QUESTIONS_AND_ANSWERS"))
			{
				xslScreen = "question_and_answer_list.xsl";
			}			
			else if(paramScreen.equals("ANSWERS"))
			{
				xslScreen = "answer_list.xsl";
			}		
			else if(paramScreen.equals("SCORES"))
			{				
				scores = scoreDAO.getScores(formID);
				if(scores != null)					
				{
					data.getScore().addAll(scores);
				}
				xslScreen = "score_list.xsl";
			}	
			else if(paramScreen.equals("EDIT_SCORE"))
			{	
				score = scoreDAO.getScore(scoreID);
				if(score != null)					
				{
					data.getScore().add(score);
				}			
				
				xslScreen = "score_edit.xsl";
			}
			else if(paramScreen.equals("REPORTS"))
			{
				xslScreen = "form_reports.xsl";
			}
			else if(paramScreen.equals("ANALYTICS"))
			{
				xslScreen = "form_analytics.xsl";
			}
			else
			{
				FormSkinDAO skinDAO = DAOFactory.getFormSkinDAO();
				java.util.List<Skin> skins = skinDAO.getSkins(user);
				if(skins != null)
				{
					data.getSkin().addAll(skins);
				}
				xslScreen = "form_general.xsl";
			}
			
			if(form != null)
			{
				questionList = questionDAO.getQuestions(formID);
				if(questionList != null)
				{
					form.getQuestion().addAll(questionList);
				}
				
				possibleAnswers = answerDAO.getPossibleAnswersByForm(formID);
				if(possibleAnswers != null)
				{
					data.getPossibleAnswer().addAll(possibleAnswers);
				}
				
				data.getForm().add(form);
			}
		}
		else
		{
			formList = formDAO.getFormsSelfAssessment(data);
			xslScreen = "form_list.xsl";
			if(formList != null)
			{
				data.getForm().addAll(formList);
			}
		}

		Environment environment = new Environment();
		environment.setComponentId(2);
		environment.setServerName(getBaseUrl(request));
		data.setEnvironment(environment);
		
		TransformerHelper transformerHelper = new TransformerHelper();
		transformerHelper.setUrlResolverBaseUrl(getServletConfig().getInitParameter("xslUrl"));
		
		String xmlStr = transformerHelper.getXmlStr("org.sw.marketing.data.form", data);
		xslScreen = getServletConfig().getInitParameter("xslPath") + xslScreen;
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

