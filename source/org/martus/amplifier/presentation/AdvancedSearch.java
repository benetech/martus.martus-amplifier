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

import java.util.Vector;

import org.apache.velocity.context.Context;
import org.martus.amplifier.common.FindBulletinsFields;
import org.martus.amplifier.velocity.AmplifierServlet;
import org.martus.amplifier.velocity.AmplifierServletRequest;
import org.martus.amplifier.velocity.AmplifierServletResponse;

public class AdvancedSearch extends AmplifierServlet
{
	public String selectTemplate(AmplifierServletRequest request, AmplifierServletResponse response, Context context)
	{		
		context.put("monthFields", FindBulletinsFields.getMonthFieldDisplayNames());
		context.put("today", FindBulletinsFields.getToday());
				
		Vector filterFields = FindBulletinsFields.getFindWordFilterDisplayNames();
		context.put("filterWordFields", filterFields);

		Vector entryDateFields = FindBulletinsFields.getFindEntryDatesDisplayNames();
		context.put("entryDateFields", entryDateFields);
		
		Vector bulletinFields = FindBulletinsFields.getBulletinFieldDisplayNames();
		context.put("bulletinFields", bulletinFields);	
		
		Vector languageFields = FindBulletinsFields.getLanguageFieldDisplayNames();
		context.put("languageFields", languageFields);			
		
		return "AdvancedSearch.vm";
	}
}
