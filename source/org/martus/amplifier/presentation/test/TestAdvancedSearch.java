package org.martus.amplifier.presentation.test;

import java.util.HashMap;

import org.apache.velocity.context.Context;
import org.martus.amplifier.common.AdvancedSearchInfo;
import org.martus.amplifier.common.SearchResultConstants;
import org.martus.amplifier.presentation.AdvancedSearch;
import org.martus.common.test.TestCaseEnhanced;

public class TestAdvancedSearch extends TestCaseEnhanced
{
	public TestAdvancedSearch(String name)
	{
		super(name);
	}
	
	public void testBasics() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		MockAmplifierResponse response = null;
		Context context = new MockContext();
		
		AdvancedSearch as = new AdvancedSearch();
		String templateName = as.selectTemplate(request, response, context);
		assertEquals("AdvancedSearch.vm", templateName);
	}	
	
	public void testPopulateAdvancedSearch() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		MockAmplifierResponse response = null;		
		Context context = new MockContext();
	
		AdvancedSearch servlet = new AdvancedSearch();					
		String templateName = servlet.selectTemplate(request, response, context);
				
		assertEquals("AdvancedSearch.vm", templateName);				
				
		AdvancedSearchInfo info = defaultAdvancedSearchInfo();
		request.getSession().setAttribute("defaultAdvancedSearch", info);
	
		servlet = new AdvancedSearch();
		servlet.selectTemplate(request, response, context);
		AdvancedSearchInfo defaultInfo = (AdvancedSearchInfo) context.get("defaultAdvancedSearch");
		
		assertEquals("Get exphrase query string", "amp test", (String) defaultInfo.get(SearchResultConstants.EXACTPHRASE_TAG));	
		assertEquals("Get anyword query string", "amp", (String) defaultInfo.get(SearchResultConstants.ANYWORD_TAG));
		assertEquals("Get these query string", "my test", (String) defaultInfo.get(SearchResultConstants.THESE_WORD_TAG));
		assertEquals("Get bulletin field string", "title", (String) defaultInfo.get(SearchResultConstants.RESULT_FIELDS_KEY));
		assertEquals("Get language string", "english", (String) defaultInfo.get(SearchResultConstants.RESULT_LANGUAGE_KEY));				
					
	}	
	
	private AdvancedSearchInfo defaultAdvancedSearchInfo()
	{
		HashMap map = new HashMap();
		map.put(SearchResultConstants.EXACTPHRASE_TAG, "amp test");
		map.put(SearchResultConstants.ANYWORD_TAG, "amp");
		map.put(SearchResultConstants.THESE_WORD_TAG, "my test");
		map.put(SearchResultConstants.RESULT_LANGUAGE_KEY, "english");
		map.put(SearchResultConstants.RESULT_FIELDS_KEY, "title");
		return new AdvancedSearchInfo(map);		
	}
}
