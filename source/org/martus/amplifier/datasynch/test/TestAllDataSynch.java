package org.martus.amplifier.datasynch.test;

import junit.framework.Test;
import junit.framework.TestSuite;


public class TestAllDataSynch extends TestSuite
{

	public TestAllDataSynch()
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
			new TestSuite("All Martus Amplifier Data Synch Tests");

		suite.addTest(new TestSuite(TestAmplifierNetworkGateway.class));
		suite.addTest(new TestSuite(TestDataSynchManager.class));
		suite.addTest(new TestSuite(TestBulletinExtractor.class));

		return suite;
	}

}
