package org.martus.amplifier.presentation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.servlet.VelocityServlet;

public class AdvancedSearch extends VelocityServlet
{
	/**
	 *   Called by the VelocityServlet
	 *   init().  We want to set a set of properties
	 *   so that templates will be found in the webapp
	 *   root.  This makes this easier to work with as 
	 *   an example, so a new user doesn't have to worry
	 *   about config issues when first figuring things
	 *   out
	 */
	protected Properties loadConfiguration(ServletConfig config )
		throws IOException, FileNotFoundException
	{
		Properties p = new Properties();

		/*
		 *  first, we set the template path for the
		 *  FileResourceLoader to the root of the 
		 *  webapp.  This probably won't work under
		 *  in a WAR under WebLogic, but should 
		 *  under tomcat :)
		 */

		String path = config.getServletContext().getRealPath("/");

		if (path == null)
		{
			System.out.println(" SampleServlet.loadConfiguration() : unable to " 
							   + "get the current webapp root.  Using '/'. Please fix.");

			path = "/";
		}

		p.setProperty( Velocity.FILE_RESOURCE_LOADER_PATH,  path );

		/**
		 *  and the same for the log file
		 */

		p.setProperty( "runtime.log", path + "velocity.log" );

		return p;
	}


	/**
	 *  <p>
	 *  main routine to handle a request.  Called by
	 *  VelocityServlet, your responsibility as programmer
	 *  is to simply return a valid Template
	 *  </p>
	 *
	 *  @param ctx a Velocity Context object to be filled with
	 *             data.  Will be used for rendering this 
	 *             template
	 *  @return Template to be used for request
	 */   
	public Template handleRequest(HttpServletRequest request,
						HttpServletResponse response, Context ctx )
	{      
    	
		Template outty = null;
        
		try
		{
			outty =  getTemplate("AdvancedSearch.vm");
		}
		catch( ParseErrorException pee )
		{
			System.out.println("AdvancedSearch : parse error for template " + pee);
		}
		catch( ResourceNotFoundException rnfe )
		{
			System.out.println("AdvancedSearch : template not found " + rnfe);
		}
		catch( Exception e )
		{
			System.out.println("Error " + e);
		}
		return outty;
	}
}
