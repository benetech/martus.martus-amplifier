package org.martus.amplifier.common.datasynch;

import java.io.IOException;
import java.util.Vector;

import org.martus.common.crypto.MartusCrypto;
import org.martus.common.MartusUtilities;
import org.martus.common.network.NetworkResponse;

/**
 * @author skoneru
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class AmplifierClientSideNetworkGateway implements AmplifierBulletinRetrieverGatewayInterface
{
	public AmplifierClientSideNetworkGateway(AmplifierNetworkInterface serverToUse)
	{
		server = serverToUse;
	}
	
	//to check if we need signature even for no parameters
	public NetworkResponse getAccountIds(MartusCrypto signer) throws 
			MartusCrypto.MartusSignatureException, IOException
	{
		Vector parameters = new Vector();
		String signature = MartusCrypto.sign(parameters, signer);
		return new NetworkResponse(server.getAccountIds(signer.getPublicKeyString(), parameters, signature));
	}

	public NetworkResponse getPublicBulletinUniversalIds(MartusCrypto signer, String accountId) throws 
			MartusCrypto.MartusSignatureException, IOException
	{
		Vector parameters = new Vector();
		parameters.add(accountId);
		String signature = MartusUtilities.sign(parameters, signer);
		return new NetworkResponse(server.getPublicBulletinUniversalIds(signer.getPublicKeyString(), parameters, signature));
			
	}
					
	public NetworkResponse getBulletinChunk(MartusCrypto signer, String authorAccountId, String bulletinLocalId, 
					int chunkOffset, int maxChunkSize) throws 
			MartusCrypto.MartusSignatureException, IOException
	{
		Vector parameters = new Vector();
		parameters.add(authorAccountId);
		parameters.add(bulletinLocalId);
		parameters.add(new Integer(chunkOffset));
		parameters.add(new Integer(maxChunkSize));
		String signature = MartusUtilities.sign(parameters, signer);
		return new NetworkResponse(server.getBulletinChunk(signer.getPublicKeyString(), parameters, signature));
	}

	
	
	
	AmplifierNetworkInterface server;
}
