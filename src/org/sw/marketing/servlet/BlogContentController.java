package org.sw.marketing.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.sw.marketing.dao.blog.BlogDAO;
import org.sw.marketing.dao.blog.topic.BlogTopicDAO;
import org.sw.marketing.dao.blog.topic.BlogTopicTagDAO;
import org.sw.marketing.dao.blog.DAOFactory;
import org.sw.marketing.dao.blog.file.BlogTopicFileDAO;
import org.sw.marketing.dao.blog.user.UserDAO;
import org.sw.marketing.data.blog.Data;
import org.sw.marketing.data.blog.Data.Blog;
import org.sw.marketing.data.blog.Data.Blog.Topic;
import org.sw.marketing.data.blog.Data.Blog.Topic.File;
import org.sw.marketing.data.blog.Data.Blog.Topic.Tag;
import org.sw.marketing.data.blog.Environment;
import org.sw.marketing.data.blog.Message;
import org.sw.marketing.data.blog.User;
import org.sw.marketing.servlet.params.blog.BlogTopicParameters;
import org.sw.marketing.transformation.TransformerHelper;
import org.sw.marketing.util.ReadFile;

public class BlogContentController extends HttpServlet
{
	private static final long serialVersionUID = 1L;	
	//
	java.util.List<String> innerScreenList = new java.util.ArrayList<String>();
	
	public void init()
	{
		innerScreenList.add("TOPICS");
		innerScreenList.add("TOPIC");
		innerScreenList.add("ARTICLE");
		innerScreenList.add("FILE_UPLOAD");	
		innerScreenList.add("CATEGORIES");	
	}
	
	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		HttpSession httpSession = request.getSession();
		
		/*
		 * DAO Initialization
		 */
		UserDAO userDAO = DAOFactory.getUserDAO();
		BlogDAO blogDAO = DAOFactory.getBlogDAO();
		BlogTopicDAO topicDAO = DAOFactory.getBlogTopicDAO();
		BlogTopicTagDAO topicTagDAO = DAOFactory.getBlogTopicTagDAO();
		BlogTopicFileDAO topicFileDAO = DAOFactory.getBlogTopicFileDAO();
//		CalendarCategoryDAO categoryDAO = DAOFactory.getBlogCategoryDAO();

