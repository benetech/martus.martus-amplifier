package org.martus.amplifier.service.search.api;

import org.martus.common.UniversalId;
import org.martus.amplifier.service.search.api.IBulletinSearcher;

/**
 * @author Bskinner
 *
 */
public interface IBulletinCatalog {
	public boolean bulletinHasBeenIndexed(UniversalId UniversalBulletinId);
}
