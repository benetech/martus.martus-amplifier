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
import org.martus.amplifier.presentation.FoundBulletin;
import org.martus.amplifier.presentation.SimpleSearch;
import org.martus.amplifier.search.BulletinIndexException;
import org.martus.amplifier.search.BulletinInfo;
import org.martus.amplifier.velocity.AmplifierServletRequest;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.packet.UniversalId;
import org.martus.common.test.TestCaseEnhanced;

public class TestFoundBulletin extends TestCaseEnhanced
{
	public TestFoundBulletin(String name)
	{
		super(name);
	}

	public void testBasics() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		MockAmplifierResponse response = null;
		Context context = createSampleSearchResults(request, response);

		FoundBulletin servlet = new FoundBulletin();
		String templateName = servlet.selectTemplate(request, response, context);
		assertEquals("FoundBulletin.vm", templateName);
	}
	
	public void testPreviousAndNext() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		request.parameters.put(SearchResultConstants.RESULT_SORTBY_KEY, "title");		
		MockAmplifierResponse response = null;
		request.parameters.put(SearchResultConstants.RESULT_SORTBY_KEY, "title");
		Context context = createSampleSearchResults(request, response);	
	
		FoundBulletin servlet = new FoundBulletin();
		String templateName = servlet.selectTemplate(request, response, context);
		assertEquals("FoundBulletin.vm", templateName);
		assertEquals("previousBulletin not -1?", new Integer(-1), context.get("previousBulletin"));
		assertEquals("nextBulletin not 2?", new Integer(2), context.get("nextBulletin"));		

		BulletinInfo bulletinInfo1 = (BulletinInfo)context.get("bulletin");
		assertEquals("Bulletin 1's ID didn't match", uid1, bulletinInfo1.getBulletinId());
		assertEquals("Bulletin 1's title didn't match", bulletin1Title, bulletinInfo1.get("title"));
		assertEquals("Total bulletin count incorrect?", new Integer(3), context.get("totalBulletins"));
		
		request.parameters.put("index","2");
		servlet.selectTemplate(request, response, context);
		assertEquals("previousBulletin not 1?", new Integer(1), context.get("previousBulletin"));
		assertEquals("nextBulletin not 3?", new Integer(3), context.get("nextBulletin"));
		BulletinInfo bulletinInfo2 = (BulletinInfo)context.get("bulletin");
		assertNotEquals("both bulletin id's equal?",bulletinInfo1.getBulletinId(), bulletinInfo2.getBulletinId());
		assertEquals("Bulletin 2's ID didn't match", uid2, bulletinInfo2.getBulletinId());
		assertEquals("Bulletin 2's title didn't match", bulletin2Title, bulletinInfo2.get("title"));

		request.parameters.put("index","3");
		servlet.selectTemplate(request, response, context);
		assertEquals("previousBulletin not 2?", new Integer(2), context.get("previousBulletin"));
		assertEquals("nextBulletin not -1?", new Integer(-1), context.get("nextBulletin"));
		BulletinInfo bulletinInfo3 = (BulletinInfo)context.get("bulletin");
		assertEquals("Bulletin 3's ID didn't match", uid3, bulletinInfo3.getBulletinId());
		assertEquals("Bulletin 3's title didn't match", bulletin3Title, bulletinInfo3.get("title"));
	}

	public void testSearchedFor() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		MockAmplifierResponse response = null;
		Context context = createSampleSearchResults(request, response);
	
	
		FoundBulletin servlet = new FoundBulletin();
		servlet.selectTemplate(request, response, context);
		assertEquals("Didn't get searchedFor correct", "title", context.get("searchedFor"));
	}
	
	public void testAccountBulletinIds() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		request.parameters.put(SearchResultConstants.RESULT_SORTBY_KEY, "title");
		MockAmplifierResponse response = null;
		Context context = createSampleSearchResults(request, response);
	
	
		FoundBulletin servlet = new FoundBulletin();
		servlet.selectTemplate(request, response, context);
		String publicCode = MartusCrypto.formatPublicCode(MartusCrypto.computePublicCode(bulletinInfo1.getAccountId()));
		assertEquals("Didn't get account public code correct", publicCode, context.get("accountPublicCode"));
		assertEquals("Didn't get bulletin local ID correct", bulletinInfo1.getLocalId(), context.get("bulletinLocalId"));
	}
	
	
	public void testPopulateSimpleSearch() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		request.parameters.put(SearchResultConstants.RESULT_SORTBY_KEY, "title");
		MockAmplifierResponse response = null;		
		Context context = new MockContext();
		
		SimpleSearch servlet = new SimpleSearch();					
		String templateName = servlet.selectTemplate(request, response, context);
					
		assertEquals("SimpleSearch.vm", templateName);				
		assertEquals("The defaultSimpleSearch is empty", "", context.get("defaultSimpleSearch"));		
		
		String sampleQuery = "this is what the user is searching for";		
		request.getSession().setAttribute("simpleQuery", sampleQuery);		
		
		servlet = new SimpleSearch();
		servlet.selectTemplate(request, response, context);
		
		assertEquals("The defaultSimpleSearch match.", sampleQuery, context.get("defaultSimpleSearch"));				
	}	
	
	public void testContactInfo() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		request.parameters.put(SearchResultConstants.RESULT_SORTBY_KEY, "title");
		MockAmplifierResponse response = null;
		Context context = createSampleSearchResults(request, response);
	
		FoundBulletin servlet = new FoundBulletin();
		servlet.selectTemplate(request, response, context);
		BulletinInfo bulletinInfo1 = (BulletinInfo)context.get("bulletin");
		assertEquals("Bulletin 1's ID didn't match", uid1, bulletinInfo1.getBulletinId());

		assertFalse("Bulletin 1 should not have any contact info", bulletinInfo1.hasContactInfo());
		String noContactInfo = (String)context.get("contactInfo");
		assertNull("ContactInfo should not be set", noContactInfo);

		context = createSampleSearchResults2(request, response);
	
		servlet = new FoundBulletin();
		servlet.selectTemplate(request, response, context);
		BulletinInfo bulletinInfo2 = (BulletinInfo)context.get("bulletin");
		assertEquals("Bulletin 2's ID didn't match", uid2, bulletinInfo2.getBulletinId());

		assertTrue("Bulletin 2 should have contact info", bulletinInfo2.hasContactInfo());
		String contactInfo = (String)context.get("contactInfo");
		assertEquals("ContactInfo should not be set", "true", contactInfo);
		assertTrue("Actual File should exist", bulletinInfo2.getContactInfoFile().exists());
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

	private Context createSampleSearchResults2(MockAmplifierRequest request, MockAmplifierResponse response) throws Exception
	{
		Context context = new MockContext();
		SearchResultsForTesting sr = new SearchResultsForTesting();
		request.putParameter("query", "title");
		request.parameters.put("index","2");
		request.parameters.put("searchedFor","title");
		sr.selectTemplate(request, response, context);
		clearContextSetBySearchResults(context);
		return context;
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
			bulletinInfo1 = new BulletinInfo(uid1);
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
	BulletinInfo bulletinInfo1;
}

