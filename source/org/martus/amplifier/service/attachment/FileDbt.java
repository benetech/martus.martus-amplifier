package org.martus.amplifier.service.attachment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import com.sleepycat.db.Db;
import com.sleepycat.db.Dbt;

public class FileDbt extends Dbt 
implements IAttachmentConstants
{
	/**
	 * For use with getting data
	 */
	public FileDbt()
	{
		super();
		set_flags(Db.DB_DBT_MALLOC);
	}

	public FileDbt(File attachment)
	{
		super();
		set_flags(Db.DB_DBT_MALLOC);
		copyFileIntoData(attachment);
	}
	
	public File getFile(String filePath)
	{
		try
		{
			FileOutputStream fileStream = 
				new FileOutputStream(filePath);
			fileStream.write(get_data());
			fileStream.close();
		}
		catch(IOException ioe)
		{
			logger.severe("Unable to write attachment file: " + ioe.getMessage());
		}
		return new File(filePath);		
	}
	
	private void copyFileIntoData(File attachment)
	{
		try
		{
			FileInputStream attachmentStream = new FileInputStream(attachment);
			ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
			int currentData = 0;
			
			while((currentData = attachmentStream.read()) != -1)
			{
				dataStream.write(currentData);
			} 
			byte[] data = dataStream.toByteArray();
			set_data(data);
			set_size(data.length);
			attachmentStream.close();
			dataStream.close();
		}
		catch(FileNotFoundException fnfe)
		{
			logger.severe("Unable to find attachment file: " + fnfe.getMessage());
		}
		catch(IOException ioe)
		{
			logger.severe("Unable to read attachment file: " + ioe.getMessage());		
		}
		
	}
	private static Logger logger = Logger.getLogger(ATTACHMENT_LOGGER);
}
