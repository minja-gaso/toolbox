package org.sw.marketing.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.sw.marketing.dao.DAOFactory;
import org.sw.marketing.dao.answer.AnswerDAO;
import org.sw.marketing.dao.form.FormDAO;
import org.sw.marketing.dao.question.QuestionDAO;
import org.sw.marketing.dao.submission.SubmissionAnswerDAO;
import org.sw.marketing.dao.submission.SubmissionDAO;
import org.sw.marketing.dao.user.UserDAO;
import org.sw.marketing.data.form.Data;
import org.sw.marketing.data.form.Data.Form;
import org.sw.marketing.data.form.Data.Form.Question;
import org.sw.marketing.data.form.Data.Form.Question.PossibleAnswer;
import org.sw.marketing.data.form.Data.Submission;
import org.sw.marketing.data.form.Data.Submission.Answer;
import org.sw.marketing.transformation.TransformerHelper;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

@WebServlet("/pdf/survey/*")
public class PDFGenerator extends HttpServlet
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
				formID = Long.parseLong(request.getPathInfo().substring(1));
			}
		}
		catch(NumberFormatException e)
		{
			e.printStackTrace();
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
			
			java.util.List<Submission> submissionList = submissionDAO.getSubmissions(form.getId());
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
		data.getForm().add(form);
		
		String xmlStr = TransformerHelper.getXmlStr("org.sw.marketing.data.form", data);
		
		FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
		try
		{
			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, byteArrayOutputStream);
			
			String pdfXsl = getServletContext().getInitParameter("assetPath") + "xsl/pdf/summary.xsl";
			Source xsltSrc = new StreamSource(new File(pdfXsl));
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(xsltSrc);

			//Make sure the XSL transformation's result is piped through to FOP
			Result res = new SAXResult(fop.getDefaultHandler());

			//Setup input
			Source src = new StreamSource(new StringReader(xmlStr));

			//Start the transformation and rendering process
			transformer.transform(src, res);

			//Prepare response
			response.setContentType("application/pdf");
			response.setContentLength(byteArrayOutputStream.size());
			
			byte[] bytes = byteArrayOutputStream.toByteArray();
			
			src = new StreamSource(new ByteArrayInputStream(bytes));
			res = new SAXResult(fop.getDefaultHandler());
			
			System.out.println(xmlStr);

			//Send content to Browser
			response.getOutputStream().write(byteArrayOutputStream.toByteArray());
//			response.getOutputStream().flush();
		}
		catch (FOPException e)
		{
			e.printStackTrace();
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
