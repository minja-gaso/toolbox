package org.sw.marketing.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.sw.marketing.dao.skin.DAOFactory;
import org.sw.marketing.dao.skin.SkinDAO;
import org.sw.marketing.dao.skin.role.SkinRoleDAO;
import org.sw.marketing.dao.skin.user.UserDAO;
import org.sw.marketing.data.skin.Data;
import org.sw.marketing.transformation.TransformerHelper;
import org.sw.marketing.util.ReadFile;
import org.sw.marketing.util.SkinReader;
import org.sw.marketing.data.skin.Environment;
import org.sw.marketing.data.skin.Message;
import org.sw.marketing.data.skin.Skin;
import org.sw.marketing.data.skin.Skin.Role;
import org.sw.marketing.data.skin.User;
import org.sw.marketing.servlet.params.skin.SkinParameters;

public class SkinServiceController extends HttpServlet
{
	private static final long serialVersionUID = 1L;	
	//
	java.util.List<String> innerScreenList = new java.util.ArrayList<String>();
	
	public void init()
	{
		innerScreenList.add("GENERAL");
		innerScreenList.add("HTML");
		innerScreenList.add("CSS");
		innerScreenList.add("ROLES");
	}
	

	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		HttpSession httpSession = request.getSession();
		
		/*
		 * DAO Initialization
		 */
		UserDAO userDAO = DAOFactory.getUserDAO();
		SkinDAO skinDAO = DAOFactory.getSkinDAO();
		SkinRoleDAO roleDAO = DAOFactory.getRoleDAO();
		
		/*
		 * Data Initialization
		 */
		Data data = new Data();
		Environment environment = new Environment();
		java.util.List<Skin> skins = null;
		Skin skin = null;
		User user = null;
		Message message = null;
		
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
		 * Process Actions like Create, Update and Delete
		 */
		if(parameterMap.get("ACTION") != null)
		{
			String paramAction = parameterMap.get("ACTION")[0];
			if(skinID == 0)
			{
				if(paramAction.equals("CREATE_SKIN"))
				{
					skinID = skinDAO.createSkin(data);
					skin = skinDAO.getSkin(skinID);	
					
					message = new Message();
					message.setType("success");
					message.setLabel("The skin has been created.");
					data.getMessage().add(message);
				}
			}
			else
			{
				skin = skinDAO.getSkin(skinID);
				
				if(paramAction.equals("SAVE_SKIN"))
				{
					skin = SkinParameters.process(request, skin);
					if(skin.getSkinHtml().trim().length() == 0)
					{
						String skinHtmlStr = null;
						try
						{
							skinHtmlStr = SkinReader.getSkinByUrl(skin.getSkinUrl(), skin.getSkinSelector());
						}
						catch(MalformedURLException e)
						{
							e.printStackTrace();
						}
						catch(IOException e)
						{
							e.printStackTrace();
						}
						
						if(skinHtmlStr != null)
						{
							skin.setSkinHtml(skinHtmlStr);
						}
						else
						{
							message = new Message();
							message.setType("error");
							message.setLabel("There was an issue getting the skin.");
							data.getMessage().add(message);
						}
					}
					
					if(message == null)
					{
						skinDAO.updateSkin(skin);					
						message = new Message();
						message.setType("success");
						message.setLabel("The skin has been saved.");
						data.getMessage().add(message);
					}
				}
				else if(paramAction.equals("DELETE_SKIN"))
				{
					skinDAO.deleteSkin(skin.getId());
					
					message = new Message();
					message.setType("success");
					message.setLabel("The skin has been deleted.");
					data.getMessage().add(message);
					
					skinID = 0;
				}
				else if(paramAction.equals("ADD_ROLE"))
				{
					String paramRoleEmail = parameterMap.get("SKIN_ROLE_EMAIL")[0];
					String paramRoleType = parameterMap.get("SKIN_ROLE_TYPE")[0];
					
					Role role = new Role();
					role.setEmail(paramRoleEmail);
					role.setType(paramRoleType);
					role.setFkId(skinID);
					
					Role uniqueRole = roleDAO.getUniqueRole(role);
					if(uniqueRole == null)
					{
						roleDAO.insert(role);
						message = new Message();
						message.setType("success");
						message.setLabel("The user was successfully added.");
						data.getMessage().add(message);
					}
					else
					{
						message = new Message();
						message.setType("error");
						message.setLabel("The role/email combination already exists.");
						data.getMessage().add(message);
					}
				}
				else if(paramAction.equals("DELETE_ROLE"))
				{
					String paramRoleID = parameterMap.get("ROLE_ID")[0];
					long roleID = Long.parseLong(paramRoleID);
					roleDAO.delete(roleID);
					
					message = new Message();
					message.setType("success");
					message.setLabel("The user was successfully deleted.");
					data.getMessage().add(message);
				}
				else if(paramAction.equals("SAVE_APP_CSS"))
				{
					String paramAppCss = parameterMap.get("SKIN_APP_CSS")[0];
					String paramAppName = parameterMap.get("SKIN_APP_NAME")[0];

					skin.setSelectedApp(paramAppName);
					if(paramAppName.equals("CALENDAR"))
					{
						skin.setCalendarCss(paramAppCss);
					}
					if(paramAppName.equals("FORMS"))
					{
						skin.setFormCss(paramAppCss);
					}
					skinDAO.updateSkin(skin);		
					
					message = new Message();
					message.setType("success");
					message.setLabel("The CSS has been saved.");
					data.getMessage().add(message);
				}
			}
		}
		
		/*
		 * Determine which screen to display
		 */
		if(parameterMap.get("SCREEN") != null && skinID > 0)
		{
			String paramScreen = parameterMap.get("SCREEN")[0];
			
			if(innerScreenList.contains(paramScreen))
			{
				skin = skinDAO.getSkin(skinID);
			}
				
			if(paramScreen.equals("ROLES"))
			{
				java.util.List<Role> roles = roleDAO.getRoles(skinID);
				if(roles != null)
				{
					skin.getRole().addAll(roles);
				}
				
				xslScreen = "roles.xsl";
			}
			else if(paramScreen.equals("HTML"))
			{				
				xslScreen = "html.xsl";
			}
			else if(paramScreen.equals("CSS"))
			{				
				xslScreen = "css.xsl";
			}
			else if(paramScreen.equals("APP_CSS"))
			{				
				String paramAppName = parameterMap.get("SKIN_APP_NAME")[0];
				skin.setSelectedApp(paramAppName);
				
				xslScreen = "app_css.xsl";
			}
			else
			{
				xslScreen = "general.xsl";
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
