package org.martus.amplifier.presentation.search;

import java.util.Collection;

import org.martus.amplifier.service.search.BulletinField;

public class SearchQueryBean
{

	public SearchQueryBean()
	{
		super();
	}

	public Collection getSearchFields()
	{
		return BulletinField.getSearchableFields();
	}
	
}
