package org.martus.amplifier.presentation;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.velocity.context.Context;

public class SimpleSearch extends AmplifierServlet
{
    public String selectTemplate(HttpServletRequest request,
				HttpServletResponse response, 
				Context context)
	{
    	HttpSession session = request.getSession();
    	String sessionId = (String)session.getAttribute("name");
    	if(sessionId == null)
    		sessionId = "my session";
    		
    	context.put("session", sessionId);

        Vector fields = new Vector();
		fields.addElement("Author");
		fields.addElement("Keywords");
		fields.addElement("Title");
		fields.addElement("Event Date");
		fields.addElement("Details");
		fields.addElement("Summary");
		fields.addElement("Location");
		fields.addElement("Entry Date");
		context.put("theFields", fields);
		
		context.put("name", request.getParameter("query"));
		
		return "SimpleSearch.vm";
    }
}
