
package org.martus.amplifier.presentation;

import java.util.Arrays;
import java.util.List;

public class MonthFields
{			
	public static List getMonthDisplayNames()
	{
		return MONTH_NAMES;			
	}
	
	public static int getIndexOfMonth(String month)
	{
		return MONTH_NAMES.indexOf(month)+1;
	}
	
	private static final List MONTH_NAMES = Arrays.asList(new String[] {
		"January", "February", "March", "April", "May", "June",
		"July", "August", "September", "October", "November", "December"
	});
}
