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
package org.martus.amplifier.presentation;

import java.util.HashMap;
import java.util.Vector;

import org.apache.velocity.context.Context;
import org.martus.amplifier.common.AdvancedSearchInfo;
import org.martus.amplifier.common.AmplifierLocalization;
import org.martus.amplifier.common.ChoiceEntry;
import org.martus.amplifier.common.FindBulletinsFields;
import org.martus.amplifier.common.SearchResultConstants;
import org.martus.amplifier.common.Today;
import org.martus.amplifier.main.MartusAmplifier;
import org.martus.amplifier.velocity.AmplifierServlet;
import org.martus.amplifier.velocity.AmplifierServletRequest;
import org.martus.amplifier.velocity.AmplifierServletResponse;

public class AdvancedSearch extends AmplifierServlet
{
	public String selectTemplate(AmplifierServletRequest request, AmplifierServletResponse response, Context context) throws Exception
	{				
		context.put("monthFields", FindBulletinsFields.getMonthFieldDisplayNames());
				
		Vector filterFields = FindBulletinsFields.getFindWordFilterDisplayNames();
		context.put("filterWordFields", filterFields);

		Vector entryDateFields = FindBulletinsFields.getFindEntryDatesDisplayNames();
		context.put("entryDateFields", entryDateFields);
		
		Vector bulletinFields = FindBulletinsFields.getBulletinFieldDisplayNames();
		context.put("bulletinFields", bulletinFields);	
		
		Vector languageFields = getAvailableLanguageChoices();
		context.put("languageFields", languageFields);		
		
		Vector sortByFields = FindBulletinsFields.getSortByFieldDisplayNames();
		context.put("sortByFields", sortByFields);
		
		AdvancedSearchInfo defaultFields = (AdvancedSearchInfo) request.getSession().getAttribute("defaultAdvancedSearch");
		if (defaultFields == null)
			defaultFields = getDefaultAdvancedSearchInfo();
			
		context.put("defaultAdvancedSearch", defaultFields);	

		return "AdvancedSearch.vm";
	}
	
	private AdvancedSearchInfo getDefaultAdvancedSearchInfo()
	{
		HashMap map = new HashMap();
		map.put(SearchResultConstants.EXACTPHRASE_TAG, "");	
		map.put(SearchResultConstants.ANYWORD_TAG, "");
		map.put(SearchResultConstants.THESE_WORD_TAG, "");
		map.put(SearchResultConstants.RESULT_END_DAY_KEY, Today.getDayString());
		map.put(SearchResultConstants.RESULT_END_MONTH_KEY, Today.getMonth());
		map.put(SearchResultConstants.RESULT_END_YEAR_KEY, Today.getYearString());
	
			
		return new AdvancedSearchInfo(map);		
	}

	Vector getAvailableLanguageChoices()
	{
		Vector languageCodes = MartusAmplifier.languagesIndexed.getListOfLanguagesIndexed();
		Vector fields = new Vector();
		for(int i = 0; i < languageCodes.size(); ++i)
		{
			String code = (String)languageCodes.get(i);
			String languageString = AmplifierLocalization.getLanguageString(code);
			if(languageString == null)
				languageString = code;
			fields.add(new ChoiceEntry(code, languageString));
		}
		return fields;
	}
}
