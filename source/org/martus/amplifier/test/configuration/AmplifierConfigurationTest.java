package org.martus.amplifier.test.configuration;

import junit.framework.TestCase;

import org.martus.amplifier.common.configuration.AmplifierConfiguration;

public class AmplifierConfigurationTest extends TestCase
{
	public AmplifierConfigurationTest(String name)
	{
		super(name);
	}
	public void testGetBasePath()
	{
		String basePath = AmplifierConfiguration.getInstance().getBasePath();
		//assertEquals("C:\\Development\\eclipse\\workspace\\martus-amplifier", basePath);
	}

}
