package org.martus.amplifier.main.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAllMain extends TestSuite
{
		public TestAllMain()
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

			suite.addTest(new TestSuite(TestMartusAmplifier.class));
			suite.addTest(new TestSuite(TestLanguagesIndexList.class));

			return suite;
		}

	}
