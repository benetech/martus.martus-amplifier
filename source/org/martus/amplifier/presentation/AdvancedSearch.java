package org.martus.amplifier.presentation;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;

public class AdvancedSearch extends AmplifierServlet
{
	public String selectTemplate(HttpServletRequest request, HttpServletResponse response, Context context)
	{
		Vector searchableDateFields = new Vector();
		
		searchableDateFields.add("Entry Date");
		searchableDateFields.add("Event Date");
		
		context.put("searchableDateFields", searchableDateFields);
		return "AdvancedSearch.vm";
	}
}
