package org.martus.amplifier.service.datasynch;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.martus.amplifier.common.datasynch.AmplifierClientSideNetworkHandlerUsingXMLRPC.SSLSocketSetupException;
import org.martus.common.MartusUtilities;
import org.martus.common.MartusUtilities.InvalidPublicKeyFileException;
import org.martus.common.MartusUtilities.PublicInformationInvalidException;
import org.martus.common.crypto.MartusCrypto;

public class BackupServerManager implements IDataSynchConstants
{
	public static List loadServersWeWillCall(File directory, MartusCrypto security) throws 
			IOException, InvalidPublicKeyFileException, PublicInformationInvalidException, SSLSocketSetupException
	{
		List serversWeWillCall = new Vector();

		File[] toCallFiles = directory.listFiles();
		if(toCallFiles != null)
		{
			for (int i = 0; i < toCallFiles.length; i++)
			{
				File toCallFile = toCallFiles[i];
				serversWeWillCall.add(getServerToCall(toCallFile, security));
				System.out.println("We will call: " + toCallFile.getName());
			}
		}

		System.out.println("Configured to call " + serversWeWillCall.size() + " servers");
		return serversWeWillCall;
	}
	
	static BackupServerInfo getServerToCall(File publicKeyFile, MartusCrypto security) throws
			IOException, 
			InvalidPublicKeyFileException, 
			PublicInformationInvalidException, 
			SSLSocketSetupException
	{
		String ip = MartusUtilities.extractIpFromFileName(publicKeyFile.getName());
		int port = 985;
		Vector publicInfo = MartusUtilities.importServerPublicKeyFromFile(publicKeyFile, security);
		String publicKey = (String)publicInfo.get(0);

		return new BackupServerInfo(ip, ip, port, publicKey);		
	}

}
