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

public class CharacterUtil
{	
	public static String removeRestrictCharacters(String str)
	{
		String newString = str;		
		for (int j=0; j<str.length();j++)
		{
			char ch = str.charAt(j);
			if (!validChar(ch))
				newString = replaceChar(ch, newString); 				  
		}
																			
		return newString;
	}
	
	public static boolean isWildcardOnly(String str)
	{
		if (str.length() > 1)
			return false;
			
		char ch = str.charAt(0);
		return (ch == '*' || ch == '?')? true:false;
	}

	private static String replaceChar(char oldChar, String text)
	{
		return text.replace(oldChar, SPACE);
	}

	private static boolean validChar(char ch)
	{
		if ((ch >= 48 && ch <= 57) ||
			(ch >= 65 && ch <= 90) ||
			(ch >= 97 && ch <= 122) ||
			(ch == 34 || ch == 39 || ch >= 128))
			return true;
			
		return false;	
	}
	
	public CharacterUtil(){}
	
	final static char SPACE	= ' ';
}
