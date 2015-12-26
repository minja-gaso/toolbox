package org.sw.marketing.servlet.survey;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.DayOfWeek;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.sw.marketing.dao.DAOFactory;
import org.sw.marketing.dao.form.FormDAO;
import org.sw.marketing.dao.user.UserDAO;
import org.sw.marketing.data.form.Data;
import org.sw.marketing.data.form.Data.Form;
import org.sw.marketing.data.form.Data.User;
import org.sw.marketing.transformation.TransformerHelper;
import org.sw.marketing.util.ReadFile;

@WebServlet("/surveyList")
public class SurveyListServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		FormDAO formDAO = DAOFactory.getFormDAO();
		UserDAO userDAO = DAOFactory.getUserDAO();
		
		Data data = new Data();		
		java.util.List<Form> formList = formDAO.getForms();
		if(formList != null)
		{
			data.getForm().addAll(formList);
		}
		
		HttpSession httpSession = (HttpSession) request.getSession();
		if(httpSession.getAttribute("user") != null)
		{
			User user = (User) httpSession.getAttribute("user");
			user = userDAO.getUserByEmail(user.getEmailAddress());
			data.setUser(user);
		}
		
		String xmlStr = TransformerHelper.getXmlStr("org.sw.marketing.data.form", data);
		String htmlStr = TransformerHelper.getHtmlStr(xmlStr, getServletContext().getResourceAsStream("/list.xsl"));
		
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
}
