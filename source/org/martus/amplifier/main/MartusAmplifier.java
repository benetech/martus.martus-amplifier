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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.TimerTask;
import java.util.Vector;

import org.martus.amplifier.attachment.DataManager;
import org.martus.amplifier.attachment.FileSystemDataManager;
import org.martus.amplifier.common.MarketingVersionNumber;
import org.martus.amplifier.datasynch.BackupServerInfo;
import org.martus.amplifier.datasynch.DataSynchManager;
import org.martus.amplifier.lucene.LuceneBulletinIndexer;
import org.martus.amplifier.network.AmplifierClientSideNetworkHandlerUsingXMLRPC.SSLSocketSetupException;
import org.martus.amplifier.search.BulletinIndexException;
import org.martus.amplifier.search.BulletinIndexer;
import org.martus.common.LoggerInterface;
import org.martus.common.LoggerToConsole;
import org.martus.common.MartusUtilities;
import org.martus.common.VersionBuildDate;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.crypto.MartusSecurity;
import org.martus.common.crypto.MartusCrypto.AuthorizationFailedException;
import org.martus.common.crypto.MartusCrypto.CryptoInitializationException;
import org.martus.common.crypto.MartusCrypto.InvalidKeyPairFileVersionException;
import org.martus.util.UnicodeReader;
import org.martus.util.Base64.InvalidBase64Exception;
import org.mortbay.http.HttpContext;
import org.mortbay.http.SunJsseListener;
import org.mortbay.jetty.Server;
import org.mortbay.util.InetAddrPort;
import org.mortbay.util.MultiException;

public class MartusAmplifier
{
	public static void main(String[] args) throws Exception
	{
		displayVersion();

		File dataDirectory = MartusAmplifier.getDefaultDataDirectory();
		MartusAmplifier amp = new MartusAmplifier(dataDirectory, new LoggerToConsole());
		
		amp.processCommandLine(args);
		amp.deleteRunningFile();
		
		if(!amp.hasAccount())
		{
			System.out.println("***** Key pair file not found *****");
			serverExit(2);
		}
		String passphrase = insecurePassword;
		if(passphrase == null)
			passphrase = getPassphraseFromConsole();
		amp.loadAccount(passphrase);
		amp.displayStatistics();
		amp.start(passphrase);
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

		if(!getKeystoreFile().delete())
		{
			System.out.println("Unable to delete keystore");
			System.exit(6);
		}
		
		File serversWhoWeCallDir = getServersWhoWeCallDirectory();
		if(serversWhoWeCallDir.exists())
		{
			File[] toDeleteFile = serversWhoWeCallDir.listFiles();
			if(toDeleteFile != null)
			{
				for (int i = 0; i < toDeleteFile.length; i++)
				{
					File toCallFile = toDeleteFile[i];
					if(!toCallFile.delete())
					{
						System.out.println("Unable to delete file: " + toCallFile.getAbsolutePath());
						System.exit(7);
					}
				}
			}
			if(!serversWhoWeCallDir.delete())
			{
				System.out.println("Unable to delete Dir: " + serversWhoWeCallDir.getAbsolutePath());
				System.exit(7);
			}
		}

		File notAmplifiedAccountsFile = getAccountsNotAmplifiedFile();
		if(notAmplifiedAccountsFile.exists())
		{	
			if(!notAmplifiedAccountsFile.delete())
			{
				System.out.println("Unable to delete File: " + notAmplifiedAccountsFile.getAbsolutePath());
				System.exit(8);
			}
		}

		File jettyconfig = new File(getStartupConfigDirectory(), "jettyConfiguration.xml");
		if(jettyconfig.exists())
		{	
			if(!jettyconfig.delete())
			{
				System.out.println("Unable to delete File: " + jettyconfig.getAbsolutePath());
				System.exit(9);
			}
		}
	}
	
	
	static public void serverExit(int exitCode) 
	{
		System.exit(exitCode);
	}

	
	public MartusAmplifier(File dataDirectoryToUse, LoggerInterface loggerToUse) throws CryptoInitializationException
	{
		dataDirectory = dataDirectoryToUse;
		logger = loggerToUse;
		security = new MartusSecurity();
	}

