package org.martus.amplifier.network;

import java.io.IOException;
import java.util.Vector;

/**
 * @author skoneru
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface AmplifierNetworkInterface { 
	
	public Vector getAccountIds(String myAccountId, Vector parameters, String signature) throws IOException;
	public Vector getPublicBulletinUniversalIds(String myAccountId, Vector parameters, String signature) throws IOException;
	public Vector getBulletinChunk(String myAccountId, Vector parameters, String signature) throws IOException;

}
