package org.martus.amplifier.presentation.test;

import org.apache.velocity.context.Context;
import org.martus.amplifier.presentation.SimpleSearch;
import org.martus.common.test.TestCaseEnhanced;

public class TestSimpleSearch extends TestCaseEnhanced
{
	public TestSimpleSearch(String name)
	{
		super(name);
	}

	public void testBasics() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		MockAmplifierResponse response = null;
		Context context = new MockContext();
		
		SimpleSearch ss = new SimpleSearch();
		String templateName = ss.selectTemplate(request, response, context);
		assertEquals("SimpleSearch.vm", templateName);
	}
	
	public void testPopulateSimpleSearch() throws Exception
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
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
}
