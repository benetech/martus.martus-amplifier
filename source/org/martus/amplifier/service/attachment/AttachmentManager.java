package org.martus.amplifier.service.attachment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.martus.common.UniversalId;
import org.martus.amplifier.common.configuration.AmplifierConfiguration;
import org.martus.amplifier.service.attachment.api.AttachmentInfo;
import org.martus.amplifier.service.attachment.api.IAttachmentManager;

import com.sleepycat.db.Db;
import com.sleepycat.db.DbException;
import com.sleepycat.db.Dbc;

public class AttachmentManager implements IAttachmentConstants, IAttachmentManager
{
	protected AttachmentManager()
	{
		super();
		attachmentFileTable = 
			createDatabase(ATTACHMENT_FILE_DATABASE_FILENAME);
		attachmentIdTable = 
			createDatabase(ATTACHMENT_ID_DATABASE_FILENAME);
		attachmentNameTable = 
			createDatabase(ATTACHMENT_NAME_DATABASE_FILENAME);
	}
	
	public static AttachmentManager getInstance()
	{
		return instance;
	}
	
	private Db createDatabase(String fileName)
	{
        // Remove the previous database.
        new File(fileName).delete();
        Db database = null;
		try
        {
        	database = new Db(null, 0);
        	database.set_error_stream(System.err);
        	database.set_errpfx("AttachmentManager");
        	database.open(null, fileName, null, Db.DB_BTREE, Db.DB_CREATE, 0644);		
        }
        catch(DbException de)
        {
        	logger.severe("Unable to create attachment database: " + de.getMessage());
        }
        catch(FileNotFoundException fnfe)
        {
            logger.severe("Unable to create find attachment database file: " + fnfe.getMessage());
        }
        return database;        
	}
	
	public void putAttachmentIds(UniversalId universalId, List attachmentIdList)
	{
		if(attachmentIdList != null)
		{
			Iterator attachmentIdIterator = attachmentIdList.iterator();
			UniversalId currentAttachmentId = null;
			while(attachmentIdIterator.hasNext())
			{
				currentAttachmentId = (UniversalId) attachmentIdIterator.next();
				putAttachmentId(universalId, currentAttachmentId);
			}
		}	
	}

	public void putAttachmentId(UniversalId universalId, UniversalId attachmentId)
	{
		UniversalIdDbt key = new UniversalIdDbt(universalId);
		StringDbt value = new StringDbt(attachmentId.toString());
		int error = 0;
		try
		{
			error = attachmentIdTable.put(null, key, value, Db.DB_NOOVERWRITE);
		}
		catch(DbException de)
		{
			logger.severe("Unable to add attachment id to database: " + de.getMessage());
		}
	}

	public List getAttachmentIds(UniversalId UniversalBulletinId)
	{
		UniversalIdDbt key = new UniversalIdDbt(UniversalBulletinId);
		StringDbt returnValue = new StringDbt();
		File result = null;
		List attachmentIdList = new ArrayList();
		int error = 0;
		Dbc attachmentIdCursor = null;
		try
		{
			attachmentIdCursor = attachmentIdTable.cursor(null, 0);
			while(attachmentIdCursor.get(key, returnValue, Db.DB_NEXT) == 0)
			{
				attachmentIdList.add(returnValue.toString());
			}
		}
		catch(DbException de)
		{
			logger.severe("Unable to get attachment ids from database: " + de);
		}
		finally
		{
			try
			{
				attachmentIdCursor.close();
			}
			catch(DbException de)
			{
				logger.severe("Unable to close the attachment id database.");
			}
		}
		return attachmentIdList;
	}
	
	public void putAttachmentName(UniversalId universalId, String attachmentName)
	{
		UniversalIdDbt key = new UniversalIdDbt(universalId);
		StringDbt value = new StringDbt(attachmentName);
		int error = 0;
		try
		{
			error = attachmentNameTable.put(null, key, value, Db.DB_NOOVERWRITE);
		}
		catch(DbException de)
		{
			logger.severe("Unable to add attachment to database: " + de.getMessage());
		}
	}

	public String getAttachmentName(UniversalId UniversalBulletinId)
	{
		UniversalIdDbt key = new UniversalIdDbt(UniversalBulletinId);
		StringDbt returnValue = new StringDbt();
		File result = null;
		int error = 0;
		try
		{
			error = attachmentNameTable.get(null, key, returnValue, 0);
			if(error != 0)
				throw new DbException("Problems finding attachment.");
		}
		catch(DbException de)
		{
			logger.severe("Unable to get attachment from database: " + de);
		}
		return returnValue.getString();
	}

	public void putAttachmentFile(UniversalId universalId, File attachment)
	{
		UniversalIdDbt key = new UniversalIdDbt(universalId);
		FileDbt value = new FileDbt(attachment);
		int error = 0;
		try
		{
			error = attachmentFileTable.put(null, key, value, Db.DB_NOOVERWRITE);
		}
		catch(DbException de)
		{
			logger.severe("Unable to add attachment to database: " + de.getMessage());
		}
	}
	
	public File getAttachmentFile(UniversalId UniversalBulletinId, String filePath)
	{
		UniversalIdDbt key = new UniversalIdDbt(UniversalBulletinId);
		FileDbt returnValue = new FileDbt();
		File result = null;
		int error = 0;
		try
		{
			error = attachmentFileTable.get(null, key, returnValue, 0);
			if(error != 0)
				throw new DbException("Problems finding attachment.");
		}
		catch(DbException de)
		{
			logger.severe("Unable to get attachment from database: " + de);
		}
		return returnValue.getFile(filePath);
	}
	
	public void putAttachmentInfoList(UniversalId universalBulletinId, List attachmentInfoList)
	{
		if(attachmentInfoList == null)
			return;
		Iterator attachmentInfoIterator = attachmentInfoList.iterator();
		AttachmentInfo currentInfo = null;
		String attachmentPath = null;
		UniversalId attachmentId = null;
			
		while(attachmentInfoIterator.hasNext());
		{
			currentInfo = (AttachmentInfo) attachmentInfoIterator.next();
			attachmentId = currentInfo.getId();
			putAttachmentId(universalBulletinId, attachmentId);
			attachmentPath = 
				AmplifierConfiguration.getInstance().buildAmplifierWorkingPath(TEMP_ATTACHMENT_FOLDER, currentInfo.getId().toString());
			File attachmentFile = new File(attachmentPath);
			putAttachmentFile(attachmentId, attachmentFile);
			putAttachmentName(attachmentId, currentInfo.getLabel());
		}
	}
		
	private static AttachmentManager instance = new AttachmentManager();
	private Db attachmentFileTable =  null;
	private Db attachmentIdTable =  null;
	private Db attachmentNameTable =  null;
	private static final String TEMP_ATTACHMENT_FOLDER = "temp_attachment_folder";
	private static final String ATTACHMENT_FILE_DATABASE_FILENAME = "attachments_file.db";
	private static final String ATTACHMENT_ID_DATABASE_FILENAME = "attachments_id.db";
	private static final String ATTACHMENT_NAME_DATABASE_FILENAME = "attachments_name.db";
	private Logger logger = Logger.getLogger(ATTACHMENT_LOGGER);
}
