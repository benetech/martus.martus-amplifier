package org.martus.amplifier.network;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.xmlrpc.XmlRpcClient;
import org.martus.common.network.NetworkInterfaceConstants;
import org.martus.common.network.SimpleHostnameVerifier;
import org.martus.common.network.SimpleX509TrustManager;
/**
 * @author skoneru
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

public class AmplifierClientSideNetworkHandlerUsingXMLRPC 
	implements NetworkInterfaceConstants, AmplifierNetworkInterfaceXmlRpcConstants, AmplifierNetworkInterface
{

	public class SSLSocketSetupException extends Exception {}

	public AmplifierClientSideNetworkHandlerUsingXMLRPC (String serverName, int portToUse) throws SSLSocketSetupException
	{
		server = serverName;
		port = portToUse;
		try 
		{
			HttpsURLConnection.setDefaultSSLSocketFactory(createSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new SimpleHostnameVerifier());
		} 
		catch (Exception e) 
		{
			throw new SSLSocketSetupException();
		}
	}

				
		
	public Vector getAccountIds(String myAccountId, Vector parameters, String signature) throws IOException
	{
		Vector params = new Vector();
		params.add(myAccountId);
		params.add(parameters);
		params.add(signature);
		return (Vector)callServer(server, cmdGetAccountIds, params);
	}			
	
	
	public Vector getPublicBulletinUniversalIds(String myAccountId, Vector parameters, String signature) throws IOException
	{
		Vector params = new Vector();
		params.add(myAccountId);
		params.add(parameters);
		params.add(signature);
		return (Vector)callServer(server, cmdGetAccountUniversalIds, params);
		
	}
	
	public Vector getBulletinChunk(String myAccountId, Vector parameters, String signature) throws IOException
	{
		Vector params = new Vector();
		params.add(myAccountId);
		params.add(parameters);
		params.add(signature);
		return (Vector)callServer(server, cmdGetAmplifierBulletinChunk, params);
	}
	
	public Object callServer(String serverName, String method, Vector params) throws IOException
	{
		
		final String serverUrl = "https://" + serverName + ":" + port + "/RPC2";
		Object result = null;
		try
		{
			XmlRpcClient client = new XmlRpcClient(serverUrl);
			result = client.execute("MartusAmplifierServer." + method, params);
		}
		catch (IOException e)
		{
			//TODO throw IOExceptions so caller can decide what to do.
			//This was added for connection refused: connect (no server connected)
			//System.out.println("ServerInterfaceXmlRpcHandler:callServer Exception=" + e);
			throw e;
		}
		catch (Exception e)
		{
			System.out.println("ServerInterfaceXmlRpcHandler:callServer Exception=" + e);
			e.printStackTrace();
		}
		return result;
	}
	
	SSLSocketFactory createSocketFactory() throws Exception
	{
		tm = new SimpleX509TrustManager();
		TrustManager []tma = {tm};
		SSLContext sslContext = SSLContext.getInstance( "TLS" );
		SecureRandom secureRandom = new SecureRandom();
		sslContext.init( null, tma, secureRandom);

		return sslContext.getSocketFactory();

	}
	
	public SimpleX509TrustManager getSimpleX509TrustManager() 
	{
		return tm;
	}

	SimpleX509TrustManager tm;
	String server;
	int port;
}

