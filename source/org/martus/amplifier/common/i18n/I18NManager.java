package org.martus.amplifier.common.i18n;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.martus.amplifier.common.logging.LoggerConstants;

public class I18NManager implements I18NConstants, LoggerConstants
{
	
	protected I18NManager()
	{
		super();
		initialize();
	}
	
	private void initialize()
	{
		Properties properties = new Properties();
    	try 
    	{
        	InputStream is = getClass().getResourceAsStream(I18N_PROPERTIES_FILE);
        	properties.load(is);
    	} 
    	catch (IOException e) 
    	{
    		logger.severe(I18NManager.getInstance().
    			getExceptionResourceBundle().
    			getString("exception_Unable_to_read_i18n_properties_file") + e.getLocalizedMessage());
    	}
    	
    	serverLanguage = properties.getProperty("language"); 
    	serverCountry = properties.getProperty("country");
    	serverVariant = properties.getProperty("variant");
    	
    	if(serverLanguage != null && serverCountry != null && serverVariant != null)
    		serverLocale = new Locale(serverLanguage, serverCountry, serverVariant);
		else if(serverLanguage != null && serverCountry != null)
			serverLocale = new Locale(serverLanguage, serverCountry);
		else if(serverLanguage != null)
			serverLocale = new Locale(serverLanguage);
		else
			serverLocale = Locale.ENGLISH;
			
		exceptionResourceBundle = 
			ResourceBundle.getBundle(EXCEPTION_RESOURCE_BUNDLE, getServerLocale());
		loggingResourceBundle = 
			ResourceBundle.getBundle(LOGGING_RESOURCE_BUNDLE, getServerLocale());
	}
	
	public static I18NManager getInstance()
	{
		return instance;
	}
	
	public Locale getServerLocale()
	{
		return serverLocale;
	}
	
	public ResourceBundle getLoggingResourceBundle()
	{
		return loggingResourceBundle;
	}
	
	public ResourceBundle getExceptionResourceBundle()
	{
		return exceptionResourceBundle;
	}
	
	private Logger logger = Logger.getLogger(I18N_LOGGER);
	private String serverLanguage = null;
	private String serverCountry = null;
	private String serverVariant = null;
	private Locale serverLocale = null;
	private ResourceBundle loggingResourceBundle = null;
	private ResourceBundle exceptionResourceBundle = null;
	private static I18NManager instance = new I18NManager();
}