		/*
		 * Data Initialization
		 */
		Data data = new Data();
		Environment environment = new Environment();
		java.util.List<Blog> blogs = null;
		Blog blog = null;
		Topic topic = null;
		User user = null;
		java.util.List<Message> messages = new java.util.ArrayList<Message>();
		
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
		if(request.getSession().getAttribute("BLOG_CONTENT_ID") != null)
		{
			blogID = (Long) request.getSession().getAttribute("BLOG_CONTENT_ID");
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
		 * Event ID
		 */
		long topicID = 0;
		if(parameterMap.get("TOPIC_ID") != null)
		{
			try
			{
				topicID = Long.parseLong(parameterMap.get("TOPIC_ID")[0]);
			}
			catch(NumberFormatException e)
			{
				topicID = 0;
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
			
			if(topicID == 0)
			{
				if(paramAction.equals("CREATE_TOPIC"))
				{
					topicID = topicDAO.createBlogTopic(blogID);
					topic = topicDAO.getBlogTopic(topicID);
				}
//				else if(paramAction.equals("ADD_CATEGORY"))
//				{
//					String paramCategoryLabel = parameterMap.get("CALENDAR_ADD_CATEGORY")[0];
//					
//					Category category = new Category();
//					category.setLabel(paramCategoryLabel);
//					category.setFkCalendarId(calendarID);
//					
//					categoryDAO.insert(category);
//					
////					Role uniqueRole = roleDAO.getUniqueRole(role);
////					if(uniqueRole == null)
////					{
////						roleDAO.insert(role);
////					}
////					else
////					{
////						Message message = new Message();
////						message.setType("error");
////						message.setLabel("The role/email combination already exists.");
////						data.getMessage().add(message);
////					}
//				}
//				else if(paramAction.equals("DELETE_CATEGORY"))
//				{
//					String paramCategoryID = parameterMap.get("CATEGORY_ID")[0];
//					long categoryID = Long.parseLong(paramCategoryID);
//					
//					categoryDAO.delete(categoryID);
//				}
			}			
			else if(topicID > 0)
			{
				topic = topicDAO.getBlogTopic(topicID);
				
				if(paramAction.equals("SAVE_TOPIC"))
				{
					topic = BlogTopicParameters.process(request, topic);

//					messages = eventSaveValidation(topic);
					
					if(messages.size() == 0)
					{					
						topicDAO.updateBlogTopic(topic);	
						
						String[] topicTags = parameterMap.get("TOPIC_TAGS");
						if(topicTags != null && topicTags.length > 0)
						{
							topicTagDAO.deleteTags(topicID);
							for(int index = 0; index < topicTags.length; index++)
							{
								String eventTag = topicTags[index].trim();
								topicTagDAO.addTag(eventTag, topicID, blogID);
							}
						}
						
						Message message = new Message();
						message.setType("success");
						message.setLabel("Event saved.");
						messages.add(message);
					}
				}
				else if(paramAction.equals("DELETE_TOPIC"))
				{
					topicDAO.delete(topicID);
					topic = null;
					
					Message message = new Message();
					message.setType("success");
					message.setLabel("The event has been deleted.");
					messages.add(message);
				}
//				else if(paramAction.equals("DELETE_EVENT_IMAGE"))
//				{
//					String uploadPath = getServletContext().getInitParameter("calendarUploadsPath");					
//					String calendarDirectoryPath = uploadPath + request.getParameter("BLOG_ID");
//					String eventDirectoryPath = calendarDirectoryPath + java.io.File.separator + request.getParameter("TOPIC_ID");
//					String fileUploadPath = eventDirectoryPath + java.io.File.separator + topic.getFileName();
//					
//					java.io.File uploadedFile = new java.io.File(fileUploadPath);
//					if(uploadedFile.exists())
//					{
//						uploadedFile.delete();
//						
//						java.io.File eventDirectory = new java.io.File(eventDirectoryPath);
//						if(eventDirectory.exists())
//						{
//							eventDirectory.delete();
//						}
//					}
//					
////					topic.setFileName("");
////					topic.setFileDescription("");
////					topicDAO.updateCalendarEvent(topic);
//					
//					Message message = new Message();
//					message.setType("success");
//					message.setLabel("The event image has been deleted.");
//					messages.add(message);
//				}
			}
		}
		
		/*
		 * Determine which screen to display
		 */
		if((parameterMap.get("SCREEN") != null || request.getSession().getAttribute("BLOG_CONTENT_SCREEN") != null)
				&& blogID > 0)
		{
			String paramScreen = null;
			if(parameterMap.get("SCREEN") != null)
			{
				paramScreen = parameterMap.get("SCREEN")[0];
			}
			else
			{
				paramScreen = (String) request.getSession().getAttribute("BLOG_CONTENT_SCREEN");
			}
			
			if(innerScreenList.contains(paramScreen))
			{
				blog = blogDAO.getBlog(blogID);
				
				request.getSession().setAttribute("BLOG_CONTENT_ID", blogID);
			}
			
			if(topic != null)
			{
				if(paramScreen.equals("TOPIC"))
				{
					java.util.List<Tag> tags = topicTagDAO.getTags(topicID);
					if(tags != null)
					{
						topic.getTag().addAll(tags);
					}
					
					xslScreen = "topic.xsl";
				}
				else if(paramScreen.equals("ARTICLE"))
				{		
					java.util.List<File> files = topicFileDAO.getFiles(topicID);
					if(files != null)
					{
						topic.getFile().addAll(files);
					}			
					xslScreen = "article.xsl";
				}
				else if(paramScreen.equals("FILE_UPLOAD"))
				{
					java.util.List<File> files = topicFileDAO.getFiles(topicID);
					if(files != null)
					{
						topic.getFile().addAll(files);
					}
					
					xslScreen = "file_upload.xsl";
				}
			}
			else if(paramScreen.equals("TOPICS"))
			{
				java.util.List<Topic> topics = topicDAO.getBlogTopics(blogID); // topicDAO.getBlogTopicsToolbox(blogID);
				if(topics != null)
				{
					blog.getTopic().addAll(topics);
				}
				
				xslScreen = "topics.xsl";
			}
			else if(paramScreen.equals("CATEGORIES"))
			{				
				xslScreen = "categories.xsl";
			}
			else
			{
				xslScreen = "list.xsl";
				
				blogs = blogDAO.getBlogsManage(user);
				if(blogs != null)
				{
					data.getBlog().addAll(blogs);
				}
			}
			
			if(blog != null)
			{
				if(topic != null)
				{
					blog.getTopic().add(topic);
				}
				
//				java.util.List<Category> categories = categoryDAO.getCategories(blogID);
//				if(categories != null)
//				{
//					blog.getCategory().addAll(categories);
//				}
				
				data.getBlog().add(blog);
			}
			
			request.getSession().setAttribute("BLOG_CONTENT_SCREEN", paramScreen);
		}
		else
		{
			xslScreen = "list.xsl";
			
			blogs = blogDAO.getBlogsManage(user);
			if(blogs != null)
			{
				data.getBlog().addAll(blogs);
			}
			
			request.getSession().removeAttribute("BLOG_CONTENT_ID");
			request.getSession().setAttribute("BLOG_CONTENT_SCREEN", "LIST");
		}
		
		/*
		 * file upload iframe message
		 */
		if(request.getSession().getAttribute("message") != null)
		{
			Message message = (Message) request.getSession().getAttribute("message");
			messages.add(message);
			request.getSession().setAttribute("message", null);
		}
		
		data.getMessage().addAll(messages);
		
		environment.setComponentId(7);
		environment.setServerName(getBaseUrl(request));
		data.setEnvironment(environment);
		
		TransformerHelper transformerHelper = new TransformerHelper();
		transformerHelper.setUrlResolverBaseUrl(getServletContext().getInitParameter("blogManageXslUrl"));
		
		String xmlStr = transformerHelper.getXmlStr("org.sw.marketing.data.blog", data);
		xslScreen = getServletContext().getInitParameter("blogManageXslPath") + xslScreen;
		String xslStr = ReadFile.getSkin(xslScreen);
		String htmlStr = transformerHelper.getHtmlStr(xmlStr, new ByteArrayInputStream(xslStr.getBytes()));
		
		String toolboxSkinPath = getServletContext().getInitParameter("assetPath") + "toolbox.html";
		String skinHtmlStr = ReadFile.getSkin(toolboxSkinPath);
		skinHtmlStr = skinHtmlStr.replace("{NAME}", "Calendar Content");
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
