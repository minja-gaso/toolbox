package org.sw.marketing.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.sw.marketing.dao.skin.DAOFactory;
import org.sw.marketing.dao.skin.SkinDAO;
import org.sw.marketing.data.skin.Data;
import org.sw.marketing.util.SkinReader;


@WebServlet("/skinPreview/*")
public class SkinPreviewServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
      
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String skinIdStr = request.getPathInfo().substring(1);
		long skinID = Long.parseLong(skinIdStr);
		
		SkinDAO skinDAO = DAOFactory.getSkinDAO();
		Data data = new Data();
		org.sw.marketing.data.skin.Skin skin = skinDAO.getSkin(skinID);
		
		if(skin != null)
		{
			data.getSkin().add(skin);
			String skinHtmlStr = "";
			if(skin.isEditable())
			{
				skinHtmlStr = skin.getSkinHtml();
			}
			else
			{
				skinHtmlStr = SkinReader.getSkinByUrl(skin.getSkinUrl(), skin.getSkinSelector());
				skinHtmlStr = skinHtmlStr.replace("{TITLE}", skin.getTitle());
				
				Element styleElement = new Element(Tag.valueOf("style"), "");
				String skinCss = skin.getSkinCssOverrides();
				styleElement.text(skinCss);
				String styleElementStr = styleElement.toString();
				styleElementStr = styleElementStr.replaceAll("&gt;", ">").replaceAll("&lt;", "<");
				skinHtmlStr = skinHtmlStr.replace("{CSS}", styleElementStr);
			}
			
			response.getWriter().println(skinHtmlStr);
		}
		else
		{
			response.getWriter().println("The skin you requested could not be found.");
			return;
		}
	}

}
