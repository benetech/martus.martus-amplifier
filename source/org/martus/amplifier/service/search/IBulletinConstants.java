package org.martus.amplifier.service.search;

import org.martus.amplifier.common.configuration.AmplifierConfiguration;

/**
 * @author Daniel Chu
 *
 * This interface holds all the shared constants and imports
 * for the Martus Amplifier searching code
 * 
 */
public interface IBulletinConstants
{
	public static final String FILES_FOLDER = "Bulletin_Folder";
	public static final String DEFAULT_FILES_LOCATION = 
		AmplifierConfiguration.getInstance().buildAmplifierWorkingPath(FILES_FOLDER);
	public static final String INDEX_FOLDER = "amplifier_index";
	public static final String DEFAULT_INDEX_LOCATION = 
		AmplifierConfiguration.getInstance().buildAmplifierWorkingPath(INDEX_FOLDER);

	

}
