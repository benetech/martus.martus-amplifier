package org.martus.amplifier.main;


import java.util.logging.Logger;

import org.martus.amplifier.common.configuration.AmplifierConfiguration;
import org.martus.amplifier.service.attachment.AttachmentManager;
import org.martus.amplifier.service.attachment.AttachmentStorageException;
import org.martus.amplifier.service.attachment.filesystem.FileSystemAttachmentManager;
import org.martus.amplifier.service.datasynch.DataSynchManager;
import org.martus.amplifier.service.search.BulletinIndexException;
import org.martus.amplifier.service.search.BulletinIndexer;
import org.martus.amplifier.service.search.lucene.LuceneBulletinIndexer;

/**
 * @author SKoneru
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class MartusAmplifierDataSynch {

	/**
	 * Constructor for MartusAmplifierDataSynch.
	 */
	public MartusAmplifierDataSynch() {
		super();
	}

	public void execute() 
	{
		
		BulletinIndexer indexer = null;
		AttachmentManager attachmentManager = null;
		try
		{
			DataSynchManager dataManager = DataSynchManager.getInstance();
			AmplifierConfiguration config = 
				AmplifierConfiguration.getInstance();
			indexer = new LuceneBulletinIndexer(
				config.getBasePath());
			attachmentManager = new FileSystemAttachmentManager(
				config.getBasePath());

			dataManager.getAllNewBulletins(attachmentManager, indexer);
		}
		catch(Exception e)
		{
			logger.severe("MartusAmplifierDataSynch.execute(): " + e.getMessage());
			e.printStackTrace();
		} 
		finally
		{
			if (indexer != null) {
				try {
					indexer.close();
				} catch (BulletinIndexException e) {
					logger.severe(
						"Unable to close the indexer: " + e.getMessage());
				}
			}
			
			if (attachmentManager != null) {
				try {
					attachmentManager.close();
				} catch (AttachmentStorageException e) {
					logger.severe(
						"Unable to close the attachment manager: " +
						e.getMessage());
				}
			}
		}
					
	}
	
	
	private static Logger logger = Logger.getLogger("MainTask");
	
}
