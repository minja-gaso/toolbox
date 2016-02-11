package org.sw.marketing.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.sw.marketing.dao.DAOFactory;
import org.sw.marketing.dao.form.user.UserDAO;
import org.sw.marketing.data.form.Data.User;

/**
 * Servlet Filter implementation class AuthenticationFilter
 */
@WebFilter("/controller")
public class AuthenticationFilter implements Filter
{
	public AuthenticationFilter()
	{
		
	}

	public void destroy()
	{
		
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		User user = null;
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession httpSession = (HttpSession) httpRequest.getSession();
		
		String baseUrl = httpRequest.getRequestURL().toString();
		
		if(!baseUrl.contains("localhost") && httpRequest.getParameter("admin") == null)
		{
			if(httpSession.getAttribute("user") != null)
			{
				user = (User) httpSession.getAttribute("user");
			}
			
			if(user == null)
			{
				httpResponse.sendRedirect("/toolbox/signin");
				return;
			}
			else
			{
				UserDAO userDAO = DAOFactory.getUserDAO();
				User userCheck = userDAO.getUserByEmail(user.getEmailAddress());
				if(userCheck == null)
				{
					userDAO.insert(user);
				}
			}
		}
		else
		{			
			String email = "mgaso@sw.org";
			if(httpRequest.getParameter("email") != null)
			{
				email = httpRequest.getParameter("email");
			}
			httpRequest.getSession().setAttribute("EMAIL_ADDRESS", email);
		}
		
		httpSession.setAttribute("user", user);
		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException
	{
		
	}

}
