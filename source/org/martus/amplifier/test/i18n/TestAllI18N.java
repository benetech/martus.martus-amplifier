package org.martus.amplifier.test.i18n;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Daniel Chu
 *
 */
public class TestAllI18N extends TestSuite {
	public TestAllI18N()
	{
		super();
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
		TestSuite suite= new TestSuite("All Martus Amplifier i18n Tests");
		
		suite.addTest(new TestSuite(TestI18NManager.class));
	
	    return suite;
	}

}
