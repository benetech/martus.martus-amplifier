package org.martus.amplifier.common.datasynch;


import org.martus.common.BulletinRetrieverGatewayInterface;
import org.martus.common.MartusCrypto;
import org.martus.common.NetworkResponse;

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
			MartusCrypto.MartusSignatureException;
			
}