package org.martus.amplifier.test.search;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAllSearch extends TestSuite
{
	public TestAllSearch()
	{
		super();
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
		TestSuite suite= new TestSuite("All Martus Amplifier Searching Tests");
		
		suite.addTest(new TestSuite(BulletinDocumentTest.class));
		suite.addTest(new TestSuite(BulletinIndexerTest.class));
		suite.addTest(new TestSuite(BulletinSearcherTest.class));
		suite.addTest(new TestSuite(BulletinCatalogTest.class));

	    return suite;
	}
}
