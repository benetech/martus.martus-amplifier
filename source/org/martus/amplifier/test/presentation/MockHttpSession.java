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
package org.martus.amplifier.test.presentation;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;


	public class MockHttpSession implements HttpSession
	{
		
		public Object getAttribute(String key)
		{
			if(attributes.containsKey(key))
				return attributes.get(key);
			return null;
		}

		public Enumeration getAttributeNames()
		{
			// TODO Auto-generated method stub
			return null;
		}

		public long getCreationTime()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		public String getId()
		{
			// TODO Auto-generated method stub
			return null;
		}

		public long getLastAccessedTime()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		public int getMaxInactiveInterval()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		public ServletContext getServletContext()
		{
			// TODO Auto-generated method stub
			return null;
		}

		public HttpSessionContext getSessionContext()
		{
			// TODO Auto-generated method stub
			return null;
		}

		public Object getValue(String arg0)
		{
			// TODO Auto-generated method stub
			return null;
		}

		public String[] getValueNames()
		{
			// TODO Auto-generated method stub
			return null;
		}

		public void invalidate()
		{
			// TODO Auto-generated method stub
		}

		public boolean isNew()
		{
			// TODO Auto-generated method stub
			return false;
		}

		public void putValue(String arg0, Object arg1)
		{
			// TODO Auto-generated method stub
		}

		public void removeAttribute(String arg0)
		{
			// TODO Auto-generated method stub
		}

		public void removeValue(String arg0)
		{
			// TODO Auto-generated method stub
		}

		public void setAttribute(String key , Object value)
		{
			attributes.put(key, value);
		}

		public void setMaxInactiveInterval(int arg0)
		{
			// TODO Auto-generated method stub
		}
		Map attributes = new HashMap();

}
