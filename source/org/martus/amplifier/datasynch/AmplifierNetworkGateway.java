package org.martus.amplifier.datasynch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import org.martus.amplifier.attachment.AttachmentManager;
import org.martus.amplifier.attachment.AttachmentStorageException;
import org.martus.amplifier.network.AmplifierBulletinRetrieverGatewayInterface;
import org.martus.amplifier.network.AmplifierClientSideNetworkGateway;
import org.martus.amplifier.network.AmplifierClientSideNetworkHandlerUsingXMLRPC;
import org.martus.amplifier.network.AmplifierNetworkInterface;
import org.martus.amplifier.network.AmplifierClientSideNetworkHandlerUsingXMLRPC.SSLSocketSetupException;
import org.martus.amplifier.search.BulletinIndexException;
import org.martus.amplifier.search.BulletinIndexer;
import org.martus.common.LoggerInterface;
import org.martus.common.MartusUtilities.ServerErrorException;
import org.martus.common.bulletin.BulletinZipUtilities;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.crypto.MartusSecurity;
import org.martus.common.crypto.MartusCrypto.DecryptionException;
import org.martus.common.crypto.MartusCrypto.NoKeyPairException;
import org.martus.common.network.NetworkInterfaceConstants;
import org.martus.common.network.NetworkResponse;
import org.martus.common.packet.UniversalId;
import org.martus.common.packet.Packet.InvalidPacketException;
import org.martus.common.packet.Packet.SignatureVerificationException;
import org.martus.common.packet.Packet.WrongPacketTypeException;
import org.martus.util.Base64.InvalidBase64Exception;

public class AmplifierNetworkGateway
{
	public AmplifierNetworkGateway(BackupServerInfo backupServerToCall, LoggerInterface loggerToUse, MartusCrypto securityToUse)
	{
		this(null, backupServerToCall, loggerToUse, securityToUse);
	}
	
	public AmplifierNetworkGateway(AmplifierBulletinRetrieverGatewayInterface gatewayToUse, 
				BackupServerInfo backupServerToCall,
				LoggerInterface loggerToUse, 
				MartusCrypto securityToUse)
	{
		super();
	
		serverToPullFrom = backupServerToCall;
		logger = loggerToUse;
		gateway = gatewayToUse;
		if(gateway == null)
			gateway = getCurrentNetworkInterfaceGateway();
			
		security = securityToUse;
	}
	
	public Vector getAllAccountIds()
	{
		class NotAuthorizedException extends Exception {}
		
		Vector result = new Vector();
		try
		{
			log("getAllAccountIds");
			NetworkResponse response = gateway.getAccountIds(security);
			String resultCode = response.getResultCode();
			if(!resultCode.equals(NetworkInterfaceConstants.OK))
				throw new NotAuthorizedException();
			result= response.getResultVector();
		}
		catch(IOException e)
		{
			//e.printStackTrace();
			//logger.info("No server available");
			log("No server available");
		}
		catch(NotAuthorizedException e)
		{
			log("AmplifierNetworkGateway.getAllAccountIds() NOT AUTHORIZED");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log("AmplifierNetworkGateway.getAllAccountIds(): ERROR: " + e.getMessage());
		}
		return result;
	}

	public Vector getContactInfo(String accountId)
	{
		try
		{
			log("getContactInfo:" + MartusCrypto.formatPublicCode(MartusCrypto.computePublicCode(accountId)));
			NetworkResponse response = gateway.getContactInfo(accountId, security);
			String resultCode = response.getResultCode();
			if(!resultCode.equals(NetworkInterfaceConstants.OK))
				return null;
			Vector contactInfoResult = response.getResultVector();
			if(security.verifySignatureOfVectorOfStrings(contactInfoResult, accountId))
				return contactInfoResult;
		}
		catch (Exception e)
		{
			log(e.toString());
		}
		return null;
	}	
	
	public Vector getAccountPublicBulletinLocalIds(String accountId)
	{
		Vector result = new Vector();
		try
		{
			log("getAccountPublicBulletinLocalIds: " + MartusSecurity.getFormattedPublicCode(accountId));
			NetworkResponse response = gateway.getPublicBulletinLocalIds(security, accountId);
			String resultCode = response.getResultCode();
			if( !resultCode.equals(NetworkInterfaceConstants.OK) )	
					throw new ServerErrorException(resultCode);
			result = response.getResultVector();		
		}	
		catch(Exception e)
		{
			log("AmplifierNetworkGateway.getAccountBulletinLocalIds(): ERROR " + e.getMessage() + ": " + accountId);
		}
		return result;
	}
	

	public void retrieveAndManageBulletin(
		UniversalId uid, BulletinExtractor bulletinExtractor) 
		throws WrongPacketTypeException, IOException, DecryptionException, 
			InvalidPacketException, BulletinIndexException, 
			NoKeyPairException, SignatureVerificationException, 
			AttachmentStorageException, InvalidBase64Exception
	{
		File bulletinFile = getBulletin(uid);
		bulletinFile.deleteOnExit();
		try
		{
			bulletinExtractor.extractAndStoreBulletin(bulletinFile);
			bulletinFile.delete();	
		}
		catch(DecryptionException e)
		{
			throw(e);
		}
	}
	
	
	public File getBulletin(UniversalId uid)
	{
		File tempFile = null;
		FileOutputStream out = null;
		int chunkSize = NetworkInterfaceConstants.MAX_CHUNK_SIZE;
		int totalLength =0;
		try 
		{
			log("getBulletin: " + MartusSecurity.getFormattedPublicCode(uid.getAccountId()) + 
								":" + uid.getLocalId());
			tempFile = File.createTempFile("$$$TempFile", null);
			tempFile.deleteOnExit();
        	out = new FileOutputStream(tempFile);		
		    try
		 	{	
				totalLength = BulletinZipUtilities.retrieveBulletinZipToStream
									(uid, out, chunkSize, gateway, security, null);
			}
			catch(Exception e)
			{
				log("Unable to retrieve bulletin: " + e.getMessage());
			}
			finally
			{
				out.close();
			}
		}
		catch(Exception e)
		{
			log("ERROR: " + e.getMessage());	
		}

		if(tempFile.length() != totalLength)
		{
			System.out.println("file=" + tempFile.length() + ", returned=" + totalLength);
			log("Error" + new ServerErrorException("totalSize didn't match data length") );
		}
		return tempFile;
	}
	
	public BulletinExtractor createBulletinExtractor(
		AttachmentManager attachmentManager, BulletinIndexer indexer)
	{
		return new BulletinExtractor(attachmentManager, indexer, security);
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
		BackupServerInfo serverInfo = serverToPullFrom;
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
			e.printStackTrace();
			return null;
		}
	}
	
	private void log(String message)
	{
		logger.log("Calling " + serverToPullFrom.getAddress() + ": " + message);
	}
	
	private AmplifierBulletinRetrieverGatewayInterface gateway;
	private MartusCrypto security;
	private LoggerInterface logger;
	BackupServerInfo serverToPullFrom;
	private AmplifierNetworkInterface currentNetworkInterfaceHandler = null;
	private AmplifierClientSideNetworkGateway currentNetworkInterfaceGateway = null;
}
