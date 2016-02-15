package org.sw.marketing.util;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import org.joda.time.DateTime;

public class Recursion
{
	private LocalDate startDate = null;
	private LocalDate currentDate = null;		
	private LocalDate endDate = null;
	
	private java.util.List<String> recursions = null;
	
	private boolean limitEnabled = false;
	private int limit = 0;
	private int interval = 1;
	
	private int dayOfMonth = 1;
	
	public LocalDate getStartDate()
	{
		return startDate;
	}
	public void setStartDate(LocalDate startDate)
	{
		this.startDate = startDate;
	}
	public LocalDate getCurrentDate()
	{
		return currentDate;
	}
	public void setCurrentDate(LocalDate currentDate)
	{
		this.currentDate = currentDate;
	}
	public LocalDate getEndDate()
	{
		return endDate;
	}
	public void setEndDate(LocalDate endDate)
	{
		this.endDate = endDate;
	}
	public java.util.List<String> getRecursions()
	{
		return recursions;
	}
	public void setRecursions(java.util.List<String> recursions)
	{
		this.recursions = recursions;
	}
	public boolean isLimitEnabled()
	{
		return limitEnabled;
	}
	public void setLimitEnabled(boolean limitEnabled)
	{
		this.limitEnabled = limitEnabled;
		if(this.limitEnabled)
		{
			this.setEndDate(LocalDate.parse("2099-12-31"));
		}
	}
	public int getLimit()
	{
		return limit;
	}
	public void setLimit(int limit)
	{
		this.limit = limit;
	}	
	public int getInterval()
	{
		return interval;
	}
	public void setInterval(int interval)
	{
		this.interval = interval;
	}
	public int getDayOfMonth()
	{
		return dayOfMonth;
	}
	public void setDayOfMonth(int dayOfMonth)
	{
		this.dayOfMonth = dayOfMonth;
	}
	
	public java.util.List<String> printWeekly(Recursion recursion)
	{
		java.util.List<String> dates = new java.util.ArrayList<String>();
		
		boolean limitEnabled = recursion.isLimitEnabled();
		int limit = recursion.getLimit();
		int interval = recursion.getInterval();
		int counter = 0;
		
		LocalDate startDate = recursion.getStartDate();
		LocalDate currentDate = startDate;
		LocalDate endDate = recursion.getEndDate();
		
		java.util.List<String> recursions = recursion.getRecursions();
		if(recursions != null && recursions.size() > 0)
		{
			while(currentDate.isBefore(endDate) || currentDate.isEqual(endDate))
			{
				String dayOfWeek = currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase();
				if(recursions.contains(dayOfWeek))
				{
					if(limitEnabled)
					{
						if(counter < limit)
						{
							dates.add(currentDate.toString());
							counter++;
						}
					}
					else
					{
						dates.add(currentDate.toString());
					}
				}
				
				String lastDayInRecursion = recursions.get(recursions.size() - 1);
				if(dayOfWeek.equals(lastDayInRecursion) && interval > 1)
				{
					currentDate = currentDate.plusWeeks(interval - 1).plusDays(1);
				}
				else
				{
					currentDate = currentDate.plusDays(1);	
				}		
			}
		}
		
		return dates;
	}
	
	public java.util.List<String> printMonthly(Recursion recursion)
	{
		java.util.List<String> dates = new java.util.ArrayList<String>();
		
		boolean limitEnabled = recursion.isLimitEnabled();
		int limit = recursion.getLimit();
		int interval = recursion.getInterval();
		int counter = 0;
		
		LocalDate startDate = recursion.getStartDate();
		LocalDate currentDate = recursion.getCurrentDate();
		LocalDate endDate = recursion.getEndDate();
		int dayOfMonth = startDate.getDayOfMonth();
		
		DateTime dateTime = new DateTime(
				currentDate.getYear(),
				currentDate.getMonthValue(),
				currentDate.getDayOfMonth(),
				0,0,0
		);
		
		int maxDaysInMonth = dateTime.dayOfMonth().getMaximumValue();
		
		if(limitEnabled)
		{
			while(counter < limit)
			{
				if(dayOfMonth <= maxDaysInMonth && currentDate != startDate)
				{
					try
					{
						if(limitEnabled && currentDate != startDate)
						{
							if(counter < limit)
							{
								dates.add(currentDate.toString());
								counter++;
							}
						}
						else
						{
							dates.add(currentDate.toString());
						}
					}
					catch(DateTimeException e)
					{}
				}
				System.out.print(currentDate);
				try
				{
					currentDate = currentDate.plusMonths(interval).withDayOfMonth(dayOfMonth);
				}
				catch(DateTimeException e)
				{
					currentDate = currentDate.plusMonths(interval + 1);
				}
				System.out.println("\t" + currentDate);
			}
		}
		else
		{
			while(currentDate.isBefore(endDate) || currentDate.isEqual(endDate))
			{
				if(dayOfMonth <= maxDaysInMonth && currentDate != startDate)
				{
					try
					{
						if(limitEnabled && currentDate != startDate)
						{
							if(counter < limit)
							{
								dates.add(currentDate.toString());
								counter++;
							}
						}
						else
						{
							dates.add(currentDate.toString());
						}
					}
					catch(DateTimeException e)
					{}
				}
				System.out.println(currentDate);;
				try
				{
					currentDate = currentDate.plusMonths(interval).withDayOfMonth(dayOfMonth);
				}
				catch(DateTimeException e)
				{
					currentDate = currentDate.plusMonths(interval + 1);
				}
			}
		}		
		
		return dates;
	}
}
