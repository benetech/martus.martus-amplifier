package org.martus.amplifier.presentation.search;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;

import org.martus.amplifier.common.configuration.AmplifierConfiguration;
import org.martus.amplifier.service.search.BulletinIndexException;
import org.martus.amplifier.service.search.BulletinSearcher;
import org.martus.amplifier.service.search.SearchConstants;
import org.martus.amplifier.service.search.lucene.LuceneBulletinSearcher;

public class SearchTag extends TagSupport implements TryCatchFinally
{
	public SearchTag()
	{
		AmplifierConfiguration config = 
			AmplifierConfiguration.getInstance();
		indexPath = 
			config.buildAmplifierBasePath(SearchConstants.INDEX_DIR_NAME);
	}
	
	public void setVar(String var)
	{
		varName = var;
	}
	
	public void setField(String field)
	{
		fieldName = field;
	}
	
	public void setQuery(String query)
	{
		queryString = query;
	}
	
	public void doCatch(Throwable t) throws Throwable 
	{
		throw t;
	}

	public void doFinally() 
	{
		if (searcher != null) {
			BulletinSearcher tmp = searcher;
			searcher = null;
			try {
				tmp.close();
			} catch (BulletinIndexException e) {
				// TODO pdalbora 28-Apr-2003 -- Handle this. Report it
				// at least.
			}
		}
	}

	public int doStartTag() throws JspException 
	{
		SearchResultsBean results;
		try {
			searcher = openBulletinSearcher();
			results = new SearchResultsBean(
				searcher.searchField(fieldName, queryString));
		} catch (BulletinIndexException e) {
			throw new JspException(e);
		}
		pageContext.setAttribute(varName, results);
		return EVAL_BODY_INCLUDE;
	}
	
	public int doEndTag() throws JspException 
	{
		pageContext.removeAttribute(varName);
		return super.doEndTag();
	}
	
	private BulletinSearcher openBulletinSearcher() 
		throws BulletinIndexException
	{
		return new LuceneBulletinSearcher(indexPath);
	}
	
	private BulletinSearcher searcher;
	private String varName;
	private String fieldName;
	private String queryString;
	private String indexPath;
	

}