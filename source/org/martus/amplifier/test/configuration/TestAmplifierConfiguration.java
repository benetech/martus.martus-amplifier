package org.martus.amplifier.test.configuration;

import junit.framework.TestCase;

import org.martus.amplifier.common.configuration.AmplifierConfiguration;

public class TestAmplifierConfiguration extends TestCase
{
	public TestAmplifierConfiguration(String name)
	{
		super(name);
	}
	public void testGetBasePath()
	{
		String basePath = AmplifierConfiguration.getInstance().getBasePath();
		//assertEquals("C:\\Development\\eclipse\\workspace\\martus-amplifier", basePath);
	}

}
