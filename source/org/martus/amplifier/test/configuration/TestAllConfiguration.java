package org.martus.amplifier.test.configuration;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAllConfiguration extends TestSuite
{

	public TestAllConfiguration()
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
			new TestSuite("All Martus Amplifier Configuration Tests");

		suite.addTest(new TestSuite(TestAmplifierConfiguration.class));

		return suite;
	}

}
