package org.sw.marketing.servlet.report.summary;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sw.marketing.dao.DAOFactory;
import org.sw.marketing.dao.form.FormDAO;
import org.sw.marketing.dao.form.answer.AnswerDAO;
import org.sw.marketing.dao.form.question.QuestionDAO;
import org.sw.marketing.dao.form.submission.SubmissionAnswerDAO;
import org.sw.marketing.dao.form.submission.SubmissionDAO;
import org.sw.marketing.dao.form.user.UserDAO;
import org.sw.marketing.data.form.Data;
import org.sw.marketing.data.form.Data.Form;
import org.sw.marketing.data.form.Data.Submission;
import org.sw.marketing.data.form.Data.Form.Question;
import org.sw.marketing.data.form.Data.Form.Question.PossibleAnswer;
import org.sw.marketing.data.form.Data.Submission.Answer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.opencsv.CSVWriter;

@WebServlet("/survey/csv/summary/*")
public class CSVGenerator extends HttpServlet {
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
		
		Data data = new Data();
		Form form = formDAO.getForm(formID);
		if(form == null)
		{
			response.getWriter().println("The form ID is invalid.");
			return;
		}
		java.util.List<Question> questions = questionDAO.getQuestions(formID);
		java.util.List<Submission> submissions = submissionDAO.getSubmissions(formID);	
		java.util.Map<Long, Long> ids = new java.util.LinkedHashMap<>();

		java.util.List<String> headers = new java.util.ArrayList<String>();
		if(questions != null)
		{
			for(Question question : questions)
			{
				String label = "[" + question.getNumber() + "] " + question.getLabel();
				
				/*
				 * if multiple choice
				 */
				java.util.List<PossibleAnswer> availableAnswers = answerDAO.getPossibleAnswers(question.getId());
				if(availableAnswers != null)
				{
					java.util.Iterator<PossibleAnswer> iter = availableAnswers.iterator();
					while(iter.hasNext())
					{
						PossibleAnswer possibleAnswer = iter.next();
						label = "[" + question.getNumber() + "] " + possibleAnswer.getLabel();
						headers.add(question.getNumber() + ". " + possibleAnswer.getLabel());
						ids.put(possibleAnswer.getId(), possibleAnswer.getId());	
					}
				}
				else
				{
					headers.add(question.getNumber() + ". " + question.getLabel());
					ids.put(question.getId(), question.getId());
				}		
			}
		}
		else
		{
			response.getWriter().println("There are no questions associated with this form/survey.");
			return;
		}
		

		StringWriter strWriter = new StringWriter();
		CSVWriter writer = new CSVWriter(strWriter, ',');
		writer.writeNext(headers.toArray(new String[headers.size()]));
		
		if(submissions != null)
		{
			for(Submission submission : submissions)
			{			
				String[] line = getSubmission(submission, ids);			
				writer.writeNext(line);
			}
		}
		
		byte[] csv = strWriter.toString().getBytes();
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=data.csv");
		response.setContentLength(csv.length);
		ServletOutputStream out = response.getOutputStream();
		out.write(csv);
		
		writer.close();
	}
	
	public String[] getSubmission(Submission submission, java.util.Map<Long, Long> ids)
	{
		SubmissionAnswerDAO submissionAnswerDAO = DAOFactory.getSubmissionAnswerDAO();
		
		java.util.List<String> line = new java.util.ArrayList<String>();
		for(Entry<Long, Long> entry : ids.entrySet())
		{
			long id = entry.getKey();
			Answer answer = submissionAnswerDAO.getSubmissionAnswer(submission.getId(), id);
			if(answer != null)
			{
				line.add(answer.getAnswerValue());
			}
			else
			{
				answer = submissionAnswerDAO.getSubmissionAnswerByValue(submission.getId(), id);
				if(answer != null)
				{
					line.add("T");
				}
				else
				{
					line.add("");
				}
			}
			
		}
		
		String[] lineArr = line.toArray(new String[line.size()]);
		
		return lineArr;		
	}
	
	protected ListMultimap<String, String> getFormFields(HttpServletRequest request)
	{
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
		
		return parameterMap;
	}
}
