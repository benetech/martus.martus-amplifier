package org.martus.amplifier.service.datasynch;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.logging.Logger;


import org.martus.amplifier.service.datasynch.BackupServerInfo;
import org.martus.amplifier.service.datasynch.BackupServerManager;

import org.martus.amplifier.common.datasynch.AmplifierBulletinRetrieverGatewayInterface;
import org.martus.amplifier.common.datasynch.AmplifierClientSideNetworkGateway;
//import org.martus.amplifier.common.datasynch.AmplifierMartusUtilities;
import org.martus.amplifier.common.datasynch.AmplifierNetworkInterface;
import org.martus.amplifier.common.datasynch.AmplifierClientSideNetworkHandlerUsingXMLRPC;
import org.martus.amplifier.common.datasynch.AmplifierClientSideNetworkHandlerUsingXMLRPC.SSLSocketSetupException;


import org.martus.amplifier.exception.MartusAmplifierApplicationException;


import org.martus.common.MartusCrypto;
import org.martus.common.MartusSecurity;
import org.martus.common.MartusUtilities;
import org.martus.common.NetworkResponse;
import org.martus.common.NetworkInterfaceConstants;
import org.martus.common.NetworkInterfaceXmlRpcConstants;
import org.martus.common.Base64.InvalidBase64Exception;
import org.martus.common.MartusCrypto.MartusSignatureException;
import org.martus.common.MartusUtilities.ServerErrorException;


//from Martus common code
import org.martus.common.UniversalId;

public class AmplifierNetworkGateway implements IDataSynchConstants
{
	
	

	protected AmplifierNetworkGateway()
	{
		super();
		
		Properties pathProperties = new Properties();
		try
		{
			pathProperties.load(getClass().getResourceAsStream(PATH_PROPERTIES));
		}
		catch(IOException ioe)
		{
			logger.severe("Unable to load path.properties configuration file.");
		}
		bulletinWorkingDirectory = pathProperties.getProperty(BULLETINWORKINGDIRECTORY);
		attachmentWorkingDirectory = pathProperties.getProperty(ATTACHMENTWORKINGDIRECTORY);
		
		serverInfoList = BackupServerManager.getInstance().getBackupServersList();
		gateway = getCurrentNetworkInterfaceGateway();
		try
		{
			security = new MartusSecurity();
		}
		catch(Exception e)
		{
			logger.severe("CryptoInitialization Exception " + e.getMessage());		
		}
		
	}
	
	public static AmplifierNetworkGateway getInstance()
	{
		if(instance == null)
		instance = new AmplifierNetworkGateway();
		return instance;
	}
	
	/*
	public List getAllAccountIds()
	{
		//fake data
		List fakeAccountIds = new ArrayList();
		fakeAccountIds.add("1");
		fakeAccountIds.add("2");
		return fakeAccountIds;
	}
	*/
	public Vector getAllAccountIds() throws ServerErrorException
	{
		Vector result = new Vector();
		try
		{
			NetworkResponse response = gateway.getAccountIds(security);
			String resultCode = response.getResultCode();
			if(!resultCode.equals(NetworkInterfaceConstants.OK))
				throw new ServerErrorException(resultCode);
			result= response.getResultVector();
		}
		catch(Exception e)
		{
			logger.severe("AmplifierNetworkGateway.getAllAccountIds(): Unable to retrieve AccountIds: " + e.getMessage());
		}
		return result;
	}
	
	
	public Vector getAccountUniversalIds(String accountId) throws ServerErrorException
	{
		Vector result = new Vector();
		try
		{
			NetworkResponse response = gateway.getAccountUniversalIds(security, accountId);
			String resultCode = response.getResultCode();
			if( !resultCode.equals(NetworkInterfaceConstants.OK) )	
					throw new ServerErrorException(resultCode);
			result = response.getResultVector();		
		}	
		catch(Exception e)
		{
			logger.severe("AmplifierNetworkGateway.getAccountUniversalIds(): unable to retrieve UniversalIds for AccountID = "+accountId);
		}
		return result;
	}
	
	
	
	/*
	
	public List getAccountBulletinIds(String accountId)
	{
		if(accountId == null)
			return null;
		// fake data
		List fakeBulletinIds = new ArrayList();
		if(accountId.equals("1"))
		{
			fakeBulletinIds.add("11");
			fakeBulletinIds.add("12");	
		}
		else
		{
			fakeBulletinIds.add("21");
			fakeBulletinIds.add("22");	
		}
		return fakeBulletinIds;
	}
	*/
	
	/*
	public List getAllBulletinIds()
	{
		List allBulletinIds = new ArrayList();
		List allAccountIds = getAllAccountIds();
		if(allAccountIds == null) 
			return allBulletinIds;
		Iterator accountIdIterator = allAccountIds.iterator();
		String currentAccountId = null;
		List currentBulletinList = null;
		while(accountIdIterator.hasNext())
		{
			currentAccountId = (String) accountIdIterator.next();
			currentBulletinList = getAccountBulletinIds(currentAccountId);
			allBulletinIds.addAll(currentBulletinList);
		}			
		return allBulletinIds;
	}
	
	*/
	public Vector getBulletin(UniversalId uid)
	{
		Vector result = new Vector();
		File tempFile = null;
		File bulletinZippedFile = null;
		String bulletinPrefix = "F-";
		String attachmentPrefix = "A-";
		String dir = "";
			  
		// 1) retrieve Bulletin in Chunks and get the Zip file
		//bulletinZippedFile = retrieveOneBulletin(uid);
		bulletinZippedFile = new File("testdata/Firebombing of NGO O13806.mbf");
		
		// 2) Unzip the file and retrieve the bulletin and attachments
		if(bulletinZippedFile != null)
		{
			result = AmplifierUtilities.unZip(bulletinZippedFile);
		}
		else
		{
			logger.severe("AmplifierNetworkGateway.getBulletin(uid):BulletinZippedFile is empty" );
		}
		
		//3.Put files in Bulletin Folder and attachments folder 
		if( result!=null)
		{
			for(int i= 0; i< result.size(); i++)
			{
				tempFile = (File)result.get(i);	
				tempFile.deleteOnExit();
				if( tempFile.getName().startsWith(bulletinPrefix) )
				{
					dir = bulletinWorkingDirectory;
					saveFileToFolder(tempFile, dir);
				}
				else
				{
					if( tempFile.getName().startsWith(attachmentPrefix) )
					{
						dir = attachmentWorkingDirectory;
						saveFileToFolder(tempFile, dir);
					}				
				}
			}
		}	
		else
		{
			logger.severe("AmplifierNetworkGateway.getBulletin(uid): Unzipping of bulletinZippedFile is not sucessful");	
		}	
			
		return result;		
	}
	
	
	
