package org.martus.amplifier.main;

import org.mortbay.http.HttpContext;
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
//			server.addWebApplication("/","c:/programs/jetty/jetty-4.2.12/demo/webapps/jetty/");
			server.addWebApplication("/","c:/programs/eclipse/workspace/martus-amplifier/presentation/");
			
			server.addContext(new HttpContext(server, "/amplifier"));
			
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
