package org.martus.amplifier.service.search;

import org.martus.common.UniversalId;
import org.martus.amplifier.service.search.api.IBulletinCatalog;
import org.martus.amplifier.service.search.IBulletinConstants;
import org.apache.lucene.search.Hits;

/**
 * @author Bskinner
 *
 */
public class BulletinCatalog implements IBulletinCatalog {
	protected BulletinCatalog()
	{}
	
	public static BulletinCatalog getInstance() 
	{
		return instance;
	}
   
	public boolean bulletinHasBeenIndexed(UniversalId UniversalBulletinId) {
        BulletinSearcher bulletinSearcher = BulletinSearcher.getInstance();
//        Hits hits = bulletinSearcher.searchField(IBulletinConstants.UNIVERSAL_ID_FIELD, "\""+UniversalBulletinId.toString()+"\"");
        Hits hits = bulletinSearcher.searchKeywordField(IBulletinConstants.UNIVERSAL_ID_FIELD, UniversalBulletinId.toString());

        return ((hits != null) && (hits.length() == 1));
	}
	
	private static BulletinCatalog instance = new BulletinCatalog();

}
