package org.martus.amplifier.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.martus.amplifier.common.AmplifierConfiguration;
import org.martus.common.test.TestCaseEnhanced;
import org.martus.util.StreamCopier;

/**
 * @author dchu
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public abstract class TestAbstractAmplifier extends TestCaseEnhanced
{
	public TestAbstractAmplifier(String name)
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
	
	protected static InputStream stringToInputStream(String s) 
		throws UnsupportedEncodingException
	{
		return new ByteArrayInputStream(s.getBytes("UTF-8"));
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
