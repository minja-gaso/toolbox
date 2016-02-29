package org.sw.marketing.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.sw.marketing.dao.skin.DAOFactory;
import org.sw.marketing.dao.skin.SkinDAO;
import org.sw.marketing.dao.skin.user.UserDAO;
import org.sw.marketing.data.skin.Data;
import org.sw.marketing.data.skin.Data.Skin;
import org.sw.marketing.transformation.TransformerHelper;
import org.sw.marketing.util.ReadFile;
import org.sw.marketing.data.skin.Environment;
import org.sw.marketing.data.skin.User;

public class SkinServiceController extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		HttpSession httpSession = request.getSession();
		
		/*
		 * DAO Initialization
		 */
		UserDAO userDAO = DAOFactory.getUserDAO();
		SkinDAO skinDAO = DAOFactory.getSkinDAO();
		
		/*
		 * Data Initialization
		 */
		Data data = new Data();
		Environment environment = new Environment();
		java.util.List<Skin> skins = null;
		Skin skin = null;
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
		 * Calendar ID
		 */
		long skinID = 0;
		if(parameterMap.get("SKIN_ID") != null)
		{
			try
			{
				skinID = Long.parseLong(parameterMap.get("SKIN_ID")[0]);
			}
			catch(NumberFormatException e)
			{
				skinID = 0;
			}
		}
		
		/*
		 * Screen filePath
		 */
		String xslScreen = null;
		
		/*
		 * Determine which screen to display
		 */
		if(parameterMap.get("SCREEN") != null && skinID > 0)
		{
			String paramScreen = parameterMap.get("SCREEN")[0];
			
//			if(innerScreenList.contains(paramScreen))
//			{
//				skin = skinDAO.getSkin(skinID);
//			}
				
			if(paramScreen.equals("ROLES"))
			{
//				java.util.List<Role> roles = roleDAO.getRoles(skinID);
//				if(roles != null)
//				{
//					skin.getRole().addAll(roles);
//				}
				
				xslScreen = "calendar_roles.xsl";
			}
			else if(paramScreen.equals("CSS"))
			{				
				xslScreen = "calendar_css.xsl";
			}
			else
			{
				xslScreen = "calendar_general.xsl";
			}
			
			if(skin != null)
			{
				data.getSkin().add(skin);
			}
		}
		else
		{
			xslScreen = "list.xsl";
			
			skins = skinDAO.getSkins(data);
			if(skins != null)
			{
				data.getSkin().addAll(skins);
			}
		}
		
		environment.setComponentId(5);
//		environment.setServerName(getBaseUrl(request));
		data.setEnvironment(environment);
		
		TransformerHelper transformerHelper = new TransformerHelper();
		transformerHelper.setUrlResolverBaseUrl(getServletConfig().getInitParameter("xslUrl"));
		
		String xmlStr = transformerHelper.getXmlStr("org.sw.marketing.data.skin", data);
		xslScreen = getServletConfig().getInitParameter("xslPath") + xslScreen;
		String xslStr = ReadFile.getSkin(xslScreen);
		String htmlStr = transformerHelper.getHtmlStr(xmlStr, new ByteArrayInputStream(xslStr.getBytes()));
		
		String toolboxSkinPath = getServletContext().getInitParameter("assetPath") + "toolbox.html";
		String skinHtmlStr = ReadFile.getSkin(toolboxSkinPath);
		skinHtmlStr = skinHtmlStr.replace("{NAME}", "List of Skins");
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
