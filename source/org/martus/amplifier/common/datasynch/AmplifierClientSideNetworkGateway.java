package org.martus.amplifier.common.datasynch;

import java.util.Vector;


import org.martus.common.MartusCrypto;
import org.martus.common.MartusUtilities;
import org.martus.common.NetworkResponse;

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
			MartusCrypto.MartusSignatureException
	{
		System.out.println("in AmplifierClientSideNetworkGateway.getAccountIds()");
		Vector parameters = new Vector();
		String signature = MartusUtilities.sign(parameters, signer);
		return new NetworkResponse(server.getAccountIds(signer.getPublicKeyString(), parameters, signature));
	}

	public NetworkResponse getAccountUniversalIds(MartusCrypto signer, String accountId) throws 
			MartusCrypto.MartusSignatureException
	{
		Vector parameters = new Vector();
		parameters.add(accountId);
		String signature = MartusUtilities.sign(parameters, signer);
		return new NetworkResponse(server.getAccountUniversalIds(signer.getPublicKeyString(), parameters, signature));
			
	}
					
	public NetworkResponse getBulletinChunk(MartusCrypto signer, String authorAccountId, String bulletinLocalId, 
					int chunkOffset, int maxChunkSize) throws 
			MartusCrypto.MartusSignatureException
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
