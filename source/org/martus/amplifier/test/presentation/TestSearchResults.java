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
		String templateName = sr.selectTemplate(request, response, context);
		assertEquals("NoSearchResults.vm", templateName);
	}

	public void testYesResults() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		HttpServletResponse response = null;
		Context context = new MockContext();

		final UniversalId uid1 = UniversalId.createDummyUniversalId();
		final UniversalId uid2 = UniversalId.createDummyUniversalId();
		class SearchResultsForTesting extends SearchResults
		{
			public List getSearchResults(AmplifierServletRequest request)
				throws Exception, BulletinIndexException
			{
				Vector infos = new Vector();
				infos.add(new BulletinInfo(uid1));
				infos.add(new BulletinInfo(uid2));
				return infos;
			}
		}
		SearchResultsForTesting sr = new SearchResultsForTesting();
		String templateName = sr.selectTemplate(request, response, context);
		assertEquals("SearchResults.vm", templateName);

		int expectedFoundCount = 2;
		Vector foundBulletins = (Vector)context.get("foundBulletins");
		assertEquals(expectedFoundCount, foundBulletins.size());
		BulletinInfo info = (BulletinInfo)foundBulletins.get(0);
		assertEquals(uid1, info.getBulletinId());
	}
}
