package org.martus.amplifier.test.search;

import java.util.logging.Logger;

import org.martus.amplifier.service.search.IBulletinConstants;
import org.martus.amplifier.service.search.ISearchConstants;
import org.martus.amplifier.test.AbstractAmplifierTest;

public abstract class AbstractAmplifierSearchTest 
	extends AbstractAmplifierTest 
	implements IBulletinConstants, ISearchConstants
{
	public AbstractAmplifierSearchTest(String name)
	{
		super(name);
	}
	
	protected static Logger getLogger()
	{
		return LOGGER;
	}
	
	private static final Logger LOGGER =
		Logger.getLogger(SEARCH_LOGGER);
}
