package org.martus.amplifier.common.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class AmplifierConfiguration implements IConfigurationConstants
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
	
	public String getTestDataPath()
	{
		StringBuffer testDataPath = new StringBuffer(200); 
		testDataPath.append(getBasePath());
		testDataPath.append(File.separator);
		testDataPath.append(getGenericProperty(TEST_DATA_PATH));
		return testDataPath.toString();
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
	
	private Logger logger = Logger.getLogger(CONFIGURATION_LOGGER);
	private Properties pathProperties = null;
	
	//property keys
	private static final String AMPLIFIER_BASE_PATH = "AMPLIFIER_BASE_DIRECTORY";
	private static final String TEST_DATA_PATH = "TEST_DATA_DIRECTORY";
	
	private static final String PATH_PROPERTY_FILE = "/Path.properties";
	private static AmplifierConfiguration instance = new AmplifierConfiguration();
}
