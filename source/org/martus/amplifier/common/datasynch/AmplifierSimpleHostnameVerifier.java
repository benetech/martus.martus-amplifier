package org.martus.amplifier.common.datasynch;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class AmplifierSimpleHostnameVerifier implements HostnameVerifier 
{

	public AmplifierSimpleHostnameVerifier() 
	{
		super();
	}

	public boolean verify(String hostName, SSLSession session) 
	{
		//This is called if the certificate CN doesn't match the URL
		//Our security relies on public keys, not IP addresses.
		return true;
	}
}