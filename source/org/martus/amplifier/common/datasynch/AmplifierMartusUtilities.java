package org.martus.amplifier.common.datasynch;


import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Vector;

import org.martus.common.*;
import org.martus.common.MartusCrypto.CryptoException;
import org.martus.common.MartusCrypto.DecryptionException;
import org.martus.common.MartusCrypto.MartusSignatureException;
import org.martus.common.MartusCrypto.NoKeyPairException;
import org.martus.common.Packet.InvalidPacketException;
import org.martus.common.Packet.SignatureVerificationException;
import org.martus.common.Packet.WrongAccountException;
import org.martus.common.Packet.WrongPacketTypeException;

public class AmplifierMartusUtilities 
{
	public static class FileTooLargeException extends Exception {}
	public static class FileVerificationException extends Exception {}
	public static class FileSigningException extends Exception {}

	public static class ServerErrorException extends Exception 
	{
		public ServerErrorException(String message)
		{
			super(message);
		}
		
		public ServerErrorException()
		{
			this("");
		}
	}


	public static int retrieveBulletinZipToStream(UniversalId uid, OutputStream outputStream, 
			int chunkSize, BulletinRetrieverGatewayInterface gateway, MartusCrypto security, 
			ProgressMeterInterface progressMeter, String progressTag)
		throws
			MartusCrypto.MartusSignatureException,
			ServerErrorException,
			IOException,
			Base64.InvalidBase64Exception
	{
		int masterTotalSize = 0;
		int totalSize = 0;
		int chunkOffset = 0;
		String lastResponse = "";
		Vector result = null;

		if(progressMeter != null)
			progressMeter.updateProgressMeter(progressTag, 0, 1);	
		while(!lastResponse.equals(NetworkInterfaceConstants.OK))
		{
			NetworkResponse response = gateway.getBulletinChunk(security, 
								uid.getAccountId(), uid.getLocalId(), chunkOffset, chunkSize);
								
			lastResponse = response.getResultCode();
			if(!lastResponse.equals(NetworkInterfaceConstants.OK) &&
				!lastResponse.equals(NetworkInterfaceConstants.CHUNK_OK))
			{
				//System.out.println((String)result.get(0));
				throw new ServerErrorException("result=" + lastResponse);
			}
			
			result = response.getResultVector();
			totalSize = ((Integer)result.get(0)).intValue();
			if(masterTotalSize == 0)
				masterTotalSize = totalSize;
				
			if(totalSize != masterTotalSize)
				throw new ServerErrorException("totalSize not consistent");
			if(totalSize < 0)
				throw new ServerErrorException("totalSize negative");
				
			int thisChunkSize = ((Integer)result.get(1)).intValue();
			if(thisChunkSize < 0 || thisChunkSize > totalSize - chunkOffset)
				throw new ServerErrorException("chunkSize out of range");
			
			// TODO: validate that length of data == chunkSize that was returned
			String data = (String)result.get(2);
			StringReader reader = new StringReader(data);
		
			Base64.decode(reader, outputStream);
			chunkOffset += thisChunkSize;
			if(progressMeter != null)
			{
				if(progressMeter.shouldExit())
					break;					
				progressMeter.updateProgressMeter(progressTag, chunkOffset, masterTotalSize);	
			}
		}
		if(progressMeter != null)
			progressMeter.updateProgressMeter(progressTag, chunkOffset, masterTotalSize);	
		return masterTotalSize;
	}

}
