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

import java.util.HashMap;

public class AmplifierLocalization
{
	public static String getLanguageString(String code)
	{
		HashMap languages = AmplifierLocalization.buildLanguageMap();
		if(!languages.containsKey(code))
			return null;
		return (String)languages.get(code);		
	}

	private static HashMap buildLanguageMap()
	{
		HashMap languages = new HashMap();
		languages.put(SearchResultConstants.LANGUAGE_ANYLANGUAGE_LABEL, SearchResultConstants.LANGUAGE_ANYLANGUAGE_LABEL);
		languages.put("en", SearchResultConstants.LANGUAGE_ENGLISH_LABEL);
		languages.put("fr", SearchResultConstants.LANGUAGE_FRENCH_LABEL);
		languages.put("de", SearchResultConstants.LANGUAGE_GERMAN_LABEL);
		languages.put("id", SearchResultConstants.LANGUAGE_INDONESIAN_LABEL);
		languages.put("ru", SearchResultConstants.LANGUAGE_RUSSIAN_LABEL);
		languages.put("es", SearchResultConstants.LANGUAGE_SPANISH_LABEL);
		return languages;
	}

}
