package org.martus.amplifier.network.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAllNetwork extends TestSuite
{

	public TestAllNetwork()
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
			new TestSuite("All Martus Amplifier Network Tests");

		// NONE YET!		
		
		return suite;
	}

}