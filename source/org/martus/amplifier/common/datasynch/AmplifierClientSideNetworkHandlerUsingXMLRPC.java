package org.martus.amplifier.common.datasynch;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.xmlrpc.XmlRpcClient;

import org.martus.common.NetworkInterface;
import org.martus.common.NetworkInterfaceConstants;

import org.martus.amplifier.common.datasynch.AmplifierNetworkInterfaceXmlRpcConstants;
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
			HttpsURLConnection.setDefaultHostnameVerifier(new AmplifierSimpleHostnameVerifier());
		} 
		catch (Exception e) 
		{
			throw new SSLSocketSetupException();
		}
	}

				
		
	public Vector getAccountIds(String myAccountId, Vector parameters, String signature)
	{
		System.out.println("in AmplifierClientSideNetworkHandlerUsingXMLRPC.getAccountIds()");
		Vector params = new Vector();
		params.add(myAccountId);
		params.add(parameters);
		params.add(signature);
		return (Vector)callServer(server, cmdGetAccountIds, params);
	}			
	
	
	public Vector getAccountUniversalIds(String myAccountId, Vector parameters, String signature)
	{
		Vector params = new Vector();
		params.add(myAccountId);
		params.add(parameters);
		params.add(signature);
		return (Vector)callServer(server, cmdGetAccountUniversalIds, params);
		
	}
	
	public Vector getBulletinChunk(String myAccountId, Vector parameters, String signature)
	{
		Vector params = new Vector();
		params.add(myAccountId);
		params.add(parameters);
		params.add(signature);
		return (Vector)callServer(server, cmdGetAmplifierBulletinChunk, params);
	}
	
	public Object callServer(String serverName, String method, Vector params)
	{
		
		final String serverUrl = "https://" + serverName + ":" + port + "/RPC2";
		System.out.println("ServerInterfaceXmlRpcHandler:callServer serverUrl=" + serverUrl);
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
			System.out.println("ServerInterfaceXmlRpcHandler:callServer Exception=" + e);
			e.printStackTrace();
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
		tm = new AmplifierSimpleX509TrustManager();
		TrustManager []tma = {tm};
		SSLContext sslContext = SSLContext.getInstance( "TLS" );
		SecureRandom secureRandom = new SecureRandom();
		sslContext.init( null, tma, secureRandom);

		return sslContext.getSocketFactory();

	}
	
	public AmplifierSimpleX509TrustManager getSimpleX509TrustManager() 
	{
		return tm;
	}

	private void logging(String message)
	{
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		System.out.println(stamp + " " + message);
	}

	
	AmplifierSimpleX509TrustManager tm;
	String server;
	int port;
}

