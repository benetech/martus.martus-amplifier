package org.martus.amplifier.test.configuration;

import org.martus.amplifier.common.configuration.AmplifierConfiguration;
import org.martus.common.test.TestCaseEnhanced;

public class TestAmplifierConfiguration extends TestCaseEnhanced
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
