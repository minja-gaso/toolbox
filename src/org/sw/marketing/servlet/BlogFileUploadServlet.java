package org.sw.marketing.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.sw.marketing.dao.blog.BlogDAO;
import org.sw.marketing.dao.blog.topic.BlogTopicDAO;
import org.sw.marketing.dao.blog.DAOFactory;
import org.sw.marketing.dao.blog.file.BlogTopicFileDAO;
import org.sw.marketing.data.blog.Data;
import org.sw.marketing.data.blog.Data.Blog;
import org.sw.marketing.data.blog.Data.Blog.Topic;
import org.sw.marketing.data.blog.Data.Blog.Topic.File;
import org.sw.marketing.data.blog.Message;
import org.sw.marketing.transformation.TransformerHelper;
import org.sw.marketing.util.ReadFile;

public class BlogFileUploadServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String paramBlogID = request.getParameter("BLOG_ID");
		long blogID = 0;
		try
		{
			blogID = Long.parseLong(paramBlogID);
		}
		catch (NumberFormatException e)
		{
			blogID = 0;
		}

		String paramTopicID = request.getParameter("TOPIC_ID");
		long topicID = 0;
		try
		{
			topicID = Long.parseLong(paramTopicID);
		}
		catch (NumberFormatException e)
		{
			topicID = 0;
		}

		BlogDAO blogDAO = DAOFactory.getBlogDAO();
		BlogTopicDAO topicDAO = DAOFactory.getBlogTopicDAO();
		BlogTopicFileDAO fileDAO = DAOFactory.getBlogTopicFileDAO();

		Data data = new Data();
		Blog blog = blogDAO.getBlog(blogID);

		if (blog != null)
		{
			Topic topic = topicDAO.getBlogTopic(topicID);
			if (topic != null)
			{
				java.util.List<File> files = fileDAO.getFiles(topic.getId());
				if(files != null)
				{
					topic.getFile().addAll(files);
				}
				blog.getTopic().add(topic);
			}
			data.getBlog().add(blog);
		}
		
		if(request.getAttribute("message") != null)
		{
			Message message = (Message) request.getAttribute("message");
			data.getMessage().add(message);
		}

		/*
		 * generate output
		 */
		TransformerHelper transformerHelper = new TransformerHelper();
		transformerHelper.setUrlResolverBaseUrl(getServletContext().getInitParameter("blogManageXslUrl"));
		
		String xmlStr = transformerHelper.getXmlStr("org.sw.marketing.data.blog", data);
		String xslScreen = getServletContext().getInitParameter("blogManageXslPath") + "file_upload_iframe.xsl";
		String xslStr = ReadFile.getSkin(xslScreen);
		String htmlStr = transformerHelper.getHtmlStr(xmlStr, new ByteArrayInputStream(xslStr.getBytes()));

		System.out.println(xmlStr);
		response.getWriter().println(htmlStr);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		process(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		java.util.List<String> validFileTypes = new java.util.ArrayList<String>();
		validFileTypes.add("image/png");
		validFileTypes.add("image/jpeg");
		validFileTypes.add("image/gif");
		validFileTypes.add("image/bmp");
		
		String paramCalendarID = request.getParameter("BLOG_ID");
		long calendarID = 0;
		try
		{
			calendarID = Long.parseLong(paramCalendarID);
		}
		catch (NumberFormatException e)
		{
			calendarID = 0;
		}

		String paramEventID = request.getParameter("TOPIC_ID");
		long eventID = 0;
		try
		{
			eventID = Long.parseLong(paramEventID);
		}
		catch (NumberFormatException e)
		{
			eventID = 0;
		}

		BlogDAO calendarDAO = DAOFactory.getBlogDAO();
		BlogTopicDAO topicDAO = DAOFactory.getBlogTopicDAO();

		Topic topic = topicDAO.getBlogTopic(eventID);
		File file = new File();
		file.setFkTopicId(topic.getId());
		
		try
		{
			List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
			if(items != null)
			{
				for (FileItem item : items)
				{
					if (item.isFormField())
					{
						/*
						 * process standard form fields
						 */
						String fieldName = item.getFieldName();
						String fieldValue = item.getString();

						if(fieldName.equals("TOPIC_FILE_TYPE"))
						{
							file.setType(fieldValue);
						}
						if(fieldName.equals("TOPIC_FILE_DESCRIPTION"))
						{
							file.setDescription(fieldValue);
						}
					}
					else
					{
						/*
						 * process file form field
						 */
						String fieldName = item.getFieldName();
						String fileName = FilenameUtils.getName(item.getName());
						String fileType = getServletContext().getMimeType(fileName);
						if(validFileTypes.contains(fileType))
						{
							file.setName(fileName);
							InputStream fileContent = item.getInputStream();
							
							String uploadPath = getServletContext().getInitParameter("blogFileUploadPath");						
							String calendarUploadPath = uploadPath + request.getParameter("BLOG_ID");
							java.io.File calendarUploadPathFile = new java.io.File(calendarUploadPath);
							if(!calendarUploadPathFile.exists())
							{
								calendarUploadPathFile.mkdir();
							}
							
							String eventUploadPath = calendarUploadPath + java.io.File.separator + request.getParameter("TOPIC_ID");
							java.io.File eventUploadPathFile = new java.io.File(eventUploadPath);
							if(!eventUploadPathFile.exists())
							{
								eventUploadPathFile.mkdir();
							}
							
							String fileUploadPath = eventUploadPath + java.io.File.separator + file.getName();
							java.io.File fileSave = new java.io.File(fileUploadPath);
							try
							{
								item.write(fileSave);
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
							
							Message message = new Message();
							message.setType("success");
							message.setLabel("The event image has been successfully uploaded.");
							request.getSession().setAttribute("message", message);
						}
						else
						{
							Message message = new Message();
							message.setType("error");
							message.setLabel("The file extension is not supported.  Please upload only a .gif, .jpg, .jpeg, and .png file.");
							request.getSession().setAttribute("message", message);
						}
					}
				}
//				topicDAO.updateBlogTopic(topic);
				BlogTopicFileDAO fileDAO = DAOFactory.getBlogTopicFileDAO();
				fileDAO.insert(file);
			}
		}
		catch (FileUploadException e)
		{
			throw new ServletException("Cannot parse multipart request.", e);
		}

		process(request, response);
	}
}
