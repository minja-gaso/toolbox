package org.sw.marketing.servlet.email;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

@WebServlet("/email")
public class SendReportEmailServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
	    try
		{
	    	Email email = new SimpleEmail();
	    	email.setHostName("smtp.googlemail.com");
	    	email.setSmtpPort(465);
	    	email.setAuthenticator(new DefaultAuthenticator("gasomi90@gmail.com", "Zaboravi90"));
	    	email.setSSLOnConnect(true);
	    	email.setFrom("gasomi90@gmail.com");
	    	email.setSubject("TestMail");
	    	email.setMsg("This is a test mail ... :-)");
	    	email.addTo("gasomi90+LogMeIn@gmail.com");
	    	email.send();

		}
		catch (EmailException e)
		{
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		process(request, response);
	}
}
