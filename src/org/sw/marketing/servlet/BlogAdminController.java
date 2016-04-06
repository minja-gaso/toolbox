package org.sw.marketing.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.sw.marketing.dao.blog.DAOFactory;
import org.sw.marketing.dao.blog.role.BlogRoleDAO;
import org.sw.marketing.dao.blog.skin.BlogSkinDAO;
import org.sw.marketing.dao.blog.user.UserDAO;
import org.sw.marketing.dao.blog.BlogDAO;
import org.sw.marketing.data.blog.Data.Blog;
import org.sw.marketing.data.blog.*;
import org.sw.marketing.data.blog.Role;
import org.sw.marketing.data.blog.Message;
import org.sw.marketing.data.blog.User;
import org.sw.marketing.servlet.params.blog.BlogParameters;
import org.sw.marketing.transformation.TransformerHelper;
import org.sw.marketing.util.ReadFile;

public class BlogAdminController extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	//
	java.util.List<String> innerScreenList = new java.util.ArrayList<String>();
	
	public void init()
	{
		innerScreenList.add("GENERAL");
		innerScreenList.add("APPEARANCE");
		innerScreenList.add("ROLES");
	}
	
	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		HttpSession httpSession = request.getSession();
		
		/*
		 * DAO Initialization
		 */
		UserDAO userDAO = DAOFactory.getUserDAO();
		BlogDAO blogDAO = DAOFactory.getBlogDAO();
		BlogRoleDAO roleDAO = DAOFactory.getBlogRoleDAO();
		BlogSkinDAO skinDAO = DAOFactory.getBlogSkinDAO();

		/*
		 * Data Initialization
		 */
		Data data = new Data();
		Environment environment = new Environment();
		java.util.List<Blog> blogs = null;
		Blog blog = null;
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
		long blogID = 0;
		if(request.getSession().getAttribute("BLOG_ADMIN_ID") != null)
		{
			blogID = (Long) request.getSession().getAttribute("BLOG_ADMIN_ID");
		}
		if(parameterMap.get("BLOG_ID") != null)
		{
			try
			{
				blogID = Long.parseLong(parameterMap.get("BLOG_ID")[0]);
			}
			catch(NumberFormatException e)
			{
				blogID = 0;
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
			if(blogID == 0)
			{
				if(paramAction.equals("CREATE_BLOG"))
				{
					blogID = blogDAO.createBlog(user);
					blog = blogDAO.getBlog(blogID);
				}
			}
			else
			{
				blog = blogDAO.getBlog(blogID);
				
				if(paramAction.equals("SAVE_BLOG"))
				{
					blog = BlogParameters.process(request, blog);
					
					Blog tempBlog = blogDAO.getBlogByPrettyUrl(blog.getPrettyUrl());
					if(tempBlog != null && tempBlog.getId() != blog.getId() && tempBlog.getPrettyUrl().equals(blog.getPrettyUrl()))
					{
						Message message = new Message();
						message.setType("error");
						message.setLabel("The pretty URL is already in use.  Please choose a unique one.");
						data.getMessage().add(message);
					}
					else if(parameterMap.get("BLOG_PRETTY_URL") != null && parameterMap.get("BLOG_PRETTY_URL")[0].trim().equals(""))
					{
						Message message = new Message();
						message.setType("error");
						message.setLabel("Please enter a pretty URL.");
						data.getMessage().add(message);
					}
					else
					{
						blogDAO.updateBlog(blog);
						
						Message message = new Message();
						message.setType("success");
						message.setLabel("The blog has been saved.");
						data.getMessage().add(message);
					}
				}
				else if(paramAction.equals("DELETE_BLOG"))
				{
					blogDAO.deleteBlog(blog.getId());
					
					Message message = new Message();
					message.setType("success");
					message.setLabel("The calendar has been deleted.");
					data.getMessage().add(message);
					
					blogID = 0;
				}
				else if(paramAction.equals("ADD_ROLE"))
				{
					String paramRoleEmail = parameterMap.get("BLOG_ROLE_EMAIL")[0];
					String paramRoleType = parameterMap.get("BLOG_ROLE_TYPE")[0];
					
					Role role = new Role();
					role.setEmail(paramRoleEmail);
					role.setType(paramRoleType);
					role.setFkId(blogID);
					
					Role uniqueRole = roleDAO.getUniqueRole(role);
					if(uniqueRole == null)
					{
						roleDAO.insert(role);
					}
					else
					{
						Message message = new Message();
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
				}
			}
		}
		
		/*
		 * Determine which screen to display
		 */
		if((parameterMap.get("SCREEN") != null || request.getSession().getAttribute("BLOG_ADMIN_SCREEN") != null)
				&& blogID > 0)
		{
			String paramScreen = null;
			if(parameterMap.get("SCREEN") != null)
			{
				paramScreen = parameterMap.get("SCREEN")[0];
			}
			else
			{
				paramScreen = (String) request.getSession().getAttribute("BLOG_ADMIN_SCREEN");
			}
				
			
			if(innerScreenList.contains(paramScreen))
			{
				blog = blogDAO.getBlog(blogID);
				
				request.getSession().setAttribute("BLOG_ADMIN_ID", blogID);
			}
				
			if(paramScreen.equals("ROLES"))
			{
				java.util.List<Role> roles = roleDAO.getRoles(blogID);
				if(roles != null)
				{
					blog.getRole().addAll(roles);
				}
				
				xslScreen = "roles.xsl";
			}
			else if(paramScreen.equals("SKIN"))
			{				
				java.util.List<Skin> skins = skinDAO.getSkins(user);
				if(skins != null)
				{
					data.getSkin().addAll(skins);
				}
				xslScreen = "skin.xsl";
			}
			else if(paramScreen.equals("CSS"))
			{				
				xslScreen = "css.xsl";
			}
			else
			{
				xslScreen = "general.xsl";
			}
			
			if(blog != null)
			{
				data.getBlog().add(blog);
			}
			
			request.getSession().setAttribute("BLOG_ADMIN_SCREEN", paramScreen);
		}
		else
		{
			xslScreen = "list.xsl";
			
			blogs = blogDAO.getBlogs(user);
			if(blogs != null)
			{
				data.getBlog().addAll(blogs);
			}
			
			request.getSession().removeAttribute("BLOG_ADMIN_ID");
			request.getSession().setAttribute("BLOG_ADMIN_SCREEN", "LIST");
		}
		
		environment.setComponentId(6);
		environment.setServerName(getBaseUrl(request));
		data.setEnvironment(environment);
		
		TransformerHelper transformerHelper = new TransformerHelper();
		transformerHelper.setUrlResolverBaseUrl(getServletContext().getInitParameter("blogAdminXslUrl"));
		
		String xmlStr = transformerHelper.getXmlStr("org.sw.marketing.data.blog", data);
		xslScreen = getServletContext().getInitParameter("blogAdminXslPath") + xslScreen;
		String xslStr = ReadFile.getSkin(xslScreen);
		String htmlStr = transformerHelper.getHtmlStr(xmlStr, new ByteArrayInputStream(xslStr.getBytes()));
		
		String toolboxSkinPath = getServletContext().getInitParameter("assetPath") + "toolbox.html";
		String skinHtmlStr = ReadFile.getSkin(toolboxSkinPath);
		skinHtmlStr = skinHtmlStr.replace("{NAME}", "List of Blogs");
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
