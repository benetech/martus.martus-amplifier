package org.martus.amplifier.service.attachment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.martus.amplifier.common.bulletin.UniversalBulletinId;

import com.sleepycat.db.Db;
import com.sleepycat.db.DbException;

public class AttachmentManager implements IAttachmentConstants
{

	protected AttachmentManager()
	{
		super();
	}
	
	
	private void createDatabase()
	{
        // Remove the previous database.
        new File(DATABASE_FILENAME).delete();
		try
        {
        	table = new Db(null, 0);
        	table.set_error_stream(System.err);
        	table.set_errpfx("AttachmentManager");
        	table.open(null, DATABASE_FILENAME, null, Db.DB_BTREE, Db.DB_CREATE, 0644);		
        }
        catch(DbException de)
        {
        	logger.severe("Unable to create attachment database: " + de.getMessage());
        }
        catch(FileNotFoundException fnfe)
        {
            logger.severe("Unable to create find attachment database file: " + fnfe.getMessage());
        }
        
	}
	
	public void putAttachment(UniversalBulletinId universalId, File attachment)
	{
		UniversalIdDbt key = new UniversalIdDbt(universalId);
		AttachmentDbt value = new AttachmentDbt(attachment);
		int error = 0;
		try
		{
			error = table.put(null, key, value, Db.DB_NOOVERWRITE);
		}
		catch(DbException de)
		{
			logger.severe("Unable to add attachment to database: " + de.getMessage());
		}
	}
	
	public File getAttachment(UniversalBulletinId UniversalBulletinId)
	{
		return null;
	}
	
	
	private AttachmentManager instance = new AttachmentManager();
	private Db table =  null;
	private static final String DATABASE_FILENAME = "attachments.db";
	private Logger logger = Logger.getLogger(ATTACHMENT_LOGGER);
}
