package org.martus.amplifier.search.test;

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

		// NONE YET!		
		
		return suite;
	}

}