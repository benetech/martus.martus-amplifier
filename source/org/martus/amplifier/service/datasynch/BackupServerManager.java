package org.martus.amplifier.service.datasynch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class BackupServerManager implements IDataSynchConstants
{
	public BackupServerManager()
	{
		super();
		Properties backupServerProperties = new Properties();
		try
		{
			backupServerProperties.load(getClass().getResourceAsStream(BACKUP_SERVER_PROPERTIES));
		}
		catch(IOException ioe)
		{
			logger.severe("Unable to load backup server configuration file.");
		}
		Enumeration serverNames = backupServerProperties.propertyNames();
		String currentServerIP = null;
		String currentServerName = null;
		backupServersList = new ArrayList();
		while(serverNames.hasMoreElements())
		{
			currentServerName = (String) serverNames.nextElement();
			currentServerIP = backupServerProperties.getProperty(currentServerName);
			backupServersList.add(currentServerIP);
		}
	}

	public List getBackupServersList() 
	{
		return backupServersList;
	}
	
	public static BackupServerManager getInstance()
	{
		if(singleton != null)
			return singleton;
		singleton = new BackupServerManager();
		return singleton;
	}
	
	public static BackupServerManager singleton = null;
	private List backupServersList = null;
	private Logger logger = Logger.getLogger(DATASYNC_LOGGER);
}
