package org.martus.amplifier.service.datasynch;

public class BackupServerInfo
{

	public BackupServerInfo(String newName, String newAddress, int newPort)
	{
		name = newName;
		port = newPort;
		address = newAddress;
	}

	private String name = null;
	private String address = null;
	private int port = 0;
	
	public String getAddress()
	{
		return address;
	}

	public String getName()
	{
		return name;
	}

	public int getPort()
	{
		return port;
	}

}
