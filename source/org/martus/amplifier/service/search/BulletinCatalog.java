package org.martus.amplifier.service.search;

import org.apache.lucene.search.Hits;
import org.martus.amplifier.service.search.api.IBulletinCatalog;
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
        BulletinSearcher bulletinSearcher = BulletinSearcher.getInstance();
        Hits hits = bulletinSearcher.searchKeywordField(ISearchConstants.UNIVERSAL_ID_INDEX_FIELD, universalId.toString());

        return ((hits != null) && (hits.length() == 1));
	}
	
	private static BulletinCatalog instance = new BulletinCatalog();

}
