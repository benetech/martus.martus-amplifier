package org.martus.amplifier.presentation.test;

import java.util.List;
import java.util.Vector;

import org.apache.velocity.context.Context;
import org.martus.amplifier.presentation.SearchResults;
import org.martus.amplifier.search.BulletinIndexException;
import org.martus.amplifier.search.BulletinInfo;
import org.martus.amplifier.velocity.AmplifierServletRequest;
import org.martus.common.packet.UniversalId;
import org.martus.common.test.TestCaseEnhanced;

public class TestSearchResults extends TestCaseEnhanced
{
	public TestSearchResults(String name)
	{
		super(name);
	}

	public void testNoResults() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		MockAmplifierResponse response = null;
		Context context = new MockContext();
		
		SearchResults sr = new SearchResults();
		request.putParameter("query", "owiefijweofiejoifoiwjefoiwef");
		String templateName = sr.selectTemplate(request, response, context);
		assertEquals("NoSearchResults.vm", templateName);
	}

	public void testYesResults() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		MockAmplifierResponse response = null;
		Context context = new MockContext();

		SearchResultsForTesting sr = new SearchResultsForTesting();
		request.putParameter("query", "owiefijweofiejoifoiwjefoiwef");
		String templateName = sr.selectTemplate(request, response, context);
		assertEquals("SearchResults.vm", templateName);

		int expectedFoundCount = 3;
		Vector foundBulletins = (Vector)context.get("foundBulletins");
		assertEquals(expectedFoundCount, foundBulletins.size());
		BulletinInfo info = (BulletinInfo)foundBulletins.get(0);
		assertEquals(uid1, info.getBulletinId());
		assertEquals("Total bulletin count incorrect?", new Integer(expectedFoundCount), context.get("totalBulletins"));

		request.putParameter("query", null); 
		templateName = sr.selectTemplate(request, response, context);
		assertEquals("NoSearchResults.vm", templateName);

	}

	public void testLanguageCodeToString() throws Exception
	{
		BulletinInfo bulletinInfo1 = new BulletinInfo(uid1);
		bulletinInfo1.set("language", bulletin1Language);
		BulletinInfo bulletinInfo2 = new BulletinInfo(uid2);
		bulletinInfo2.set("language", bulletin2Language);
		BulletinInfo bulletinInfo3 = new BulletinInfo(uid3);
		bulletinInfo3.set("language", bulletin3Language);

		SearchResultsForTesting sr = new SearchResultsForTesting();

		sr.convertLanguageCode(bulletinInfo1);
		assertEquals("English LanguageCode still exists?", "English", bulletinInfo1.get("language"));
		sr.convertLanguageCode(bulletinInfo2);
		assertEquals("Spanish LanguageCode still exists?", "Spanish", bulletinInfo2.get("language"));
		sr.convertLanguageCode(bulletinInfo3);
		assertEquals("Unknown LanguageCode should be returned unchanged?", bulletin3Language, bulletinInfo3.get("language"));
		
	}
	
/*	private Context createSampleSearchResults(MockAmplifierRequest request, HttpServletResponse response) throws Exception
	{
		Context context = new MockContext();
		SearchResultsForTesting sr = new SearchResultsForTesting();
		request.putParameter("query", "test");
		request.parameters.put("index","1");
		sr.selectTemplate(request, response, context);
		return context;
	}
*/
	final UniversalId uid1 = UniversalId.createDummyUniversalId();
	final UniversalId uid2 = UniversalId.createDummyUniversalId();
	final UniversalId uid3 = UniversalId.createDummyUniversalId();
	final String bulletin1Title = "title 1";
	final String bulletin2Title = "title 2";
	final String bulletin3Title = "title 3";
	final String bulletin1Language = "en";
	final String bulletin2Language = "es";
	final String bulletin3Language = "un";


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
			bulletinInfo1.set("language", bulletin1Language);
			infos.add(bulletinInfo1);
			
			BulletinInfo bulletinInfo2 = new BulletinInfo(uid2);
			bulletinInfo2.set("title", bulletin2Title);
			bulletinInfo2.set("language", bulletin2Language);
			infos.add(bulletinInfo2);
			
			BulletinInfo bulletinInfo3 = new BulletinInfo(uid3);
			bulletinInfo3.set("title", bulletin3Title);
			bulletinInfo3.set("language", bulletin3Language);
			infos.add(bulletinInfo3);
			return infos;
		}
	}
	
}