	public  void saveFileToFolder(File fileToSave, String dir)
	{
		
		String outFileName = fileToSave.getName();
		String path = dir + File.separator + outFileName;	
		logger.info("saving file "+ path);
		try
		{
			
			File outFile = new File(dir);
			if(! outFile.exists())
			{			
				logger.info("Creating directory "+ dir);
				outFile.mkdir();
			}
			FileInputStream inStream = new FileInputStream(fileToSave);
			FileOutputStream outStream = new FileOutputStream(path);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outStream);
			byte[] buffer = new byte[1024];
			int len = 0;
			while( (len=inStream.read(buffer)) >= 0)
			{
				bufferedOutputStream.write(buffer, 0, len);	
			}
			bufferedOutputStream.flush();
			outStream.flush();
			inStream.close();
			bufferedOutputStream.close();
			outStream.close();	
		}
		catch(IOException ioe)
		{
		  logger.severe("AmplifierNetworkGateway.saveFileToFolder:IOException "+ioe.getMessage());
		}
	}
	
	
	public File retrieveOneBulletin(UniversalId uid)
	{
		File tempFile = null;
		FileOutputStream out = null;
		int chunkSize = NetworkInterfaceConstants.MAX_CHUNK_SIZE;
		int totalLength =0;
		try 
		{
			tempFile = File.createTempFile("$$$TempFile", null);
			tempFile.deleteOnExit();
        	out = new FileOutputStream(tempFile);		
		    try
		 	{	
				totalLength = MartusUtilities.retrieveBulletinZipToStream
									(uid, out, chunkSize, gateway, security, null, null);
			}
			catch(Exception e)
			{
				logger.severe("Unable to retrieve bulletin: " + e.getMessage());
			}
		out.close();
		}
		catch(FileNotFoundException fe)
		{
			logger.severe("File not found : " + fe.getMessage());	
		}
		catch(IOException ie)
		{
			logger.severe("IO Exception could not create tempfile : " + ie.getMessage());	
		}

		if(tempFile.length() != totalLength)
		{
			System.out.println("file=" + tempFile.length() + ", returned=" + totalLength);
			logger.severe("Error" + new ServerErrorException("totalSize didn't match data length") );
		}
		return tempFile;
	}
	
//methods copied/modified from MartusAPP	
	private AmplifierClientSideNetworkGateway getCurrentNetworkInterfaceGateway()
	{
		if(currentNetworkInterfaceGateway == null)
		{
			currentNetworkInterfaceGateway = new AmplifierClientSideNetworkGateway(getCurrentNetworkInterfaceHandler());
		}
		
		return currentNetworkInterfaceGateway;
	}
	
	private AmplifierNetworkInterface getCurrentNetworkInterfaceHandler()
	{
		if(currentNetworkInterfaceHandler == null)
		{
			currentNetworkInterfaceHandler = createXmlRpcNetworkInterfaceHandler();
		}

		return currentNetworkInterfaceHandler;
	}

	private AmplifierNetworkInterface createXmlRpcNetworkInterfaceHandler() 
	{
		int index = 0;
		BackupServerInfo serverInfo = (BackupServerInfo) serverInfoList.get(index);
		String ourServer = serverInfo.getName();
//		int ourPort = NetworkInterfaceXmlRpcConstants.MARTUS_PORT_FOR_SSL;
		int ourPort = serverInfo.getPort();
		try 
		{
			AmplifierClientSideNetworkHandlerUsingXMLRPC handler = new AmplifierClientSideNetworkHandlerUsingXMLRPC(ourServer, ourPort);
		//	handler.getSimpleX509TrustManager().setExpectedPublicKey(getConfigInfo().getServerPublicKey());
		    handler.getSimpleX509TrustManager().setExpectedPublicKey(serverInfo.getServerPublicKey());
			return handler;
		} 
		catch (SSLSocketSetupException e) 
		{
			//TODO propagate to UI and needs a test.
			e.printStackTrace();
			return null;
		}
	}
	
	private static AmplifierNetworkGateway instance = null;
	private static AmplifierBulletinRetrieverGatewayInterface gateway;
	private static MartusCrypto security;
	private static Logger logger = Logger.getLogger(DATASYNC_LOGGER);
	private static List serverInfoList = null;
	private static String bulletinWorkingDirectory = "";
	private static String attachmentWorkingDirectory ="";
	
	private static final String BULLETINWORKINGDIRECTORY = "BULLETIN_WORKING_DIRECTORY";
	private static final String ATTACHMENTWORKINGDIRECTORY = "ATTACHMENT_WORKING_DIRECTORY";
	

	
	private AmplifierNetworkInterface currentNetworkInterfaceHandler = null;
	private AmplifierClientSideNetworkGateway currentNetworkInterfaceGateway = null;
	
	

}
