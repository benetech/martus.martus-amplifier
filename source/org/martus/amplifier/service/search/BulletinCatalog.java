package org.martus.amplifier.service.search;

import java.util.logging.Logger;

import org.apache.lucene.search.Hits;
import org.martus.amplifier.common.configuration.AmplifierConfiguration;
import org.martus.amplifier.service.search.api.IBulletinCatalog;
import org.martus.amplifier.service.search.lucene.LuceneBulletinSearcher;
import org.martus.common.UniversalId;

/**
 * A class that represents a catalog of all of the 
 * bulletins that have been indexed.  
 * 
 * @author Bskinner
 * @version first created Dec 2002
 *
 */
public class BulletinCatalog implements IBulletinCatalog {
	protected BulletinCatalog()
	{}
	
	public static BulletinCatalog getInstance() 
	{
		return instance;
	}
   
	public boolean bulletinHasBeenIndexed(UniversalId universalId)
	{
        AmplifierConfiguration config = AmplifierConfiguration.getInstance();
        BulletinSearcher searcher = null;
        try {
        	searcher = 
        		new LuceneBulletinSearcher(config.buildAmplifierBasePath(BulletinSearcher.INDEX_DIR_NAME));
        	return (searcher.getBulletinData(universalId) != null);
        } catch (BulletinIndexException e) {
        	Logger.getLogger("catalog").severe("Catalog error: " + e.getMessage());
        } finally {
        	if (searcher != null) {
        		try {
        			searcher.close();
        		} catch (BulletinIndexException e) {
        			Logger.getLogger("catalog").severe("Catalog error: " + e.getMessage());
        		}
        	}
        }
        return false;
	}
	
	private static BulletinCatalog instance = new BulletinCatalog();

}
