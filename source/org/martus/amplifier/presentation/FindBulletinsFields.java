
package org.martus.amplifier.presentation;

import java.util.Arrays;
import java.util.List;

import org.martus.amplifier.service.search.SearchConstants;


public class FindBulletinsFields implements SearchConstants
{
	public static List getFindBulletinsWordFilterDisplayNames()
	{
		return FILTER_NAMES;			
	}
	
	public static List getFindBulletinsFieldDisplayNames()
	{
		return FIELDS_NAMES;			
	}	
	
	public static String getFilterWordValue(String word)
	{
		return null;
	}
	
	public static String getBulletineFieldValue(String phrase)
	{	
		
		return null;
	}
	
	final static String THESE_WORD_KEY 		= "with all of these words";
	final static String EXACTPHRASE_KEY 	= "exactPhrase";
	final static String ANYWORD_KEY 		=  "anyWord";
	final static String WITHOUTWORDS_KEY  	= "withoutWords";
	
	final static String ANYWHER_IN_BULLETIN_KEY	= "anywhere in the bulletin";
	final static String IN_TITLE_KEY			= "in the title";
	final static String IN_KEYWORDS_KEY			= "in the keywords";
	final static String IN_SUMMARY_KEY			= "in the summary";
	final static String IN_AUTHOR_KEY			= "in the author";
	final static String IN_ORGANIZATION_KEY		= "in the organization";
	
	
	private static final List FILTER_NAMES = Arrays.asList(new String[] {
		THESE_WORD_KEY, EXACTPHRASE_KEY, ANYWORD_KEY, WITHOUTWORDS_KEY});		
		
	private static final List FIELDS_NAMES = Arrays.asList(new String[] {
		ANYWHER_IN_BULLETIN_KEY,IN_TITLE_KEY,IN_KEYWORDS_KEY, IN_SUMMARY_KEY,IN_AUTHOR_KEY,
		IN_ORGANIZATION_KEY});			
}
