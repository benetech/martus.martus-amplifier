package org.martus.amplifier.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.martus.amplifier.attachment.test.TestAllAttachment;
import org.martus.amplifier.common.test.TestAllCommon;
import org.martus.amplifier.datasynch.test.TestAllDataSynch;
import org.martus.amplifier.lucene.test.TestAllLucene;
import org.martus.amplifier.main.test.TestAllMain;
import org.martus.amplifier.network.test.TestAllNetwork;
import org.martus.amplifier.presentation.test.TestAllPresentation;
import org.martus.amplifier.search.test.TestAllSearch;

public class TestAllAmplifier extends TestSuite
{
    public TestAllAmplifier() 
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
		suite.addTest(TestAllAttachment.suite());
		suite.addTest(TestAllLucene.suite());
		suite.addTest(TestAllDataSynch.suite());
		suite.addTest(TestAllPresentation.suite());
		suite.addTest(TestAllMain.suite());
		suite.addTest(TestAllCommon.suite());
		suite.addTest(TestAllSearch.suite());
		suite.addTest(TestAllNetwork.suite());
		
	    return suite;
	}
}
