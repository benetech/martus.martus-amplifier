package org.martus.amplifier.presentation.search;

import java.io.Serializable;
import java.util.Collection;

import org.martus.amplifier.service.search.BulletinField;

public class SearchBean implements Serializable
{
	public Collection getSearchFields()
	{
		return BulletinField.getSearchableTextFields();
	}
}

