package org.sw.marketing.servlet.linkedin;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.sw.marketing.data.form.Data.User;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/callback")
public class LinkedInCallback extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private static final String consumerKey = "78u27r4jbd4ohs";
	private static final String consumerSecret = "uRdsiqrrItp4aCim";
	private static final String callback = "http://localhost:8080/toolbox/callback";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String paramVerifier = request.getParameter("oauth_verifier");
		OAuthService service = new ServiceBuilder().provider(LinkedInApi.class).apiKey(consumerKey).apiSecret(consumerSecret).callback(callback).build();
		Verifier verifier = new Verifier(paramVerifier);
		
		Token requestToken = (Token) request.getSession().getAttribute("requestToken");
		Token accessToken = service.getAccessToken(requestToken, verifier);
		
		OAuthRequest oauthRequest = new OAuthRequest(Verb.GET, "http://api.linkedin.com/v1/people/~:(id,picture-url,email-address,first-name,last-name)?format=json");
		service.signRequest(accessToken, oauthRequest);
		Response oauthResponse = oauthRequest.send();
		String responseBody = oauthResponse.getBody();
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(responseBody);
		
//		System.out.println(responseBody);

		String id = null, firstName = null, lastName = null, emailAddress = null, pictureUrl = null;
		if(jsonNode.get("id") != null)
		{
			id = jsonNode.get("id").asText();
		}
		if(jsonNode.get("firstName") != null)
		{
			firstName = jsonNode.get("firstName").asText();
		}
		if(jsonNode.get("lastName") != null)
		{
			lastName = jsonNode.get("lastName").asText();
		}
		if(jsonNode.get("emailAddress") != null)
		{
			emailAddress = jsonNode.get("emailAddress").asText();
		}
		if(jsonNode.get("pictureUrl") != null)
		{
			pictureUrl = jsonNode.get("pictureUrl").asText();
		}
		
		User user = new User();
		user.setEmailAddress(emailAddress);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setPictureUrl(pictureUrl);
		
		request.getSession().setAttribute("user", user);
		response.sendRedirect("/toolbox/controller");
	}

}
