package org.martus.amplifier.service.datasynch;

import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.Enumeration;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;

public class AmplifierUtilities{
		
	public static Vector unZip(File file)
	{
		Vector fileList = new Vector();
	 	try
		{	
		 	ZipFile zipFile = new ZipFile(file);
		  	Enumeration entries = zipFile.entries();
		  	while(entries.hasMoreElements()) 
	        {
	        	ZipEntry entry = (ZipEntry)entries.nextElement();
	        	if(entry.isDirectory()) 
	        	{	//ignore directories
	          		(new File(entry.getName())).mkdir();
	          		continue;
	        	}
	        	
	        	File outFile = getFile(entry.getName(), zipFile.getInputStream(entry) );
	        	fileList.add(outFile);	           	
	       	}	        	
	     } 
      	 catch (IOException ioe) 
	     {
	      	System.err.println("Unhandled exception:");
	      	ioe.printStackTrace();
		 }
     	 return fileList; 
	}



	private static File getFile(String fileName,InputStream inStream)
	{	
		File outFile = null;
		try
		{
			outFile = new File(fileName);
			InputStream in = inStream;
			FileOutputStream fileOutputStream = new FileOutputStream(outFile);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
			byte[] buffer = new byte[1024];
			int len = 0;
			while( (len=in.read(buffer)) >= 0)
			{
				bufferedOutputStream.write(buffer, 0, len);	
			}
			bufferedOutputStream.flush();
			fileOutputStream.flush();
			in.close();
			bufferedOutputStream.close();
			fileOutputStream.close();
		}
		catch(IOException ioe)
		{
		  System.out.println("Unhandled exception");	
		  ioe.printStackTrace();
		}
		return outFile;	
		
	}
	
	
	
	
}
