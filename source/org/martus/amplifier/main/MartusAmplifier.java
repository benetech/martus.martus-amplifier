package org.martus.amplifier.main;

import java.util.Timer;
import java.util.TimerTask;

public class MartusAmplifier
{

	public MartusAmplifier()
	{
		super();
	}

	public static void main(String[] args)
	{
		boolean isDaemonProcess = true;
		Timer timer = new Timer(isDaemonProcess);
		timer.scheduleAtFixedRate(timedTask, 0, 10000);
		while(true)
		{
			
		}
	}
	
	private static TimerTask timedTask = new DummyUpdateFromServerTask();
}
