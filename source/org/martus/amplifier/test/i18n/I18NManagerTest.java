package org.martus.amplifier.test.i18n;

import java.util.Locale;

import org.martus.amplifier.common.i18n.I18NManager;
import org.martus.amplifier.test.AbstractAmplifierTest;

public class I18NManagerTest extends AbstractAmplifierTest
{
	public I18NManagerTest(String name)
	{
		super(name);
	}
	
	public void testGetServerLocale()
	{
		Locale locale = I18NManager.getInstance().getServerLocale();
		assertNotNull(locale);
	}

}
