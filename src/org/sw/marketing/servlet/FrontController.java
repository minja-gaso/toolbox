package org.sw.marketing.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "FrontController", urlPatterns =
{ "/controller" })
public class FrontController extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FrontController()
	{
		super();
	}

	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html");
		
		/*
		 * process all fields
		 */
		java.util.Map<String, String[]> parameterMap = new java.util.HashMap<String, String[]>();
		java.util.Enumeration<String> parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements())
		{
			String parameterName = (String) parameterNames.nextElement();
			String[] parameterValue = request.getParameterValues(parameterName);
			parameterMap.put(parameterName, parameterValue);
		}
		
		request.setAttribute("parameterMap", parameterMap);
		
		String paramComponentId = request.getParameter("COMPONENT_ID");
		if(paramComponentId == null)
		{
			request.getRequestDispatcher("/survey").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		process(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		process(request, response);
	}

}
