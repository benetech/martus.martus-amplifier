package org.martus.amplifier.common.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAllCommon extends TestSuite
{
		public TestAllCommon()
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
				new TestSuite("All Martus Amplifier Main Tests");

			suite.addTest(new TestSuite(TestAmplifierConfiguration.class));
			suite.addTest(new TestSuite(TestSearchParameters.class));

			return suite;
		}

	}
