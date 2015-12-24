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
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

@WebServlet("/signin")
public class LinkedInLogin extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private static final String consumerKey = "78u27r4jbd4ohs";
	private static final String consumerSecret = "uRdsiqrrItp4aCim";
	private static final String callback = "http://localhost:8080/toolbox/callback";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		OAuthService service = new ServiceBuilder().provider(LinkedInApi.class).apiKey(consumerKey).apiSecret(consumerSecret).callback(callback).build();
		Token requestToken = service.getRequestToken();
		request.getSession().setAttribute("requestToken", requestToken);
		String authUrl = service.getAuthorizationUrl(requestToken);
		response.sendRedirect(authUrl);
	}
}
