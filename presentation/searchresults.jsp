<%@ page import = "javax.servlet.*, javax.servlet.http.*, java.io.*, org.apache.lucene.search.Hits, org.martus.amplifier.presentation.search.SearchResultsBean, org.apache.lucene.document.Document" %>
<jsp:useBean id="searchResultsBean" class="org.martus.amplifier.presentation.search.SearchResultsBean" />
<head>
        <title>Martus Amplifier Search Results</title>
        <link rel="stylesheet" href="stylesheets/style.css" type="text/css">
</head>
<table border="0" cellspacing="0" cellpadding="0">
<tr>
<td>
        <img src="images/martus_logo.gif" width="457" height="114" alt="Image: Martus logo">
        <img src="images/martus_logo_sub.gif" width="151" height="23" alt="">
</td>
<td>
        <h2>Search Results</h2>
</td>
</tr>
</table>
<hr>
<table width="800" border="0" cellpadding="0" cellspacing="0">
<%
        // width="211" height="140"
        String queryString = request.getParameter("query");           
        String startVal    = request.getParameter("startat");         
        String maxresults  = request.getParameter("maxresults");
        String field 	= request.getParameter("field");
        Hits hits = null;         
        int maxpage = 50;
        int startindex = 0;
        
        try 
        {
                maxpage    = Integer.parseInt(maxresults);    
                startindex = Integer.parseInt(startVal);  
        } 
        catch (Exception e) 
        { } 
        //we don't care if something happens we'll just start at 0 or end at 50
        
        if (queryString == null)
                throw new ServletException("No query specified");
        if(hits == null)
                hits= searchResultsBean.getSearchResults(field, queryString);
        if(hits.length() == 0)
        {
%>
<tr>
<td>No documents matched your search query.</td>
</tr>
<%
        }
        else
        {
                int thispage = maxpage;
                if ((startindex + maxpage) > hits.length()) 
                        thispage = hits.length() - startindex;
                
                Document doc = null;
                for (int i = startindex; i < (thispage + startindex); i++) 
                {        
%>
<tr>
<%
                        doc = hits.doc(i);                     
                        String doctitle = doc.get("title");            
                        String path = doc.get("path");                   
                        if ((doctitle == null) || doctitle.equals(""))
                                doctitle = path;
%>
        <td><a href="<%=path%>"><%=doctitle%></a></td>
        <td><%=doc.get("summary")%></td>
</tr>

<%                
                        //if there are more documents to display, show the more link
                        if ( (startindex + maxpage) < hits.length()) 
                        {    
                                String moreurl="results.jsp?query=" + queryString +
                                               "&maxresults=" + maxpage + 
                                               "&startat=" + (startindex + maxpage);
%>
<tr>
        <td></td><td><a href="<%=moreurl%>">More Results>></a></td>
</tr>
<%
                        }
             }
        }
%>
 </table>

