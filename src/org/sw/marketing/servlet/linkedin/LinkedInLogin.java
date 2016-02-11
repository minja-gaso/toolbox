package org.sw.marketing.servlet.linkedin;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

@WebServlet("/signin")
public class LinkedInLogin extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private static final String consumerKey = "78u27r4jbd4ohs";
	private static final String consumerSecret = "uRdsiqrrItp4aCim";
	private static String callback = "/toolbox/callback";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		callback = getBaseUrl(request) + callback;
		OAuthService service = new ServiceBuilder().provider(LinkedInApi.class).apiKey(consumerKey).apiSecret(consumerSecret).callback(callback).build();
		Token requestToken = service.getRequestToken();
		request.getSession().setAttribute("requestToken", requestToken);
		String authUrl = service.getAuthorizationUrl(requestToken);
		response.sendRedirect(authUrl);
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
