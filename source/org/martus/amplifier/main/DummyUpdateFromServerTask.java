package org.martus.amplifier.main;

import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.martus.amplifier.service.datasynch.BackupServerInfo;
import org.martus.amplifier.service.datasynch.BackupServerManager;

public class DummyUpdateFromServerTask extends TimerTask
{

	public DummyUpdateFromServerTask()
	{
		super();
	}

	public void run()
	{
		logger.info("Scheduled Task started " + System.currentTimeMillis());
		List backupServers = 
			BackupServerManager.getInstance().getBackupServersList();
		if(backupServers != null)
		{
			Iterator iterator = backupServers.iterator();
			BackupServerInfo info = null;
			while(iterator.hasNext())
			{
				info = (BackupServerInfo) iterator.next();
				logger.info("server:" + info.getName());
			}
		}
		logger.info("Scheduled Task finished " + System.currentTimeMillis());
	}

	private static Logger logger = Logger.getLogger("UpdateTask");
}
