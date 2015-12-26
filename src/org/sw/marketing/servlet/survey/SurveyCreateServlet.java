package org.sw.marketing.servlet.survey;

import java.io.IOException;
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

@WebServlet("/surveyCreate")
public class SurveyCreateServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		@SuppressWarnings("unchecked")
		java.util.Map<String, String[]> parameterMap = (java.util.HashMap<String, String[]>) request.getAttribute("parameterMap");

		FormDAO formDAO = DAOFactory.getFormDAO();
		UserDAO userDAO = DAOFactory.getUserDAO();
		HttpSession httpSession = (HttpSession) request.getSession();

		Data data = new Data();
		if(httpSession.getAttribute("user") != null)
		{
			User user = (User) httpSession.getAttribute("user");
			user = userDAO.getUserByEmail(user.getEmailAddress());
			data.setUser(user);
		}
		//
		String paramScreen = parameterMap.get("SCREEN")[0].trim();
		String paramAction = parameterMap.get("ACTION")[0].trim();
		if (paramAction.equals("CREATE_FORM"))
		{
			long id = formDAO.createForm(data.getUser());
			if (id > 0)
			{
				request.setAttribute("surveyId", id);
				request.getRequestDispatcher("/surveyGeneral").forward(request, response);
			}
		}

		String xmlStr = TransformerHelper.getXmlStr("org.sw.marketing.data.form", data);
		String htmlStr = TransformerHelper.getHtmlStr(xmlStr, getServletContext().getResourceAsStream("/create.xsl"));

		String toolboxSkinPath = getServletContext().getInitParameter("assetPath") + "toolbox.html";
		String skinHtmlStr = ReadFile.getSkin(toolboxSkinPath);
		skinHtmlStr = skinHtmlStr.replace("{NAME}", "Create New Survey");
		skinHtmlStr = skinHtmlStr.replace("{CONTENT}", htmlStr);

		// System.out.println(xmlStr);
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
