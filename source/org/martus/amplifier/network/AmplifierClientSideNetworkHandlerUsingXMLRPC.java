/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2002,2003, Beneficent
Technology, Inc. (Benetech).

Martus is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later
version with the additions and exceptions described in the
accompanying Martus license file entitled "license.txt".

It is distributed WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, including warranties of fitness of purpose or
merchantability.  See the accompanying Martus License and
GPL license for more details on the required license terms
for this software.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.

*/
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
	
	public Vector getContactInfo(String myAccountId, Vector parameters, String signature) throws IOException
	{
		Vector params = new Vector();
		params.add(myAccountId);
		params.add(parameters);
		params.add(signature);
		return (Vector)callServer(server, cmdGetContactInfo, params);
	}			

	
	public Vector getPublicBulletinLocalIds(String myAccountId, Vector parameters, String signature) throws IOException
	{
		Vector params = new Vector();
		params.add(myAccountId);
		params.add(parameters);
		params.add(signature);
		return (Vector)callServer(server, cmdGetPublicBulletinLocalIds, params);
		
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

