package org.martus.amplifier.test.presentation;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;
import org.martus.amplifier.presentation.AmplifierServletRequest;
import org.martus.amplifier.presentation.SearchResults;
import org.martus.amplifier.service.search.BulletinIndexException;
import org.martus.amplifier.service.search.BulletinInfo;
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
		HttpServletResponse response = null;
		Context context = new MockContext();
		
		SearchResults sr = new SearchResults();
		request.putParameter("query", "owiefijweofiejoifoiwjefoiwef");
		String templateName = sr.selectTemplate(request, response, context);
		assertEquals("NoSearchResults.vm", templateName);
	}

	public void testYesResults() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		HttpServletResponse response = null;
		Context context = new MockContext();

		SearchResultsForTesting sr = new SearchResultsForTesting();
		request.putParameter("query", "owiefijweofiejoifoiwjefoiwef");
		String templateName = sr.selectTemplate(request, response, context);
		assertEquals("SearchResults.vm", templateName);

		int expectedFoundCount = 2;
		Vector foundBulletins = (Vector)context.get("foundBulletins");
		assertEquals(expectedFoundCount, foundBulletins.size());
		BulletinInfo info = (BulletinInfo)foundBulletins.get(0);
		assertEquals(uid1, info.getBulletinId());

		request.putParameter("query", null); 
		templateName = sr.selectTemplate(request, response, context);
		assertEquals("NoSearchResults.vm", templateName);

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
