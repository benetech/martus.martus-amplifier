package org.martus.amplifier.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.martus.amplifier.common.AmplifierConfiguration;
import org.martus.common.test.TestCaseEnhanced;
import org.martus.util.StreamCopier;

public abstract class AbstractAmplifierTestCase extends TestCaseEnhanced
{
	public AbstractAmplifierTestCase(String name)
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
	
	protected String getTestBulletinPath()
	{
		return basePath + File.separator + "bulletins";
	}
	
	protected static String inputStreamToString(InputStream in) 
		throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new StreamCopier().copyStream(in, out);
		return out.toString("UTF-8");
	}
	
	private String basePath;
}