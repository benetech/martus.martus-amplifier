package org.martus.amplifier.main;

import java.util.TimerTask;
import java.util.logging.Logger;

public class DummyUpdateFromServerTask extends TimerTask
{

	public DummyUpdateFromServerTask()
	{
		super();
	}

	public void run()
	{
		logger.info("Scheduled Task started" + System.currentTimeMillis());
		logger.info("Scheduled Task finished" + System.currentTimeMillis());
	}

	private static Logger logger = Logger.getLogger("UpdateTask");
}
