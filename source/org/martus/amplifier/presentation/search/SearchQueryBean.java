package org.martus.amplifier.presentation.search;

import java.util.Collection;

import org.martus.amplifier.service.search.BulletinField;
import org.martus.amplifier.service.search.IBulletinConstants;

public class SearchQueryBean implements IBulletinConstants
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
