package org.martus.amplifier.common.datasynch;

import java.util.Vector;

import org.martus.common.BulletinRetrieverGatewayInterface;
import org.martus.common.MartusCrypto;
import org.martus.common.MartusUtilities;
import org.martus.common.NetworkInterface;
import org.martus.common.NetworkResponse;

/**
 * @author skoneru
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class AmplifierClientSideNetworkGateway implements BulletinRetrieverGatewayInterface
{
	public AmplifierClientSideNetworkGateway(NetworkInterface serverToUse)
	{
		server = serverToUse;
	}
	
	public NetworkResponse getServerInfo()
	{
		Vector parameters = new Vector();
		return new NetworkResponse(server.getServerInfo(parameters));
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
	NetworkInterface server;
}
