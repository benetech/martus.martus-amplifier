package org.martus.amplifier.main;

import java.util.Timer;
import java.util.TimerTask;
import org.martus.amplifier.main.MartusAmplifierDataSynch;

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
		MartusAmplifierDataSynch amplifierDataSynch = new MartusAmplifierDataSynch();
		amplifierDataSynch.execute();	
	}
	
	private static TimerTask timedTask = new DummyUpdateFromServerTask();
}
