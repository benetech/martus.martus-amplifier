package org.martus.amplifier.service.search.api;

import org.martus.common.UniversalId;

/**
 * An interface that represents a catalog of all of the 
 * bulletins that have been indexed.  
 * 
 * @author Bskinner
 * @version first created Dec 2002
 * @see org.martus.amplifier.service.search.BulletinCatalog
 *
 */
public interface IBulletinCatalog {
	
   /**
	* Returns true if a bulletin with a given id has been indexed.
	* 
	* @param universalId the id of a bulletin
	* @return true if the bulletin is in the index, or false if it is not
	*/
	public boolean bulletinHasBeenIndexed(UniversalId universalId);
}
