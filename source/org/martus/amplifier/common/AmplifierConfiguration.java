package org.martus.amplifier.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class AmplifierConfiguration
{

	protected AmplifierConfiguration()
	{
		super();
		initialize();
	}
	
	private void initialize()
	{
		InputStream stream = getClass().getResourceAsStream(PATH_PROPERTY_FILE);
		pathProperties = new Properties();
		try
		{
			pathProperties.load(stream);
		}
		catch(IOException ioe)
		{
			logger.severe("Unable to load " + PATH_PROPERTY_FILE + " file. Default paths will be used.");
		}
	}
	
	public String getBasePath()
	{
		return getGenericProperty(AMPLIFIER_BASE_PATH);
	}
	
	public String getPacketsDirectory()
	{
		return getGenericProperty(AMPLIFIER_PACKETS_PATH);
	}
	
	public String getFeedbackDirectory()
	{
		return getGenericProperty(AMPLIFIER_FEEDBACK_PATH);
	}
	
	public String buildAmplifierBasePath(String directoryOrFile)
	{
		return buildGenericPath(getBasePath(), directoryOrFile);
	}
	
	private String buildGenericPath(String basePath, String directoryOrFile)
	{
		StringBuffer newPath = new StringBuffer(200); 
		newPath.append(basePath);
		newPath.append(File.separator);
		newPath.append(directoryOrFile);
		return newPath.toString();
	}
	
	private String getGenericProperty(String propertyKey)
	{
		String propertyValue = null;
		if(pathProperties != null)
			propertyValue = pathProperties.getProperty(propertyKey);
		return propertyValue;
	}
	
	public static AmplifierConfiguration getInstance()
	{
		return instance;
	}
	
	private Logger logger = Logger.getLogger("CONFIGURATION_LOGGER");
	private Properties pathProperties = null;
	
	public static final String DATASYNC_LOGGER = "DATASYNC_LOGGER";

	//property keys
	private static final String AMPLIFIER_BASE_PATH = "AMPLIFIER_BASE_DIRECTORY";
	private static final String AMPLIFIER_PACKETS_PATH = "AMPLIFIER_PACKETS_DIRECTORY";
	private static final String AMPLIFIER_FEEDBACK_PATH = "AMPLIFIER_FEEDBACK_DIRECTORY";
	
	private static final String PATH_PROPERTY_FILE = "/Path.properties";
	private static AmplifierConfiguration instance = new AmplifierConfiguration();
}
