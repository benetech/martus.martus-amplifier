package org.martus.amplifier.common.datasynch;


import java.io.IOException;

import org.martus.common.crypto.MartusCrypto;
import org.martus.common.network.BulletinRetrieverGatewayInterface;
import org.martus.common.network.NetworkResponse;

/**
 * @author skoneru
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface AmplifierBulletinRetrieverGatewayInterface extends BulletinRetrieverGatewayInterface {


	public NetworkResponse getAccountIds(MartusCrypto signer) throws 
			MartusCrypto.MartusSignatureException, IOException;
			
	public NetworkResponse getPublicBulletinUniversalIds(MartusCrypto signer, String accountId) throws 
			MartusCrypto.MartusSignatureException, IOException;
			
}