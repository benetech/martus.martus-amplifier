
/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2003, Beneficent
Technology, Inc. (Benetech).

Martus is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later
version with the additions and exceptions described in the
accompanying Martus license file entitled "license.txt".

It is distributed WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, including warranties of fitness of purpose or
merchantability.  See the accompanying Martus License and
GPL license for more details on the required license terms
for this software.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.

*/
package org.martus.amplifier.common;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class Today
{
	public static int getYear()
	{	
		return new GregorianCalendar().get(Calendar.YEAR);
	}
	
	public static String getMonth()
	{	
		int month = new GregorianCalendar().get(Calendar.MONTH);
		return new Integer(month).toString();
	}
	
	public static int getDay()
	{
		return new GregorianCalendar().get(Calendar.DATE);
	}	
	
	public static String getYearString()
	{
		return new Integer(getYear()).toString();
	}	
	
	public static String getDayString()
	{
		return new Integer(getDay()).toString();
	}		
		
	public Today(){}
}
