package org.martus.amplifier.presentation.test;

import java.util.List;
import java.util.Vector;

import org.apache.velocity.context.Context;
import org.martus.amplifier.presentation.FoundBulletin;
import org.martus.amplifier.presentation.SearchResults;
import org.martus.amplifier.search.BulletinIndexException;
import org.martus.amplifier.search.BulletinInfo;
import org.martus.amplifier.velocity.AmplifierServletRequest;
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
		MockAmplifierResponse response = null;
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

	private Context createSampleSearchResults(MockAmplifierRequest request, MockAmplifierResponse response) throws Exception
	{
		Context context = new MockContext();
		SearchResultsForTesting sr = new SearchResultsForTesting();
		request.putParameter("query", "test");
		request.parameters.put("index","1");
		sr.selectTemplate(request, response, context);
		return context;
	}

	final UniversalId uid1 = UniversalId.createDummyUniversalId();
	final UniversalId uid2 = UniversalId.createDummyUniversalId();
	final UniversalId uid3 = UniversalId.createDummyUniversalId();
	final String bulletin1Title = "title 1";
	final String bulletin2Title = "title 2";
	final String bulletin3Title = "title 3";


	class SearchResultsForTesting extends SearchResults
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
			infos.add(bulletinInfo2);
			
			BulletinInfo bulletinInfo3 = new BulletinInfo(uid3);
			bulletinInfo3.set("title", bulletin3Title);
			infos.add(bulletinInfo3);
			return infos;
		}
	}
}

