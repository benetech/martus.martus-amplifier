package org.martus.amplifier.service.search;

import org.martus.amplifier.service.search.lucene.TestLuceneSearcher;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAllSearch extends TestSuite
{

	public TestAllSearch()
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
			new TestSuite("All Martus Amplifier Search Tests");
		
		suite.addTest(new TestSuite(TestLuceneSearcher.class));
		
		return suite;
	}

}