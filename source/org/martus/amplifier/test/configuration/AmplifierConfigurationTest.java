package org.martus.amplifier.test.configuration;

import junit.framework.TestCase;

import org.martus.amplifier.common.configuration.AmplifierConfiguration;

public class AmplifierConfigurationTest extends TestCase
{
	public void testGetTestDataPath()
	{
		String testDataPath = AmplifierConfiguration.getInstance().getTestDataPath();
		assertEquals("C:\\Development\\eclipse\\workspace\\martus-amplifier\\testdata", testDataPath);
	}

}