	void start(String password) throws Exception
	{
		deleteLuceneLockFile();
		String packetsDirectory = new File(getBasePath(), "ampPackets").getPath();

		dataManager = new FileSystemDataManager(packetsDirectory);
		
		File backupServersDirectory = getServersWhoWeCallDirectory();
		backupServersList = loadServersWeWillCall(backupServersDirectory, security);
		
		File notAmplifiedAccountsFile = getAccountsNotAmplifiedFile();
		loadAccountsWeWillNotAmplify(notAmplifiedAccountsFile);
		log(notAmplifiedAccountsList.size() + " account(s) will not get amplified");

		
		//Code.setDebug(true);
		File indexDir = LuceneBulletinIndexer.getIndexDir(getBasePath());
		File languages = new File(indexDir, "languagesIndexed.txt");
		languagesIndexed = new LanguagesIndexedList(languages);
		try
		{
			languagesIndexed.loadLanguagesAlreadyIndexed();
		}
		catch (IOException e)
		{
			log("Error: LanguagesIndex" + e);
		}
		
		try
		{
			startServers(password);
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			serverExit(3);
		}
		deleteStartupFiles();

		startBackgroundTimers();

		writeSyncFile(getRunningFile());
		System.out.println("Waiting for connection...");
	}

	void startBackgroundTimers()
	{
		MartusUtilities.startTimer(new UpdateFromServerTask(), dataSynchIntervalMillis);
		MartusUtilities.startTimer(new ShutdownRequestMonitor(), shutdownRequestIntervalMillis);
	}
	

	private File getServersWhoWeCallDirectory()
	{
		return new File(getStartupConfigDirectory(), SERVERS_WHO_WE_CALL_DIRIRECTORY);
	}

	private File getAccountsNotAmplifiedFile()
	{
		return new File(getStartupConfigDirectory(), ACCOUNTS_NOT_AMPLIFIED_FILE);
	}

	private void startServers(String password) throws IOException, MultiException
	{
		startSSLServer(password);
		startNonSSLServer();
	}

	private void startNonSSLServer() throws IOException, MultiException
	{
		Server nonsslServer = new Server();
		nonsslServer.addWebApplication("", getPresentationBasePath() + "presentationNonSSL");
		InetAddrPort nonssllistener = new InetAddrPort(80);
		nonssllistener.setInetAddress(getAmpIpAddress());
		nonsslServer.addListener(nonssllistener);
		
		nonsslServer.start();
	}

	private void startSSLServer(String password) throws IOException, MultiException
	{
		SunJsseListener sslListener = new SunJsseListener(new InetAddrPort(443));
		sslListener.setInetAddress(getAmpIpAddress());
		sslListener.setPassword(password);
		sslListener.setKeyPassword(password);
		sslListener.setMaxIdleTimeMs(MAX_IDLE_TIME_MS);
		sslListener.setMaxThreads(MAX_THREADS);
		sslListener.setMinThreads(MIN_THREADS);
		sslListener.setLowResourcePersistTimeMs(LOW_RESOURCE_PERSIST_TIME_MS);
		File jettyKeystore = getKeystoreFile();
		sslListener.setKeystore(jettyKeystore.getAbsolutePath());

		//File jettyXmlFile = new File(getStartupConfigDirectory(), "jettyConfiguration.xml");
		//Server sslServer = new Server(jettyXmlFile.getAbsolutePath());
		Server sslServer = new Server();

		sslServer.addWebApplication("", getPresentationBasePath() + "presentation");
		addPasswordAuthentication(sslServer);
		sslServer.addListener(sslListener);
		sslServer.start();
	}

	private File getKeystoreFile()
	{
		return new File(getStartupConfigDirectory(), "keystore");
	}

