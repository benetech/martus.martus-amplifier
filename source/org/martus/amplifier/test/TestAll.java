package org.martus.amplifier.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAll extends TestSuite
{
    public TestAll() 
    {
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
		TestSuite suite= new TestSuite("All Martus Amplifier Tests");
		
		// example of a test suite
		suite.addTest(org.martus.amplifier.test.search.TestAllSearch.suite());

	    return suite;
	}
}
