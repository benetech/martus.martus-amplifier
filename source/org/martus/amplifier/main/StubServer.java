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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.TimerTask;

import org.martus.amplifier.common.MarketingVersionNumber;
import org.martus.common.LoggerInterface;
import org.martus.common.LoggerToConsole;
import org.martus.common.MartusUtilities;
import org.martus.common.VersionBuildDate;
import org.martus.common.MartusUtilities.InvalidPublicKeyFileException;
import org.martus.common.MartusUtilities.PublicInformationInvalidException;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.crypto.MartusSecurity;
import org.martus.common.crypto.MartusCrypto.AuthorizationFailedException;
import org.martus.common.crypto.MartusCrypto.CryptoInitializationException;
import org.martus.common.crypto.MartusCrypto.InvalidKeyPairFileVersionException;
import org.martus.util.Base64.InvalidBase64Exception;


public class StubServer 
{
	public static void main(String[] args) throws Exception
	{
		displayVersion();
		StubServer server = new StubServer(StubServer.getDefaultDataDirectory());
		
		server.processCommandLine(args);
		server.deleteRunningFile();
		
		if(!server.hasAccount())
		{
			System.out.println("***** Key pair file not found *****");
			serverExit(2);
		}
		char[] passphrase = server.insecurePassword;
		if(passphrase == null)
			passphrase = server.getPassphraseFromConsole();
		server.loadAccount(passphrase);
		server.displayStatistics();
		server.initalizeAmplifier(passphrase);

		server.deleteStartupFiles();
		server.startBackgroundTimers();
		StubServer.writeSyncFile(server.getRunningFile());
		System.out.println("Waiting for connection...");
	}
	
	static public void serverExit(int exitCode) 
	{
		System.exit(exitCode);
	}

	StubServer(File dir) throws 
					CryptoInitializationException, IOException, InvalidPublicKeyFileException, PublicInformationInvalidException
	{
		this(dir, new LoggerToConsole());
	}
		
	public StubServer(File dir, LoggerInterface loggerToUse) throws 
	MartusCrypto.CryptoInitializationException, IOException, InvalidPublicKeyFileException, PublicInformationInvalidException
	{
		dataDirectory = dir;
		logger = loggerToUse;
		amp = new MartusAmplifier(this);
		
		security = new MartusSecurity();
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
		
		dataSynchIntervalMillis = indexEveryXMinutes * MINITUES_TO_MILLI;
		
		if(isSecureMode())
			System.out.println("Running in SECURE mode");
		else
			System.out.println("***RUNNING IN INSECURE MODE***");
	}

	private static void displayVersion()
	{
		System.out.println("MartusAmplifier");
		System.out.println("Version " + MarketingVersionNumber.marketingVersionNumber);
		String versionInfo = VersionBuildDate.getVersionBuildDate();
		System.out.println("Build Date " + versionInfo);
	}

	public boolean isSecureMode()
	{
		return secureMode;
	}
	
	public void enterSecureMode()
	{
		secureMode = true;
	}
	
	void deleteRunningFile()
	{
		getRunningFile().delete();
	}

	File getRunningFile()
	{
		File runningFile = new File(getTriggerDirectory(), AMP_RUNNING_FILE);
		return runningFile;
	}

	boolean hasAccount()
	{
		return getKeyPairFile().exists();
	}
	
	File getKeyPairFile()
	{
		return new File(getStartupConfigDirectory(), KEYPAIR_FILENAME);
	}

	void loadAccount(char[] passphrase) throws AuthorizationFailedException, InvalidKeyPairFileVersionException, IOException
	{
		FileInputStream in = new FileInputStream(getKeyPairFile());
		readKeyPair(in, passphrase);
		in.close();
		System.out.println("Passphrase correct.");			
	}
	
	void readKeyPair(InputStream in, char[] passphrase) throws 
		IOException,
		MartusCrypto.AuthorizationFailedException,
		MartusCrypto.InvalidKeyPairFileVersionException
	{
		security.readKeyPair(in, passphrase);
	}
	
	void displayStatistics() throws InvalidBase64Exception
	{
		displayServerAccountId();
		displayServerPublicCode();
	}
	
	private String displayServerAccountId()
	{
		String accountId = getAccountId();
		System.out.println("Server Account: " + accountId);
		System.out.println();
		return accountId;
	}