	private void processCommandLine(String[] args)
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
				insecurePassword = "password";
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
			indexEveryXMinutes = DEFAULT_HOURS_TO_SYNC * 60;
			System.out.println("Indexing every " + DEFAULT_HOURS_TO_SYNC + " hours");
		}
		
		dataSynchIntervalMillis = indexEveryXMinutes * MINITUES_TO_MILLI;
		
		if(isSecureMode())
			System.out.println("Running in SECURE mode");
		else
			System.out.println("***RUNNING IN INSECURE MODE***");
	}
	
	private static InetAddress getAmpIpAddress() throws UnknownHostException
	{
		return InetAddress.getByName(ampIpAddress);
	}

	private void displayStatistics() throws InvalidBase64Exception
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
	
	private void deleteRunningFile()
	{
		getRunningFile().delete();
	}

	private File getRunningFile()
	{
		File runningFile = new File(getTriggerDirectory(), AMP_RUNNING_FILE);
		return runningFile;
	}

	static public File getShutdownFile()
	{
		return new File(getTriggerDirectory(), EXIT_AMP_FILE);
	}

	public boolean isSecureMode()
	{
		return secureMode;
	}
	
	public void enterSecureMode()
	{
		secureMode = true;
	}
	
	static File getTriggerDirectory()
	{
		return new File(getBasePath(), ADMIN_TRIGGER_DIRECTORY);
		
	}

	public static String getBasePath()
	{
		return getDataDirectory().getPath();
	}
	
	public File getStartupConfigDirectory()
	{
		return new File(getBasePath(), ADMIN_STARTUP_CONFIG_DIRECTORY);
	}

	boolean hasAccount()
	{
		return getKeyPairFile().exists();
	}
	
	File getKeyPairFile()
	{
		return new File(getStartupConfigDirectory(), KEYPAIR_FILENAME);
	}

	void deleteLuceneLockFile() throws BulletinIndexException
	{
		File indexDirectory = LuceneBulletinIndexer.getIndexDir(getBasePath());
		File lockFile = new File(indexDirectory, "write.lock");
		if(lockFile.exists())
		{
			log("Deleting lucene lock file: " + lockFile.getPath());
			lockFile.delete();
		}
	}

	void addPasswordAuthentication(Server server)
	{
		PasswordAuthenticationHandler handler = new PasswordAuthenticationHandler();
		HttpContext context = server.getContext("/");
		context.addHandler(handler);
	}
	
	private static String getPassphraseFromConsole()
	{
		System.out.print("Enter passphrase: ");
		System.out.flush();
		
		File waitingFile = new File(getTriggerDirectory(), AMP_WAITING_FILE);
		waitingFile.delete();
		writeSyncFile(waitingFile);
		
		InputStreamReader rawReader = new InputStreamReader(System.in);	
		BufferedReader reader = new BufferedReader(rawReader);
		String passphrase = null;
		try
		{
			passphrase = reader.readLine();
		}
		catch(Exception e)
		{
			System.out.println("MartusServer.main: " + e);
			System.exit(3);
		}
		return passphrase;
	}

	void loadAccount(String passphrase) throws AuthorizationFailedException, InvalidKeyPairFileVersionException, IOException
	{
		FileInputStream in = new FileInputStream(getKeyPairFile());
		readKeyPair(in, passphrase);
		in.close();
		System.out.println("Passphrase correct.");			
	}
	
	void readKeyPair(InputStream in, String passphrase) throws 
		IOException,
		MartusCrypto.AuthorizationFailedException,
		MartusCrypto.InvalidKeyPairFileVersionException
	{
		security.readKeyPair(in, passphrase);
	}
	
	static public boolean isShutdownRequested()
	{
		return(getShutdownFile().exists());
	}
	
	public boolean canExitNow()
	{
		return !(isAmplifierSyncing());
	}
	
	MartusSecurity getSecurity()
	{
		return security;
	}

	public boolean isAmplifierSyncing()
	{
		return isSyncing;
	}
	
	public void startSynch()
	{
		isSyncing = true;
	}
	
	public void endSynch()
	{
		isSyncing = false;
	}

	public void pullNewDataFromServers(List backupServersList) 
	{
		for(int i=0; i < backupServersList.size(); ++i)
		{
			if(isShutdownRequested())
				return;
			BackupServerInfo backupServerToCall = (BackupServerInfo)backupServersList.get(i);
			pullNewDataFromOneServer(backupServerToCall);
		}
	}

	private void pullNewDataFromOneServer(BackupServerInfo backupServerToCall)
	{
		BulletinIndexer indexer = null;
		try
		{
			DataSynchManager dataSyncManager = new DataSynchManager(backupServerToCall, logger, getSecurity());
			indexer = new LuceneBulletinIndexer(getBasePath());
		
			dataSyncManager.getAllNewData(dataManager, indexer, getListOfAccountsWeWillNotAmplify());
		}
		catch(Exception e)
		{
			log("MartusAmplifierDataSynch.execute(): " + e.getMessage());
			e.printStackTrace();
		} 
		finally
		{
			if (indexer != null) 
			{
				try 
				{
					indexer.close();
				} 
				catch (BulletinIndexException e) 
				{
					log("Unable to close the indexer: " + e.getMessage());
				}
			}
		}
	}

	public void loadAccountsWeWillNotAmplify(File notAmplifiedAccountsFile) throws IOException
	{
		if(notAmplifiedAccountsFile == null || !notAmplifiedAccountsFile.exists())
		{	
			notAmplifiedAccountsList = new Vector();
			return;
		}
		
		try
		{
			UnicodeReader reader = new UnicodeReader(notAmplifiedAccountsFile);
			notAmplifiedAccountsList = MartusUtilities.loadListFromFile(reader);
			reader.close();
		}
		catch(Exception e)
		{
			log("Error: loadAccountsWeWillNotAmplify" + e);
			throw new IOException(e.toString());
		}
	}
	
	public List getListOfAccountsWeWillNotAmplify()
	{
		return notAmplifiedAccountsList;
	}
	
	public List loadServersWeWillCall(File directory, MartusCrypto security) throws 
			IOException, MartusUtilities.InvalidPublicKeyFileException, MartusUtilities.PublicInformationInvalidException, SSLSocketSetupException
	{
		List serversWeWillCall = new Vector();
	
		File[] toCallFiles = directory.listFiles();
		if(toCallFiles != null)
		{
			for (int i = 0; i < toCallFiles.length; i++)
			{
				File toCallFile = toCallFiles[i];
				if(!toCallFile.isDirectory())
				{
					serversWeWillCall.add(getServerToCall(toCallFile, security));
					log("We will call: " + toCallFile.getName());
				}
			}
		}

		log("Configured to call " + serversWeWillCall.size() + " servers");
		return serversWeWillCall;
	}

	BackupServerInfo getServerToCall(File publicKeyFile, MartusCrypto security) throws
			IOException, 
			MartusUtilities.InvalidPublicKeyFileException, 
			MartusUtilities.PublicInformationInvalidException, 
			SSLSocketSetupException
	{
		String ip = MartusUtilities.extractIpFromFileName(publicKeyFile.getName());
		int port = 985;
		Vector publicInfo = MartusUtilities.importServerPublicKeyFromFile(publicKeyFile, security);
		String publicKey = (String)publicInfo.get(0);
	
		return new BackupServerInfo(ip, ip, port, publicKey);		
	}
	
	void log(String message)
	{
		logger.log(message);
	}
	
	class UpdateFromServerTask extends TimerTask
	{	
		public void run()
		{
			if(! isAmplifierSyncing() )
			{
				startSynch();
				pullNewDataFromServers(backupServersList);
				endSynch();
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


	private static void displayVersion()
	{
		System.out.println("MartusAmplifier");
		System.out.println("Version " + MarketingVersionNumber.marketingVersionNumber);
		String versionInfo = VersionBuildDate.getVersionBuildDate();
		System.out.println("Build Date " + versionInfo);
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
	
	public static String getPresentationBasePath()
	{
		String presentationBasePath = null;
		if(isRunningUnderWindows())
			presentationBasePath = "";
		else
			presentationBasePath = "/usrlocal/martus/htdocs/MartusAmplifier/";
		return presentationBasePath;
		
	}

	public static String getDefaultDataDirectoryPath()
	{
		String dataDirectory = null;
		if(isRunningUnderWindows())
			dataDirectory = "C:/MartusServer/";
		else
			dataDirectory = "/var/MartusServer/";
		return dataDirectory;
	}
	
	private static boolean isRunningUnderWindows()
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
	
	public static File getDataDirectory()
	{
		return dataDirectory;
	}

	boolean secureMode;
	private static String ampIpAddress;
	static String insecurePassword;
	public static File dataDirectory;	

	public static MartusSecurity security;
	static final long IMMEDIATELY = 0;
	static final long MINITUES_TO_MILLI = 60 * 1000;
	static final long DEFAULT_HOURS_TO_SYNC = 24;
	long dataSynchIntervalMillis;

	boolean isSyncing;
	
	List backupServersList;
	List notAmplifiedAccountsList;
	
	LoggerInterface logger;

	public static LanguagesIndexedList languagesIndexed;
	public static DataManager dataManager;

	private static final String ADMIN_TRIGGER_DIRECTORY = "adminTriggers";
	private static final String ADMIN_STARTUP_CONFIG_DIRECTORY = "deleteOnStartup";
	private static final String SERVERS_WHO_WE_CALL_DIRIRECTORY = "serversWhoWeCall";
	private static final String KEYPAIR_FILENAME = "keypair.dat";
	private static final String ACCOUNTS_NOT_AMPLIFIED_FILE = "accountsNotAmplified.txt";
	private static final String EXIT_AMP_FILE = "exit";
	private static final String AMP_RUNNING_FILE = "running";
	private static final String AMP_WAITING_FILE = "waiting";
	

	private static final int MAX_IDLE_TIME_MS = 30000;
	private static final int LOW_RESOURCE_PERSIST_TIME_MS = 5000;
	private static final int MIN_THREADS = 5;
	private static final int MAX_THREADS = 255;

	private static final long shutdownRequestIntervalMillis = 1000;
}
