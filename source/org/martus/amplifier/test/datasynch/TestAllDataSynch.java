package org.martus.amplifier.test.datasynch;

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

		suite.addTest(new TestSuite(AmplifierNetworkGatewayTest.class));
		suite.addTest(new TestSuite(BackupServerManagerTest.class));

		return suite;
	}

}
