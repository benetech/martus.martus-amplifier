package org.martus.amplifier.test.attachment;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAllAttachment extends TestSuite
{

	public TestAllAttachment()
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
			new TestSuite("All Martus Amplifier Attachment Tests");

		suite.addTest(new TestSuite(AttachmentManagerTest.class));

		return suite;
	}

}
