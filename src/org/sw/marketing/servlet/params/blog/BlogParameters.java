package org.sw.marketing.servlet.params.blog;

import javax.servlet.http.HttpServletRequest;

import org.sw.marketing.data.blog.Data.Blog;

public class BlogParameters
{
	public static Blog process(HttpServletRequest request, Blog blog)
	{
		@SuppressWarnings("unchecked")
		java.util.Map<String, String[]> parameterMap = (java.util.HashMap<String, String[]>) request.getAttribute("parameterMap");

		if(parameterMap.get("BLOG_TITLE") != null)
		{
			blog.setTitle(parameterMap.get("BLOG_TITLE")[0]);
		}
		if(parameterMap.get("BLOG_PRETTY_URL") != null)
		{
			String prettyUrl = parameterMap.get("BLOG_PRETTY_URL")[0];
			prettyUrl = prettyUrl.replaceAll("[^A-Za-z0-9\\-\\.\\_\\~\\s]", "").replaceAll("\\s", "-");
			prettyUrl = prettyUrl.toLowerCase();
			
			blog.setPrettyUrl(prettyUrl);			
		}
		if(parameterMap.get("BLOG_SKIN_ID") != null)
		{
			blog.setFkSkinId(Long.parseLong(parameterMap.get("BLOG_SKIN_ID")[0]));
		}

		return blog;
	}
}
