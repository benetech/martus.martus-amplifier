package org.martus.amplifier.main;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.martus.amplifier.common.configuration.AmplifierConfiguration;
import org.martus.amplifier.service.attachment.AttachmentManager;
import org.martus.amplifier.service.attachment.AttachmentStorageException;
import org.martus.amplifier.service.attachment.filesystem.FileSystemAttachmentManager;
import org.martus.amplifier.service.datasynch.BackupServerManager;
import org.martus.amplifier.service.datasynch.DataSynchManager;
import org.martus.amplifier.service.search.BulletinIndexException;
import org.martus.amplifier.service.search.BulletinIndexer;
import org.martus.amplifier.service.search.lucene.LuceneBulletinIndexer;
import org.mortbay.http.SocketListener;
import org.mortbay.jetty.Server;

public class MartusAmplifier
{
	public static void main(String[] args) throws Exception
	{	
		timer.scheduleAtFixedRate(timedTask, IMMEDIATELY, dataSynchIntervalMillis);

		SocketListener listener = new SocketListener();
		listener.setPort(8080); 

		Server server = new Server();
		server.addListener(listener);
		server.addWebApplication("/","presentation/");
		server.start();

		while(! isShutdownRequested() )
		{
		}
	}
	
	static boolean isShutdownRequested()
	{
		String ampDir = AmplifierConfiguration.getInstance().getWorkingPath();
		File shutdownFile = new File(ampDir, "shutdown");
		boolean doShutdown = false;
		if(shutdownFile.exists() && ! isAmplifierSyncing() )
		{
			shutdownFile.delete();
			doShutdown = true;
		}
		return doShutdown;
	}
	
	static public boolean isAmplifierSyncing()
	{
		return isSyncing;
	}
	
	static public void startSynch()
	{
		isSyncing = true;
	}
	
	static public void endSynch()
	{
		isSyncing = false;
	}

	static public void pullNewBulletinsFromServers(List backupServersList) 
	{
		Logger logger = Logger.getLogger("MainTask");
				
		BulletinIndexer indexer = null;
		AttachmentManager attachmentManager = null;
		try
		{
			DataSynchManager dataManager = new DataSynchManager(backupServersList);
			AmplifierConfiguration config = 
				AmplifierConfiguration.getInstance();
			indexer = new LuceneBulletinIndexer(
				config.getBasePath());
			attachmentManager = new FileSystemAttachmentManager(
				config.getBasePath());
	
			dataManager.getAllNewBulletins(attachmentManager, indexer);
		}
		catch(Exception e)
		{
			logger.severe("MartusAmplifierDataSynch.execute(): " + e.getMessage());
			e.printStackTrace();
		} 
		finally
		{
			if (indexer != null) {
				try {
					indexer.close();
				} catch (BulletinIndexException e) {
					logger.severe(
						"Unable to close the indexer: " + e.getMessage());
				}
			}
			
			if (attachmentManager != null) {
				try {
					attachmentManager.close();
				} catch (AttachmentStorageException e) {
					logger.severe(
						"Unable to close the attachment manager: " +
						e.getMessage());
				}
			}
		}
	}
	
	static class UpdateFromServerTask extends TimerTask
	{	
		public void run()
		{
			if(! isAmplifierSyncing() )
			{
				startSynch();
				System.out.println("Scheduled Task started " + System.currentTimeMillis());

				List backupServersList = new BackupServerManager().getBackupServersList();
				MartusAmplifier.pullNewBulletinsFromServers(backupServersList);
				
				System.out.println("Scheduled Task finished " + System.currentTimeMillis() + "\n");
				endSynch();
			}
		}
	}
	static Timer timer = new Timer(true);
	static TimerTask timedTask = new UpdateFromServerTask();
	static final long IMMEDIATELY = 0;
	static final long dataSynchIntervalMillis = 100000;
	static boolean isSyncing;
	
}
