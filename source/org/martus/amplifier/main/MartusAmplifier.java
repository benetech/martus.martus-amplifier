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
import java.util.logging.Logger;

import org.martus.amplifier.attachment.AttachmentManager;
import org.martus.amplifier.attachment.FileSystemAttachmentManager;
import org.martus.amplifier.common.AmplifierConfiguration;
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
import org.mortbay.http.HttpContext;
import org.mortbay.jetty.Server;

public class MartusAmplifier
{
	public static void main(String[] args) throws Exception
	{
		displayVersion();
		MartusAmplifier amp = new MartusAmplifier(new LoggerToConsole());
		
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
		amp.start();
	}
	
	public MartusAmplifier(LoggerInterface loggerToUse) throws CryptoInitializationException
	{
		logger = loggerToUse;
		security = new MartusSecurity();
	}

	void start() throws Exception
	{
		deleteLuceneLockFile();
		String basePath = AmplifierConfiguration.getInstance().getBasePath();

		attachmentManager = new FileSystemAttachmentManager(basePath);
		
		File configDirectory = new File(basePath);
		File backupServersDirectory = new File(configDirectory, "serversWhoWeCall");
		backupServersList = loadServersWeWillCall(backupServersDirectory, security);
		
		//Code.setDebug(true);
		Server server = new Server("jettyConfiguration.xml");
		server.addWebApplication("/","presentation/");
		
		addPasswordAuthentication(server);
		
		server.start();
		timer.scheduleAtFixedRate(timedTask, IMMEDIATELY, dataSynchIntervalMillis);
		
		while(! isShutdownRequested() )
		{
		}
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

	String getBasePath()
	{
		return AmplifierConfiguration.getInstance().getBasePath();
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
		File indexDirectory = new File(AmplifierConfiguration.getInstance().getBasePath(), "index");
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
		return new File(AmplifierConfiguration.getInstance().getWorkingPath());
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

	public void pullNewBulletinsFromServers(List backupServersList) 
	{
		Logger fancyLogger = Logger.getLogger("MainTask");
		
		if(backupServersList.size() == 0)
			return;
				
		BulletinIndexer indexer = null;
		try
		{
			DataSynchManager dataManager = new DataSynchManager(backupServersList, getSecurity());
			AmplifierConfiguration config = 
				AmplifierConfiguration.getInstance();
			indexer = new LuceneBulletinIndexer(
				config.getBasePath());
	
			dataManager.getAllNewBulletins(attachmentManager, indexer);
		}
		catch(Exception e)
		{
			fancyLogger.severe("MartusAmplifierDataSynch.execute(): " + e.getMessage());
			e.printStackTrace();
		} 
		finally
		{
			if (indexer != null) {
				try {
					indexer.close();
				} catch (BulletinIndexException e) {
					fancyLogger.severe(
						"Unable to close the indexer: " + e.getMessage());
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

		if(serversWeWillCall.size() > 1)
		{
			log("ERROR: Can only call one server. Aborting.");
			System.exit(55);
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

				pullNewBulletinsFromServers(backupServersList);
				
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

	boolean secureMode;
	static String insecurePassword;

	MartusSecurity security;
	static final long IMMEDIATELY = 0;
	static final long dataSynchIntervalMillis = 100000;

	Timer timer = new Timer(true);
	TimerTask timedTask = new UpdateFromServerTask();
	boolean isSyncing;
	
	List backupServersList;
	
	LoggerInterface logger;

	public static AttachmentManager attachmentManager;
	private static final String KEYPAIRFILENAME = "keypair.dat";
	private static final String ADMINTRIGGERDIRECTORY = "adminTriggers";
	private static final String ADMINSTARTUPCONFIGDIRECTORY = "deleteOnStartup";
}
