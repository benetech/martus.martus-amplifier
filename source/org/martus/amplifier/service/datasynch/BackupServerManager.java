package org.martus.amplifier.service.datasynch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class BackupServerManager implements IDataSynchConstants
{
	public static List getBackupServersList() 
	{
		Logger logger = Logger.getLogger(DATASYNC_LOGGER);
		Properties backupServerProperties = new Properties();
		try
		{
			backupServerProperties.load(BackupServerManager.class.getResourceAsStream(BACKUP_SERVER_PROPERTIES));
		}
		catch(IOException ioe)
		{
			logger.severe("Unable to load backup server configuration file.");
		}

		String currentServerIP = null;
		String currentServerName = null;
		String currentServerPortString = null;
		String serverPublicKey = null;
		int currentServerPort = 0;
		
		List backupServersList = new ArrayList();
		currentServerName = backupServerProperties.getProperty(NAME_PROPERTY);
		currentServerIP = backupServerProperties.getProperty(IP_PROPERTY);
		currentServerPortString = backupServerProperties.getProperty(PORT_PROPERTY);
		serverPublicKey = backupServerProperties.getProperty(SERVERPUBLICKEY_PROPERTY);
		if(currentServerPortString != null)
			currentServerPort = Integer.parseInt(currentServerPortString);
		backupServersList.add(new BackupServerInfo(currentServerName,
			currentServerIP, currentServerPort, serverPublicKey));
		return backupServersList;
	}
	
	private static final String NAME_PROPERTY = "serverName";
	private static final String IP_PROPERTY = "serverIP";
	private static final String PORT_PROPERTY = "serverPort";
	private static final String SERVERPUBLICKEY_PROPERTY = "serverPublicKey";
}
