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
	
	public String getWorkingPath()
	{
		return getGenericProperty(AMPLIFIER_WORKING_PATH);
	}
	
	public String buildAmplifierBasePath(String directoryOrFile)
	{
		return buildGenericPath(getBasePath(), directoryOrFile);
	}
	
	public String buildAmplifierBasePath(String folder, String file)
	{
		return buildGenericPath(getBasePath(), folder, file);
	}

	public String buildAmplifierWorkingPath(String directoryOrFile)
	{
		return buildGenericPath(getWorkingPath(), directoryOrFile);
	}
	
	public String buildAmplifierWorkingPath(String folder, String file)
	{
		return buildGenericPath(getWorkingPath(), folder, file);
	}

	private String buildGenericPath(String basePath, String folder, String file)
	{
		StringBuffer newPath = new StringBuffer(buildGenericPath(basePath, folder));
		newPath.append(File.separator);
		newPath.append(file);
		return newPath.toString();
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
	
	private Logger logger = Logger.getLogger(CONFIGURATION_LOGGER);
	private Properties pathProperties = null;
	
	//property keys
	private static final String AMPLIFIER_BASE_PATH = "AMPLIFIER_BASE_DIRECTORY";
	private static final String AMPLIFIER_WORKING_PATH = "AMPLIFIER_WORKING_DIRECTORY";
	
	private static final String PATH_PROPERTY_FILE = "/Path.properties";
	private static AmplifierConfiguration instance = new AmplifierConfiguration();
}
