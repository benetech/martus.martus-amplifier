package org.martus.amplifier.test.presentation;

import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;
import org.martus.amplifier.presentation.FoundBulletin;
import org.martus.common.test.TestCaseEnhanced;

public class TestFoundBulletin extends TestCaseEnhanced
{
	public TestFoundBulletin(String name)
	{
		super(name);
	}

	public void testBasics()
	{
		MockAmplifierRequest request = new MockAmplifierRequest();
		HttpServletResponse response = null;
		Context context = new MockContext();
		
		FoundBulletin servlet = new FoundBulletin();
		String templateName = servlet.selectTemplate(request, response, context);
		assertEquals("FoundBulletin.vm", templateName);
	}
}
