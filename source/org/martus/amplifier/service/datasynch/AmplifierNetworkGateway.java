package org.martus.amplifier.service.datasynch;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;

//from Martus common code
import org.martus.common.UniversalId;

public class AmplifierNetworkGateway
{


	private static List getAllAccountIds()
	{
		//fake data
		List fakeAccountIds = new ArrayList();
		fakeAccountIds.add("1");
		fakeAccountIds.add("2");
		return fakeAccountIds;
	}
	
	private static List getAccountBulletinIds(String accountId)
	{
		if(accountId == null)
			return null;
		// fake data
		List fakeBulletinIds = new ArrayList();
		if(accountId.equals("1"))
		{
			fakeBulletinIds.add("11");
			fakeBulletinIds.add("12");	
		}
		else
		{
			fakeBulletinIds.add("21");
			fakeBulletinIds.add("22");	
		}
		return fakeBulletinIds;
	}
	
	public static List getAllBulletinIds()
	{
		List allBulletinIds = new ArrayList();
		List allAccountIds = getAllAccountIds();
		if(allAccountIds == null) 
			return allBulletinIds;
		Iterator accountIdIterator = allAccountIds.iterator();
		String currentAccountId = null;
		List currentBulletinList = null;
		while(accountIdIterator.hasNext())
		{
			currentAccountId = (String) accountIdIterator.next();
			currentBulletinList = getAccountBulletinIds(currentAccountId);
			allBulletinIds.addAll(currentBulletinList);
		}			
		return allBulletinIds;
	}
	
	
	public static Vector getBulletin(UniversalId uid)
	{
		Vector result = new Vector();
		File tempFile = null;
		/*
		 * 1) retrieve Bulletin in Chunks and get the Zip file
		 * 2) Unzip the file and retrieve the bulletin and attachments
		 * 3) 
		 * 
		 */	 
		File bulletinZippedFile = new File("c:/srilatha/martus_data/Firebombing of NGO O13806.mbf");
		result = AmplifierUtilities.unZip(bulletinZippedFile);
		for(int i=0; i<result.size(); i++)
		{
			tempFile = (File)result.get(i);
			System.out.println("FileName is "+ tempFile.getName());
		}		
		return result;		
	}


	private static Vector getBulletinChunk(int chunkOffset, int maxChunkSize)
	{
		Vector result = new Vector();
		return result;	
	}
	
	
	private static Object callServer(String serverName, String method, Vector Params)
	{
		Object result = null;	
		return result;	
	}
	
}
