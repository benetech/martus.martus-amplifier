/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2003, Beneficent
Technology, Inc. (Benetech).

Martus is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later
version with the additions and exceptions described in the
accompanying Martus license file entitled "license.txt".

It is distributed WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, including warranties of fitness of purpose or
merchantability.  See the accompanying Martus License and
GPL license for more details on the required license terms
for this software.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.

*/

package org.martus.amplifier.common;

import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import org.martus.amplifier.search.SearchConstants;
import org.martus.amplifier.velocity.*;

public class SearchParameters implements SearchResultConstants, SearchConstants
{
	public SearchParameters(AmplifierServletRequest request, SearchFields fields)
	{
		searchRequest = request;
		searchFields  = fields;
		loadFromRequest();		
	}
	
	private void loadFromRequest()
	{
		for(int i=0; i< ADVANCED_KEYS.length; i++)
		{
			String value = getParameterValue(ADVANCED_KEYS[i]);
			if (value != null)
			{		
				resultList.put(ADVANCED_KEYS[i], value);				
			}
		}
		
		if (!containsKey(RESULT_START_YEAR_KEY) || 
			!containsKey(RESULT_START_MONTH_KEY) ||
			!containsKey(RESULT_START_DAY_KEY) ||
			!containsKey(RESULT_END_YEAR_KEY) ||
			!containsKey(RESULT_END_MONTH_KEY) ||
			!containsKey(RESULT_END_DAY_KEY))
		{				
				hasEventFields = false;				
		}				
		
		setEventDate();
		setNormalFields();																	
	}	

	private void addField(String key, Object value)
	{
		searchFields.add(key, value);
	}
	
	private void setEventDate()
	{
		Date startDate	= getStartDate();
		Date endDate	= getEndDate();

		if (startDate != null && endDate != null)
		{			
			addField(SEARCH_EVENT_START_DATE_INDEX_FIELD, startDate);
			addField(SEARCH_EVENT_END_DATE_INDEX_FIELD, endDate);
		}	
	}

	private void setNormalFields()
	{
		addField(RESULT_FIELDS_KEY, resultList.get(RESULT_FIELDS_KEY));
		addField(RESULT_FILTER_BY_KEY, resultList.get(RESULT_FILTER_BY_KEY));
		addField(RESULT_LANGUAGE_KEY, resultList.get(RESULT_LANGUAGE_KEY));
		addField(RESULT_ENTRY_DATE_KEY,resultList.get(RESULT_ENTRY_DATE_KEY));	
	}
	
	public void setSearchFields(SearchFields result)
	{
		String fields = getParameterValue(RESULT_FIELDS_KEY);
		if (fields != null)
			result.add(fields, fields);
	}	

	public boolean containsKey(String key)
	{
		return resultList.containsKey(key);
	}	
		
	private String getParameterValue(String param)
	{
		return searchRequest.getParameter(param);
	}
	
	public String getValue(String key)
	{
		return (String) resultList.get(key);
	}	
	
	public Collection getSearchResultValues()
	{
		return resultList.values();
	}
	
	public boolean hasEventFieldKeys()
	{
		return hasEventFields;
	}
	
	public Date getStartDate()
	{	
		if (hasEventFieldKeys())
		 return getDate(Integer.parseInt(getValue(RESULT_START_YEAR_KEY)),					
		               	MonthFields.getIndexOfMonth(getValue(RESULT_START_MONTH_KEY)),
		               	Integer.parseInt(getValue(RESULT_START_DAY_KEY)));
		return null;
	}
		
	public Date getEndDate()
	{
		if (hasEventFieldKeys())
			return getDate(Integer.parseInt(getValue(RESULT_END_YEAR_KEY)),					
						MonthFields.getIndexOfMonth(getValue(RESULT_END_MONTH_KEY)),
						Integer.parseInt(getValue(RESULT_END_DAY_KEY)));
		return null;
	}
	
	public static Date getDate(int year, int month, int day)
	{
		return new GregorianCalendar(year, month, day).getTime();
	}	

	AmplifierServletRequest searchRequest;
	Hashtable resultList 	= new Hashtable();
	SearchFields	searchFields;
	boolean hasEventFields 	= true;
}
