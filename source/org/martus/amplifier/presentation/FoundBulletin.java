/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2004, Beneficent
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

import java.util.List;

import org.apache.velocity.context.Context;
import org.martus.amplifier.search.BulletinInfo;
import org.martus.amplifier.velocity.AmplifierServlet;
import org.martus.amplifier.velocity.AmplifierServletRequest;
import org.martus.amplifier.velocity.AmplifierServletResponse;
import org.martus.amplifier.velocity.AmplifierServletSession;
import org.martus.common.crypto.MartusCrypto;

public class FoundBulletin extends AmplifierServlet
{
	public String selectTemplate(AmplifierServletRequest request, AmplifierServletResponse response, Context context)
			throws Exception
	{
		super.selectTemplate(request, response, context);
		
		AmplifierServletSession session = request.getSession();
		List bulletins = (List)session.getAttribute("foundBulletins");
		int index = Integer.parseInt(request.getParameter("index"));
		BulletinInfo info = (BulletinInfo)bulletins.get(index - 1);
		context.put("bulletin", info);
		context.put("accountPublicCode", MartusCrypto.formatPublicCode(MartusCrypto.computePublicCode(info.getAccountId())));
		context.put("bulletinLocalId", info.getLocalId());

		if(info.hasContactInfo())
			context.put("contactInfo", "true");
		int previousIndex = index - 1;
		int nextIndex = index + 1;
		if(previousIndex <= 0)
			previousIndex = -1;
		if(nextIndex > bulletins.size())
			nextIndex = -1;
		context.put("searchedFor", request.getParameter("searchedFor"));
		context.put("previousBulletin", new Integer(previousIndex));
		context.put("nextBulletin", new Integer(nextIndex));
		context.put("currentBulletin", new Integer(index));
		context.put("totalBulletins", new Integer(bulletins.size()));
		return "FoundBulletin.vm";
	}
}
