package org.martus.amplifier.test;

import org.martus.amplifier.test.attachment.TestAllAttachment;
import org.martus.amplifier.test.configuration.TestAllConfiguration;
import org.martus.amplifier.test.datasynch.TestAllDataSynch;
import org.martus.amplifier.test.i18n.TestAllI18N;
import org.martus.amplifier.test.search.TestAllSearch;

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
		suite.addTest(TestAllAttachment.suite());
		suite.addTest(TestAllConfiguration.suite());
		suite.addTest(TestAllDataSynch.suite());
		suite.addTest(TestAllI18N.suite());
		suite.addTest(TestAllSearch.suite());
		
	    return suite;
	}
}
