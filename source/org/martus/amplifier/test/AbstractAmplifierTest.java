package org.martus.amplifier.test;

import java.io.File;

import org.martus.amplifier.common.configuration.AmplifierConfiguration;
import org.martus.amplifier.service.search.SearchConstants;

import junit.framework.TestCase;

/**
 * @author dchu
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public abstract class AbstractAmplifierTest extends TestCase
{
	public AbstractAmplifierTest(String name)
	{
		super(name);
	}
	
	protected void setUp() throws Exception
	{
		super.setUp();
		basePath = AmplifierConfiguration.getInstance().buildAmplifierBasePath("test");
	}
	
	protected String getTestBasePath()
	{
		return basePath;
	}
	
	protected String getTestAttachmentPath()
	{
		return basePath + File.separator + "attachments";			
	}
	
	protected String getTestIndexPath()
	{
		return basePath + File.separator + SearchConstants.INDEX_DIR_NAME;
	}
	
	private String basePath;
}