	private void displayServerPublicCode() throws InvalidBase64Exception
	{
		System.out.print("Server Public Code: ");
		String accountId = getAccountId();
		String publicCode = MartusCrypto.computePublicCode(accountId);
		System.out.println(MartusCrypto.formatPublicCode(publicCode));
		System.out.println();
	}

	public String getAccountId()
	{
		return security.getPublicKeyString();
	}
	
	public void deleteStartupFiles()
	{
		if(!isSecureMode())
			return;

		if(!getKeyPairFile().delete())
		{
			System.out.println("Unable to delete keypair");
			System.exit(5);
		}

		amp.deleteAmplifierStartupFiles();
	}


	void startBackgroundTimers()
	{
		MartusUtilities.startTimer(new UpdateFromServerTask(), dataSynchIntervalMillis);
		MartusUtilities.startTimer(new ShutdownRequestMonitor(), shutdownRequestIntervalMillis);
	}
	
	public boolean isShutdownRequested()
	{
		return(getShutdownFile().exists());
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

	private class ShutdownRequestMonitor extends TimerTask
	{
		public void run()
		{
			if( isShutdownRequested() && canExitNow() )
			{
				log("Shutdown request received.");
				getShutdownFile().delete();
				log("Server has exited.");
				try
				{
					serverExit(0);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	void log(String message)
	{
		logger.log(message);
	}
	
	public void initalizeAmplifier(char[] keystorePassword) throws Exception
	{
		amp.initalizeAmplifier(keystorePassword);
	}
		
	public File getShutdownFile()
	{
		return new File(getTriggerDirectory(), EXIT_AMP_FILE);
	}

	File getTriggerDirectory()
	{
		return new File(getDataDirectory(), ADMIN_TRIGGER_DIRECTORY);
			
	}

	public File getStartupConfigDirectory()
	{
		return new File(getDataDirectory(), ADMIN_STARTUP_CONFIG_DIRECTORY);
	}

	char[] getPassphraseFromConsole()
	{
		System.out.print("Enter passphrase: ");
		System.out.flush();
			
		File waitingFile = new File(getTriggerDirectory(), AMP_WAITING_FILE);
		waitingFile.delete();
		StubServer.writeSyncFile(waitingFile);
			
		InputStreamReader rawReader = new InputStreamReader(System.in);	
		BufferedReader reader = new BufferedReader(rawReader);
		String passphrase = null;
		try
		{
			//Security issue passphrase is a String
			passphrase = reader.readLine();
		}
		catch(Exception e)
		{
			System.out.println("MartusServer.main: " + e);
			System.exit(3);
		}
		return passphrase.toCharArray();
	}

	public static void writeSyncFile(File syncFile) 
	{
		try 
		{
			FileOutputStream out = new FileOutputStream(syncFile);
			out.write(0);
			out.close();
		} 
		catch(Exception e) 
		{
			System.out.println("MartusServer.main: " + e);
			System.exit(6);
		}
	}

	public static String getDefaultDataDirectoryPath()
	{
		String dataDirectory = null;
		if(StubServer.isRunningUnderWindows())
			dataDirectory = "C:/MartusServer/";
		else
			dataDirectory = "/var/MartusServer/";
		return dataDirectory;
	}

	static boolean isRunningUnderWindows()
	{
		return System.getProperty("os.name").indexOf("Windows") >= 0;
	}

	public static File getDefaultDataDirectory()
	{
		File file = new File(getDefaultDataDirectoryPath());
		if(!file.exists())
		{
			file.mkdirs();
		}
			
		return file;
	}

	public File getDataDirectory()
	{
		return dataDirectory;
	}


	static final long MINITUES_TO_MILLI = 60 * 1000;
	private static final String ADMIN_TRIGGER_DIRECTORY = "adminTriggers";
	private static final String ADMIN_STARTUP_CONFIG_DIRECTORY = "deleteOnStartup";
	private static final String KEYPAIR_FILENAME = "keypair.dat";
	private static final String EXIT_AMP_FILE = "exit";
	private static final String AMP_RUNNING_FILE = "running";
	private static final String AMP_WAITING_FILE = "waiting";
	private static final long shutdownRequestIntervalMillis = 1000;

	long dataSynchIntervalMillis;
	boolean secureMode;
	public File dataDirectory;
	LoggerInterface logger;
	public MartusAmplifier amp;
	char[] insecurePassword;
	String ampIpAddress;

	// NOTE: The following members *MUST* be static because they are 
	// used by servlets that do not have access to a server object! 
	// USE THEM CAREFULLY!
	public static MartusSecurity security;
}
