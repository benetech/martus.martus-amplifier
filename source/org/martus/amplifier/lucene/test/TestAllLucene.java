package org.martus.amplifier.lucene.test;

import junit.framework.Test;
import junit.framework.TestSuite;


public class TestAllLucene extends TestSuite
{

	public TestAllLucene()
	{
		super();
	}

	public static void main(String[] args)
	{
		runTests();
	}

	public static void runTests()
	{
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite()
	{
		TestSuite suite =
			new TestSuite("All Martus Amplifier Lucene Tests");
		
		suite.addTest(new TestSuite(TestLuceneSearcher.class));
		suite.addTest(new TestSuite(TestLuceneBulletinIndexer.class));
		suite.addTest(new TestSuite(TestRawLuceneSearching.class));
		
		return suite;
	}

}