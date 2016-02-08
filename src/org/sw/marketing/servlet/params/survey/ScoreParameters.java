package org.sw.marketing.servlet.params.survey;

import javax.servlet.http.HttpServletRequest;

import org.sw.marketing.data.form.Data.Score;

public class ScoreParameters
{
	public static Score process(HttpServletRequest request, Score score)
	{
		@SuppressWarnings("unchecked")
		java.util.Map<String, String[]> parameterMap = (java.util.HashMap<String, String[]>) request.getAttribute("parameterMap");

		if(parameterMap.get("SCORE_TITLE") != null)
		{
			score.setTitle(parameterMap.get("SCORE_TITLE")[0]);
		}
		int beginScore = 0;
		if(parameterMap.get("SCORE_BEGIN") != null)
		{
			try
			{
				beginScore = Integer.parseInt(parameterMap.get("SCORE_BEGIN")[0]);
			}
			catch(NumberFormatException e)
			{
				beginScore = score.getBegin();
			}
			score.setBegin(beginScore);
		}
		int endScore = 0;
		if(parameterMap.get("SCORE_END") != null)
		{
			try
			{
				endScore = Integer.parseInt(parameterMap.get("SCORE_END")[0]);
			}
			catch(NumberFormatException e)
			{
				endScore = score.getEnd();
			}
			score.setEnd(endScore);
		}
		if(parameterMap.get("SCORE_SUMMARY") != null)
		{
			score.setSummary(parameterMap.get("SCORE_SUMMARY")[0]);
		}
		
		/*
		 * swap low and high if lower number is larger
		 */
		if(beginScore > endScore)
		{
			score.setBegin(endScore);
			score.setEnd(beginScore);
		}
		
		return score;
	}
}
