/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2003, Beneficent
Technology, Inc. (Benetech).

Martus is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later
version with the additions and exceptions described in the
accompanying Martus license file entitled "license.txt".

It is distributed WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, including warranties of fitness of purpose or
merchantability.  See the accompanying Martus License and
GPL license for more details on the required license terms
for this software.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.

*/
package org.martus.amplifier.main;

import java.io.File;
import java.io.IOException;
import java.util.TimerTask;

import org.martus.common.LoggerInterface;
import org.martus.common.LoggerToConsole;
import org.martus.common.MartusUtilities;
import org.martus.common.MartusUtilities.InvalidPublicKeyFileException;
import org.martus.common.MartusUtilities.PublicInformationInvalidException;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.crypto.MartusCrypto.CryptoInitializationException;
import org.martus.server.forclients.MartusServer;

public class StubServer extends MartusServer
{
	public static void main(String[] args)
	{
		try
		{
			displayVersion();
			StubServer server = new StubServer(StubServer.getDefaultDataDirectory());
			
			server.processCommandLine(args);
			server.deleteRunningFile();
			
			if(!server.hasAccount())
			{
				System.out.println("***** Key pair file not found *****");
				System.exit(2);
			}
			char[] passphrase = server.insecurePassword;
			if(passphrase == null)
				passphrase = server.getPassphraseFromConsole(server);
			server.loadAccount(passphrase);
			server.displayStatistics();
			server.initalizeAmplifier(passphrase);

			server.deleteStartupFiles();
			server.startBackgroundTimers();
			StubServer.writeSyncFile(server.getRunningFile());
			System.out.println("Waiting for connection...");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	StubServer(File dir) throws 
					CryptoInitializationException, IOException, InvalidPublicKeyFileException, PublicInformationInvalidException
	{
		this(dir, new LoggerToConsole());
	}
		
	public StubServer(File dir, LoggerInterface loggerToUse) throws 
	MartusCrypto.CryptoInitializationException, IOException, InvalidPublicKeyFileException, PublicInformationInvalidException
	{
		super(dir, loggerToUse);
	}

	public StubServer(File dir, LoggerInterface loggerToUse, MartusCrypto securityToUse) throws 
	MartusCrypto.CryptoInitializationException, IOException, InvalidPublicKeyFileException, PublicInformationInvalidException
	{
		super(dir, loggerToUse, securityToUse);
	}

	void processCommandLine(String[] args)
	{
		long indexEveryXMinutes = 0;
		String indexEveryXHourTag = "indexinghours=";
		String indexEveryXMinutesTag = "indexingminutes=";
		String ampipTag = "ampip=";

		for(int arg = 0; arg < args.length; ++arg)
		{
			String argument = args[arg];
			if(argument.equals("secure"))
				enterSecureMode();
			if(argument.equals("nopassword"))
				insecurePassword = "password".toCharArray();
			if(argument.startsWith(ampipTag))
				ampIpAddress = argument.substring(ampipTag.length());

			if(argument.startsWith(indexEveryXHourTag))
			{	
				String hours = argument.substring(indexEveryXHourTag.length());
				System.out.println("Indexing every " + hours + " hours");
				long indexEveryXHours = new Integer(hours).longValue();
				indexEveryXMinutes = indexEveryXHours * 60;
			}
			if(argument.startsWith(indexEveryXMinutesTag))
			{	
				String minutes = argument.substring(indexEveryXMinutesTag.length());
				System.out.println("Indexing every " + minutes + " minutes");
				indexEveryXMinutes = new Integer(minutes).longValue();
			}
		}
		if(indexEveryXMinutes==0)
		{
			long defaultSyncHours = MartusAmplifier.DEFAULT_HOURS_TO_SYNC;
			indexEveryXMinutes = defaultSyncHours * 60;
			System.out.println("Indexing every " + defaultSyncHours + " hours");
		}
		
		dataSynchIntervalMillis = indexEveryXMinutes * MINUTES_TO_MILLI;
		
		if(isSecureMode())
			System.out.println("Running in SECURE mode");
		else
			System.out.println("***RUNNING IN INSECURE MODE***");
	}

	protected void startBackgroundTimers()
	{
		super.startBackgroundTimers();
		MartusUtilities.startTimer(new UpdateFromServerTask(), dataSynchIntervalMillis);
	}
	
	public boolean canExitNow()
	{
		return !(amp.isAmplifierSyncing());
	}

	class UpdateFromServerTask extends TimerTask
	{	
		public void run()
		{
			if(! amp.isAmplifierSyncing() )
			{
				amp.startSynch();
				amp.pullNewDataFromServers(amp.backupServersList);
				amp.endSynch();
			}
		}
	}

	public void initalizeAmplifier(char[] keystorePassword) throws Exception
	{
		amp.initalizeAmplifier(keystorePassword);
	}
	private static final long MINUTES_TO_MILLI = 60 * 1000;

	private long dataSynchIntervalMillis;
	char[] insecurePassword;
}
