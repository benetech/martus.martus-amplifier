package org.martus.amplifier.presentation.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAllPresentation extends TestSuite
{
	public TestAllPresentation(String name)
	{
		super(name);
	}
	
	public static void main (String[] args) 
	{
		runTests();
	}

	public static void runTests () 
	{
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite ( ) 
	{
		TestSuite suite= new TestSuite("All Martus Amplifier Presentation Tests");
		
		suite.addTest(new TestSuite(TestAdvancedSearch.class));
		suite.addTest(new TestSuite(TestDoSearch.class));
		suite.addTest(new TestSuite(TestDownloadAttachment.class));
		suite.addTest(new TestSuite(TestFoundBulletin.class));
		suite.addTest(new TestSuite(TestSearchResults.class));
		suite.addTest(new TestSuite(TestSimpleSearch.class));
		suite.addTest(new TestSuite(TestUserFeedbackForm.class));
	
		return suite;
	}


}
