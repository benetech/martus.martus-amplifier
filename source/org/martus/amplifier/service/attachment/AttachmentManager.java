package org.martus.amplifier.service.attachment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.martus.amplifier.common.configuration.AmplifierConfiguration;
import org.martus.amplifier.service.attachment.api.AttachmentInfo;
import org.martus.amplifier.service.attachment.api.IAttachmentManager;
import org.martus.amplifier.service.attachment.exception.UnableToDecryptAttachmentException;
import org.martus.amplifier.service.attachment.exception.UnableToFindAttachmentXmlException;
import org.martus.common.AttachmentPacket;
import org.martus.common.FileInputStreamWithSeek;
import org.martus.common.MartusCrypto;
import org.martus.common.MartusSecurity;
import org.martus.common.UniversalId;
import org.martus.common.Base64.InvalidBase64Exception;
import org.martus.common.MartusCrypto.CryptoInitializationException;
import org.martus.common.Packet.InvalidPacketException;
import org.martus.common.Packet.SignatureVerificationException;
import org.martus.common.Packet.WrongPacketTypeException;

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

	public boolean hasAttachments(UniversalId bulletinId)
	{
		List attachmentIds = getAttachmentIds(bulletinId);
		if(attachmentIds != null && !attachmentIds.isEmpty())
			return true;
		return false;
	}
	
	public void putAttachmentId(UniversalId universalId, UniversalId attachmentId)
	{
		UniversalIdDbt key = new UniversalIdDbt(universalId);
		StringDbt value = new StringDbt(attachmentId.toString());
		int error = 0;
		try
		{
			error = attachmentIdTable.put(null, key, value, Db.DB_NOOVERWRITE);
			if(error != 0)
				throw new DbException("Problems finding attachment.");
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
		List attachmentIdList = new ArrayList();
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
			if(error != 0)
				throw new DbException("Problems finding attachment.");
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
			if(error != 0)
				throw new DbException("Problems finding attachment.");
		}
		catch(DbException de)
		{
			logger.severe("Unable to add attachment to database: " + de.getMessage());
		}
	}
	
	public File getAttachmentFile(UniversalId UniversalBulletinId, String filePath, String fileName)
	{
		UniversalIdDbt key = new UniversalIdDbt(UniversalBulletinId);
		FileDbt returnValue = new FileDbt();
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
		return returnValue.getFile(filePath, fileName);
	}
	
	public void putAttachmentInfoList(UniversalId universalBulletinId, List attachmentInfoList)
	{
		if(attachmentInfoList == null)
			return;
		Iterator attachmentInfoIterator = attachmentInfoList.iterator();
		AttachmentInfo currentInfo = null;
		String attachmentPath = null;
		String attachmentLocalId = null;
			
		while(attachmentInfoIterator.hasNext());
		{
			currentInfo = (AttachmentInfo) attachmentInfoIterator.next();
			attachmentLocalId = currentInfo.getLocalId();
			UniversalId attachmentId = 
				UniversalId.createFromAccountAndLocalId(
					universalBulletinId.getAccountId(), 
					attachmentLocalId);
			putAttachmentId(universalBulletinId, attachmentId);
			attachmentPath = 
				AmplifierConfiguration.getInstance().buildAmplifierWorkingPath(TEMP_ATTACHMENT_FOLDER, attachmentId.toString());
			File attachmentFile = new File(attachmentPath);
			putAttachmentFile(attachmentId, attachmentFile);
			putAttachmentName(attachmentId, currentInfo.getLabel());
		}
	}
		
	public void extractAttachment(File xmlFile, byte[] sessionKey, File destinationFile)
	throws UnableToFindAttachmentXmlException, UnableToDecryptAttachmentException
	{
		FileInputStreamWithSeek attachmentXml = null;
		try
		{
			attachmentXml = new FileInputStreamWithSeek(xmlFile);
		}
		catch(IOException ioe)
		{
			throw new UnableToFindAttachmentXmlException(ioe.getMessage());
		}
		try
		{
			MartusCrypto verifier = new MartusSecurity();
			destinationFile.createNewFile();
			AttachmentPacket.exportRawFileFromXml(attachmentXml, sessionKey, verifier, destinationFile);
		}
		catch(CryptoInitializationException cie)
		{
			logger.severe(cie.getMessage());
		}
		catch(InvalidBase64Exception ibe)
		{			
			logger.severe(ibe.getMessage());
		}
		catch(SignatureVerificationException sve)
		{
			logger.severe(sve.getMessage());
		}
		catch(InvalidPacketException ipe)
		{
			logger.severe(ipe.getMessage());
		}
		catch(WrongPacketTypeException wpte)
		{
			logger.severe(wpte.getMessage());		
		}
		catch(IOException ioe)
		{
			logger.severe(ioe.getMessage());
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
