package org.sw.marketing.servlet.params.skin;

import javax.servlet.http.HttpServletRequest;

import org.sw.marketing.data.skin.Skin;


public class SkinParameters
{
	public static Skin process(HttpServletRequest request, Skin skin)
	{
		@SuppressWarnings("unchecked")
		java.util.Map<String, String[]> parameterMap = (java.util.HashMap<String, String[]>) request.getAttribute("parameterMap");

		if(parameterMap.get("SKIN_TITLE") != null)
		{
			skin.setTitle(parameterMap.get("SKIN_TITLE")[0]);
		}
		if(parameterMap.get("SKIN_URL") != null)
		{
			skin.setSkinUrl(parameterMap.get("SKIN_URL")[0]);
		}
		if(parameterMap.get("SKIN_SELECTOR") != null)
		{
			skin.setSkinSelector(parameterMap.get("SKIN_SELECTOR")[0]);
		}
		if(parameterMap.get("SKIN_CSS") != null)
		{
			skin.setSkinCssOverrides(parameterMap.get("SKIN_CSS")[0]);
		}
		if(parameterMap.get("SKIN_EDITABLE") != null)
		{
			skin.setEditable(Boolean.parseBoolean(parameterMap.get("SKIN_EDITABLE")[0]));
		}
		if(parameterMap.get("SKIN_HTML") != null)
		{
			skin.setSkinHtml(parameterMap.get("SKIN_HTML")[0]);
		}
		
		return skin;
	}
}
