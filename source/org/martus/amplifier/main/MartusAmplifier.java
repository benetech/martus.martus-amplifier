package org.martus.amplifier.main;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import org.martus.amplifier.common.configuration.AmplifierConfiguration;

public class MartusAmplifier
{
	public static void main(String[] args)
	{	
		timer.scheduleAtFixedRate(timedTask, IMMEDIATELY, dataSynchIntervalMillis);

		while(! isShutdownRequested() )
		{
			;
		}
	}
	
	static void execute()
	{
		MartusAmplifierDataSynch amplifierDataSynch = new MartusAmplifierDataSynch();
		amplifierDataSynch.execute();
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
	
	static class UpdateFromServerTask extends TimerTask
	{	
		public void run()
		{
			if(! isAmplifierSyncing() )
			{
				startSynch();
				System.out.println("Scheduled Task started " + System.currentTimeMillis());

				execute();
				
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
