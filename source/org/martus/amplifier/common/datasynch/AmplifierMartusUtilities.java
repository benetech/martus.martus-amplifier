package org.martus.amplifier.common.datasynch;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Vector;

import org.martus.common.MartusUtilities;
import org.martus.common.ProgressMeterInterface;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.network.NetworkInterfaceConstants;
import org.martus.common.network.NetworkResponse;
import org.martus.common.packet.UniversalId;
import org.martus.util.Base64;

/**
 * @author skoneru
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class AmplifierMartusUtilities extends MartusUtilities {
	
	
	public static int retrieveBulletinZipToStream(UniversalId uid, OutputStream outputStream, 
			int chunkSize, AmplifierBulletinRetrieverGatewayInterface gateway, MartusCrypto security, 
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
		if(progressMeter != null)
		{
			progressMeter.setStatusMessageTag(progressTag);
			progressMeter.updateProgressMeter(0, 1);	
		}
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
			
			Vector result = response.getResultVector();
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
				progressMeter.setStatusMessageTag(progressTag);
				progressMeter.updateProgressMeter(chunkOffset, masterTotalSize);	
			}
		}
		if(progressMeter != null)
		{
			progressMeter.setStatusMessageTag(progressTag);
			progressMeter.updateProgressMeter(chunkOffset, masterTotalSize);	
		}
		return masterTotalSize;
	}





}
