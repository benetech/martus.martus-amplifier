package org.martus.amplifier.test.presentation;

import java.util.Vector;

import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;
import org.martus.amplifier.presentation.AdvancedSearch;
import org.martus.amplifier.presentation.MonthFields;
import org.martus.common.test.TestCaseEnhanced;

public class TestAdvancedSearch extends TestCaseEnhanced
{
	public TestAdvancedSearch(String name)
	{
		super(name);
	}
	
	public void testBasics()
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		HttpServletResponse response = null;
		Context context = new MockContext();
		
		AdvancedSearch as = new AdvancedSearch();
		String templateName = as.selectTemplate(request, response, context);
		assertEquals("AdvancedSearch.vm", templateName);

		Vector fields = (Vector)context.get("searchableDateFields");
		assertEquals(2, fields.size());
		assertContains("Entry Date", fields);
	}
	
	public void testMonthFieldsDisplay()
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		HttpServletResponse response = null;
		Context context = new MockContext();
		
		AdvancedSearch as = new AdvancedSearch();
		String templateName = as.selectTemplate(request, response, context);
		assertEquals("AdvancedSearch.vm", templateName);

		assertEquals(2, MonthFields.getIndexOfMonth("February"));
		assertEquals(12, MonthFields.getIndexOfMonth("December"));		
	}
}
