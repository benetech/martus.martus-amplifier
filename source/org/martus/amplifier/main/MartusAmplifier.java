package org.martus.amplifier.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.martus.amplifier.attachment.DataManager;
import org.martus.amplifier.attachment.FileSystemDataManager;
import org.martus.amplifier.common.AmplifierConstants;
import org.martus.amplifier.datasynch.BackupServerInfo;
import org.martus.amplifier.datasynch.DataSynchManager;
import org.martus.amplifier.lucene.LuceneBulletinIndexer;
import org.martus.amplifier.network.AmplifierClientSideNetworkHandlerUsingXMLRPC.SSLSocketSetupException;
import org.martus.amplifier.search.BulletinIndexException;
import org.martus.amplifier.search.BulletinIndexer;
import org.martus.common.LoggerInterface;
import org.martus.common.LoggerToConsole;
import org.martus.common.MartusUtilities;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.crypto.MartusSecurity;
import org.martus.common.crypto.MartusCrypto.AuthorizationFailedException;
import org.martus.common.crypto.MartusCrypto.CryptoInitializationException;
import org.martus.common.crypto.MartusCrypto.InvalidKeyPairFileVersionException;
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
			System.exit(2);
		}
		String passphrase = insecurePassword;
		if(passphrase == null)
			passphrase = getPassphraseFromConsole(amp);
		amp.loadAccount(passphrase);
		amp.displayStatistics();
		amp.start(passphrase);
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
		String packetsDirectory = new File(getBasePath(), "packets").getPath();

		dataManager = new FileSystemDataManager(packetsDirectory);
		
		File configDirectory = getStartupConfigDirectory();
		File backupServersDirectory = new File(configDirectory, "serversWhoWeCall");
		backupServersList = loadServersWeWillCall(backupServersDirectory, security);
		
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
		
		startServer(password);
		timer.scheduleAtFixedRate(timedTask, IMMEDIATELY, dataSynchIntervalMillis);
		
		while(! isShutdownRequested() )
		{
		}
	}
	

	private void startServer(String password) throws IOException, MultiException
	{
		File jettyConfigDirectory = getStartupConfigDirectory();
		File jettyXmlFile = new File(jettyConfigDirectory, "jettyConfiguration.xml");
		File jettyKeystore = new File(jettyConfigDirectory, "keystore");
		Server server = new Server(jettyXmlFile.getAbsolutePath());
		
		SunJsseListener sslListener = new SunJsseListener(new InetAddrPort(443));
		sslListener.setPassword(password);
		sslListener.setKeyPassword(password);
		sslListener.setKeystore(jettyKeystore.getAbsolutePath());
		sslListener.setMaxIdleTimeMs(30000);
		sslListener.setMaxThreads(255);
		sslListener.setMinThreads(5);
		sslListener.setLowResourcePersistTimeMs(5000);
		server.addListener(sslListener);
		
		server.addWebApplication("/","presentation/");
		addPasswordAuthentication(server);
		server.start();

		writeSyncFile(getRunningFile());
		System.out.println("Waiting for connection...");
	}

	private void processCommandLine(String[] args)
	{
		for(int arg = 0; arg < args.length; ++arg)
		{
			if(args[arg].equals("secure"))
				enterSecureMode();
			if(args[arg].equals("nopassword"))
				insecurePassword = "password";
		}
		
		if(isSecureMode())
			System.out.println("Running in SECURE mode");
		else
			System.out.println("***RUNNING IN INSECURE MODE***");
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
		File runningFile = new File(getTriggerDirectory(), "running");
		return runningFile;
	}

	public boolean isSecureMode()
	{
		return secureMode;
	}
	
	public void enterSecureMode()
	{
		secureMode = true;
	}
	
	File getTriggerDirectory()
	{
		return new File(getBasePath(), ADMINTRIGGERDIRECTORY);
		
	}

	public static String getBasePath()
	{
		return getDataDirectory().getPath();
	}
	
	public File getStartupConfigDirectory()
	{
		return new File(getBasePath(), ADMINSTARTUPCONFIGDIRECTORY);
	}

	boolean hasAccount()
	{
		return getKeyPairFile().exists();
	}
	
	File getKeyPairFile()
	{
		return new File(getStartupConfigDirectory(), KEYPAIRFILENAME);
	}

	void deleteLuceneLockFile()
	{
		File indexDirectory = getLuceneIndexDirectory();
		File lockFile = new File(indexDirectory, "write.lock");
		if(lockFile.exists())
		{
			log("Deleting lucene lock file: " + lockFile.getPath());
			lockFile.delete();
		}
	}

	private File getLuceneIndexDirectory()
	{
		return new File(getBasePath(), "index");
	}

	void addPasswordAuthentication(Server server)
	{
		PasswordAuthenticationHandler handler = new PasswordAuthenticationHandler();
		HttpContext context = server.getContext("/");
		context.addHandler(handler);
	}
	
	private static String getPassphraseFromConsole(MartusAmplifier amp)
	{
		System.out.print("Enter passphrase: ");
		System.out.flush();
		
		File waitingFile = new File(amp.getTriggerDirectory(), "waiting");
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
	
	boolean isShutdownRequested()
	{
		File ampDir = getWorkingDirectory();
		File shutdownFile = new File(ampDir, "shutdown");
		boolean doShutdown = false;
		if(shutdownFile.exists() && ! isAmplifierSyncing() )
		{
			shutdownFile.delete();
			doShutdown = true;
		}
		return doShutdown;
	}
	
	MartusSecurity getSecurity()
	{
		return security;
	}

	private File getWorkingDirectory()
	{
		return new File(getBasePath());
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
		
			dataSyncManager.getAllNewData(dataManager, indexer);
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
				//System.out.println("Scheduled Task started " + System.currentTimeMillis());

				pullNewDataFromServers(backupServersList);
				
				//System.out.println("Scheduled Task finished " + System.currentTimeMillis() + "\n");
				endSynch();
			}
		}
	}

	private static void displayVersion()
	{
		System.out.println("MartusAmplifier");
		System.out.println("Version " + AmplifierConstants.marketingVersionNumber);
		String versionInfo = MartusUtilities.getVersionDate();
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

	public static String getDefaultDataDirectoryPath()
	{
		String dataDirectory = null;
		if(System.getProperty("os.name").indexOf("Windows") >= 0)
		{
			dataDirectory = "C:/MartusAmplifier/";
		}
		else
		{
			dataDirectory = "/var/MartusAmplifier/";
		}
		return dataDirectory;
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
	static String insecurePassword;
	public static File dataDirectory;	

	public static MartusSecurity security;
	static final long IMMEDIATELY = 0;
	static final long dataSynchIntervalMillis = 100000;

	Timer timer = new Timer(true);
	TimerTask timedTask = new UpdateFromServerTask();
	boolean isSyncing;
	
	List backupServersList;
	
	LoggerInterface logger;

	public static LanguagesIndexedList languagesIndexed;
	public static DataManager dataManager;
	private static final String KEYPAIRFILENAME = "keypair.dat";
	private static final String ADMINTRIGGERDIRECTORY = "adminTriggers";
	private static final String ADMINSTARTUPCONFIGDIRECTORY = "deleteOnStartup";
}
