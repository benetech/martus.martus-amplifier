package org.martus.amplifier.presentation.search;

import java.util.Date;
import java.util.GregorianCalendar;

public class DateValidator
{
	public void setYear(int year)
	{
		this.year = year;
	}
	
	public void setMonth(int month)
	{
		this.month = month;
	}
	
	public void setDay(int day)
	{
		this.day = day;
	}
	
	public Date getDate()
	{
		return new GregorianCalendar(year, month, day).getTime();
	}
	
	private int year;
	private int month;
	private int day;
}

