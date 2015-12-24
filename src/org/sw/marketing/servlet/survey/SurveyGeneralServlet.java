package org.sw.marketing.servlet.survey;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sw.marketing.dao.DAOFactory;
import org.sw.marketing.dao.form.FormDAO;
import org.sw.marketing.data.form.Data;
import org.sw.marketing.data.form.Data.Environment;
import org.sw.marketing.data.form.Data.Form;
import org.sw.marketing.servlet.params.QuestionParameters;
import org.sw.marketing.servlet.params.SurveyParameters;
import org.sw.marketing.transformation.TransformerHelper;
import org.sw.marketing.util.ReadFile;

/**
 * Servlet implementation class SurveyGeneralServlet
 */
@WebServlet("/surveyGeneral")
public class SurveyGeneralServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		@SuppressWarnings("unchecked")
		java.util.Map<String, String[]> parameterMap = (java.util.HashMap<String, String[]>) request.getAttribute("parameterMap");

		FormDAO formDAO = DAOFactory.getFormDAO();
		Data data = new Data();
		Form form = null;

		String paramFormId = null;
		int id = 0;
		if (request.getAttribute("surveyId") != null)
		{
			id = (int) request.getAttribute("surveyId");
		}
		else if (parameterMap.get("FORM_ID") != null)
		{
			paramFormId = parameterMap.get("FORM_ID")[0];
			id = Integer.parseInt(paramFormId);
		}
		form = formDAO.getForm(id);

		String paramAction = null;
		if (parameterMap.get("ACTION") != null)
		{
			paramAction = parameterMap.get("ACTION")[0];
			if (paramAction.equals("SAVE_FORM"))
			{
				form = SurveyParameters.process(request, formDAO.getForm(id));
				formDAO.updateForm(form);
			}
		}
		data.getForm().add(form);
		Environment environment = new Environment();
		environment.setContactEmail("minja.gaso@sw.org");
		environment.setContactName("Minja Gaso");
		environment.setServerName(getServerName(request));
		data.setEnvironment(environment);

		String xmlStr = TransformerHelper.getXmlStr("org.sw.marketing.data.form", data);
		String htmlStr = TransformerHelper.getHtmlStr(xmlStr, getServletContext().getResourceAsStream("/general.xsl"));

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

	public static String getServerName(HttpServletRequest request)
	{
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/";
	}
}
