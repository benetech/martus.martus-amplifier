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

package org.martus.amplifier.presentation.test;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.apache.velocity.context.Context;
import org.martus.amplifier.common.SearchResultConstants;
import org.martus.amplifier.presentation.DoSearch;
import org.martus.amplifier.presentation.PrintFoundBulletin;
import org.martus.amplifier.search.BulletinIndexException;
import org.martus.amplifier.search.BulletinInfo;
import org.martus.amplifier.velocity.AmplifierServletRequest;
import org.martus.common.packet.UniversalId;
import org.martus.util.TestCaseEnhanced;

public class TestPrintFoundBulletin extends TestCaseEnhanced
{
	public TestPrintFoundBulletin(String name)
	{
		super(name);
	}

	public void testBasics() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		MockAmplifierResponse response = null;
		Context context = createSampleSearchResults(request, response);

		PrintFoundBulletin servlet = new PrintFoundBulletin();
		String templateName = servlet.selectTemplate(request, response, context);
		assertEquals("PrintFoundBulletin.vm", templateName);
	}
	
	public void testCurrentBulletin() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		request.parameters.put(SearchResultConstants.RESULT_SORTBY_KEY, "title" );
		MockAmplifierResponse response = null;
		Context context = createSampleSearchResults(request, response);
	
	
		PrintFoundBulletin servlet = new PrintFoundBulletin();
		String templateName = servlet.selectTemplate(request, response, context);
		assertEquals("PrintFoundBulletin.vm", templateName);
		BulletinInfo bulletinInfo1 = (BulletinInfo)context.get("bulletin");
		assertEquals("Bulletin 1's ID didn't match", uid1, bulletinInfo1.getBulletinId());
		assertEquals("Bulletin 1's title didn't match", bulletin1Title, bulletinInfo1.get("title"));
		
	}

	public void testSearchedFor() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		MockAmplifierResponse response = null;
		Context context = createSampleSearchResults(request, response);
	
	
		PrintFoundBulletin servlet = new PrintFoundBulletin();
		servlet.selectTemplate(request, response, context);
		assertEquals("Didn't get searchedFor correct", "title", context.get("searchedFor"));
	}
	

	public void testCurrentTotalBulletins() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		MockAmplifierResponse response = null;
		Context context = createSampleSearchResults(request, response);
	
	
		PrintFoundBulletin servlet = new PrintFoundBulletin();
		servlet.selectTemplate(request, response, context);
		assertEquals("Didn't get correct current bulletin", new Integer(1), context.get("currentBulletin"));
		assertEquals("Didn't get correct total bulletin count", new Integer(3), context.get("totalBulletins"));
	}

	private Context createSampleSearchResults(MockAmplifierRequest request, MockAmplifierResponse response) throws Exception
	{
		Context context = new MockContext();
		SearchResultsForTesting sr = new SearchResultsForTesting();
		request.putParameter("query", "title");
		request.parameters.put("index","1");
		request.parameters.put("searchedFor","title");
		sr.selectTemplate(request, response, context);
		clearContextSetBySearchResults(context);
		return context;
	}

	private void clearContextSetBySearchResults(Context context)
	{
		context.put("searchedFor", null);
		context.put("previousBulletin", null);
		context.put("nextBulletin", null);
		context.put("currentBulletin", null);
		context.put("totalBulletins", null);
	}


	final UniversalId uid1 = UniversalId.createDummyUniversalId();
	final UniversalId uid2 = UniversalId.createDummyUniversalId();
	final UniversalId uid3 = UniversalId.createDummyUniversalId();
	final String bulletin1Title = "title 1";
	final String bulletin2Title = "title 2";
	final String bulletin3Title = "title 3";


	class SearchResultsForTesting extends DoSearch
	{
		public List getSearchResults(AmplifierServletRequest request)
			throws Exception, BulletinIndexException
		{
			if(request.getParameter("query")==null)
				throw new Exception("malformed query");
			
			Vector infos = new Vector();
			BulletinInfo bulletinInfo1 = new BulletinInfo(uid1);
			bulletinInfo1.set("title", bulletin1Title);
			infos.add(bulletinInfo1);
			
			BulletinInfo bulletinInfo2 = new BulletinInfo(uid2);
			bulletinInfo2.set("title", bulletin2Title);
			File info2ContactInfo = createTempFile();
			info2ContactInfo.createNewFile();
			bulletinInfo2.setContactInfoFile(info2ContactInfo);
			infos.add(bulletinInfo2);
			
			BulletinInfo bulletinInfo3 = new BulletinInfo(uid3);
			bulletinInfo3.set("title", bulletin3Title);
			infos.add(bulletinInfo3);
			return infos;
		}
	}
}

