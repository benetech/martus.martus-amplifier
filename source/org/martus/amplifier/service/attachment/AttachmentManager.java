package org.martus.amplifier.service.attachment;

import java.io.File;
import java.io.FileNotFoundException;

import org.martus.amplifier.common.bulletin.UniversalBulletinId;

import com.sleepycat.db.Db;
import com.sleepycat.db.DbException;

public class AttachmentManager
{

	protected AttachmentManager()
	{
		super();
	}
	
	
	private void createDatabase()
	{
        try
        {
        	table = new Db(null, 0);
        	table.set_error_stream(System.err);
        	table.set_errpfx("AccessExample");
        	table.open(null, DATABASE_FILENAME, null, Db.DB_BTREE, Db.DB_CREATE, 0644);		
        }
        catch(DbException de)
        {}
        catch(FileNotFoundException fnfe)
        {}
        
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
		{}
	}
	
	public File getAttachment(String UniversalBulletinId)
	{
		return null;
	}
	
	
	private AttachmentManager instance = new AttachmentManager();
	private Db table =  null;
	private static final String DATABASE_FILENAME = "attachments.db";
}
