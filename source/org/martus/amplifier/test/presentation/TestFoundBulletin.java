package org.martus.amplifier.test.presentation;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;
import org.martus.amplifier.presentation.AmplifierServletRequest;
import org.martus.amplifier.presentation.FoundBulletin;
import org.martus.amplifier.presentation.SearchResults;
import org.martus.amplifier.service.search.BulletinIndexException;
import org.martus.amplifier.service.search.BulletinInfo;
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
		HttpServletResponse response = null;
		Context context = createSampleSearchResults(request, response);

		FoundBulletin servlet = new FoundBulletin();
		String templateName = servlet.selectTemplate(request, response, context);
		assertEquals("FoundBulletin.vm", templateName);
	}
	
	public void testPreviousAndNext() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		HttpServletResponse response = null;
		Context context = createSampleSearchResults(request, response);
	
	
		FoundBulletin servlet = new FoundBulletin();
		String templateName = servlet.selectTemplate(request, response, context);
		assertEquals("FoundBulletin.vm", templateName);
		assertEquals("previousBulletin not -1?", new Integer(-1), context.get("previousBulletin"));
		assertEquals("nextBulletin not 2?", new Integer(2), context.get("nextBulletin"));
		BulletinInfo bulletinInfo1 = (BulletinInfo)context.get("bulletin");
	
		request.parameters.put("index","2");
		servlet.selectTemplate(request, response, context);
		assertEquals("previousBulletin not 1?", new Integer(1), context.get("previousBulletin"));
		assertEquals("nextBulletin not 3?", new Integer(3), context.get("nextBulletin"));
		BulletinInfo bulletinInfo2 = (BulletinInfo)context.get("bulletin");
		assertNotEquals("both bulletin id's equal?",bulletinInfo1.getBulletinId(), bulletinInfo2.getBulletinId());

		request.parameters.put("index","3");
		servlet.selectTemplate(request, response, context);
		assertEquals("previousBulletin not 2?", new Integer(2), context.get("previousBulletin"));
		assertEquals("nextBulletin not -1?", new Integer(-1), context.get("nextBulletin"));
	}

	private Context createSampleSearchResults(MockAmplifierRequest request, HttpServletResponse response) throws Exception
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
	class SearchResultsForTesting extends SearchResults
	{
		public List getSearchResults(AmplifierServletRequest request)
			throws Exception, BulletinIndexException
		{
			if(request.getParameter("query")==null)
				throw new Exception("malformed query");
			Vector infos = new Vector();
			infos.add(new BulletinInfo(uid1));
			infos.add(new BulletinInfo(uid2));
			infos.add(new BulletinInfo(uid3));
			return infos;
		}
	}
}

