package org.sw.marketing.servlet.report.summary;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.sw.marketing.dao.DAOFactory;
import org.sw.marketing.dao.form.FormDAO;
import org.sw.marketing.dao.form.answer.AnswerDAO;
import org.sw.marketing.dao.form.question.QuestionDAO;
import org.sw.marketing.dao.form.submission.SubmissionAnswerDAO;
import org.sw.marketing.dao.form.submission.SubmissionDAO;
import org.sw.marketing.dao.form.user.UserDAO;
import org.sw.marketing.data.form.Data;
import org.sw.marketing.data.form.Data.Analytics;
import org.sw.marketing.data.form.Data.Form;
import org.sw.marketing.data.form.Data.Form.Question;
import org.sw.marketing.data.form.Data.Form.Question.PossibleAnswer;
import org.sw.marketing.data.form.Data.Submission;
import org.sw.marketing.data.form.Data.Submission.Answer;
import org.sw.marketing.transformation.TransformerHelper;
import org.sw.marketing.util.ReadFile;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

@WebServlet("/survey/html/summary/*")
public class HTMLGenerator extends HttpServlet
{
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
		
		Data data = new Data();
		Form form = formDAO.getForm(formID);
		java.util.List<Question> questionList = questionDAO.getQuestions(formID);
		if(questionList != null)
		{
			for(Question question : questionList)
			{
				long questionID = question.getId();
				java.util.List<PossibleAnswer> answerList = answerDAO.getPossibleAnswers(questionID);
				if(answerList != null)
				{
					question.getPossibleAnswer().addAll(answerList);
				}
				form.getQuestion().add(question);
			}
//			form.getQuestion().addAll(questionList);
			
			java.util.List<Submission> submissionList = null;			
			if(startDateParam != null && endDateParam != null)
			{
				submissionList = submissionDAO.getSubmissionsFromStartToEndDate(formID, startDateParam, endDateParam);
				Analytics analytics = new Analytics();
				analytics.setStartDateStr(startDateParam);
				analytics.setEndDateStr(endDateParam);
				data.setAnalytics(analytics);
			}
			else
			{
				submissionList = submissionDAO.getSubmissions(formID);
			}
			
			if(submissionList != null)
			{
				for(Submission submission : submissionList)
				{
					java.util.List<Answer> submissionAnswerList = submissionAnswerDAO.getSubmissionAnswers(submission.getId());
					if(submissionAnswerList != null)
					{
						for(Answer answer : submissionAnswerList)
						{
							if(answer.isMultipleChoice())
							{
								long answerID = Long.parseLong(answer.getAnswerValue());
								String answerLabel = answerDAO.getPossibleAnswerLabel(answerID);
								answer.setAnswerLabel(answerLabel);
							}
						}
						submission.getAnswer().addAll(submissionAnswerList);
					}
					data.getSubmission().add(submission);
				}
			}
		}
		else
		{
			response.getWriter().println("There are no questions associated with this form/survey.");
			return;
		}
		data.getForm().add(form);

		TransformerHelper transformerHelper = new TransformerHelper();
		transformerHelper.setUrlResolverBaseUrl(getServletContext().getInitParameter("assetXslFormsPath"));
		
		String xmlStr = transformerHelper.getXmlStr("org.sw.marketing.data.form", data);
		StringWriter result = new StringWriter();
		StreamResult resultStream = new StreamResult(result);
		String htmlStr = null;
		
		try
		{	
			/*
			 * Prepare to transform XML with XSLT
			 */
			String htmlXsl = getServletContext().getInitParameter("assetPath") + "xsl/html/summary.xsl";
			Source htmlXslSource = new StreamSource(new File(htmlXsl));
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer(htmlXslSource);

			/*
			 * Prepare XML input
			 */
			Source src = new StreamSource(new StringReader(xmlStr));

			/*
			 * Transform
			 */
			transformer.transform(src, resultStream);
			htmlStr = result.toString();

			String toolboxSkinPath = getServletContext().getInitParameter("assetPath") + "toolbox_1col.html";
			String skinHtmlStr = ReadFile.getSkin(toolboxSkinPath);
			skinHtmlStr = skinHtmlStr.replace("{NAME}", form.getTitle());
			skinHtmlStr = skinHtmlStr.replace("{CONTENT}", htmlStr);
			
			/*
			 * Display PDF in browser
			 */
			System.out.println(xmlStr);
			response.setContentType("text/html");
			response.getWriter().println(skinHtmlStr);
		}
		catch (TransformerConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (TransformerException e)
		{
			e.printStackTrace();
		}
	}
}