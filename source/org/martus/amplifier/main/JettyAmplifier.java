package org.martus.amplifier.main;

import org.mortbay.http.SocketListener;
import org.mortbay.jetty.Server;


public class JettyAmplifier
{
	public static void main(String[] args)
	{
		SocketListener listener = new SocketListener();
		listener.setPort(8080); 

		Server server = new Server();
		server.addListener(listener);

		try
		{
			server.addWebApplication("/","presentation/");
			
			server.start();
			while(true)
				;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(1);	
	}
}
