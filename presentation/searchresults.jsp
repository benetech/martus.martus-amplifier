<%@ page import = "org.apache.lucene.search.Hits, org.martus.amplifier.presentation.search.SearchResultsBean, org.apache.lucene.document.Document" %>
<jsp:useBean id="searchResultsBean" class="org.martus.amplifier.presentation.search.SearchResultsBean" />
<head>
        <title>Martus Amplifier Search Results</title>
        <link rel="stylesheet" href="stylesheets/style.css" type="text/css">
</head>
<table width="800" border="0" cellspacing="0" cellpadding="0">
<tr>
<td width="457">
        <a href="index.jsp"><img src="images/martus_logo.gif" border="0" width="457" height="114" alt="Image: Martus logo">
        <img src="images/martus_logo_sub.gif" border="0" width="151" height="23" alt=""></a>
</td>
</tr>
</table>
<hr>
<table width="800" border="0" cellpadding="0" cellspacing="0">
<tr>
<td>
        <img src="images/spacer.gif" width="20" height="0"/>
</td>
<td colspan="3">
        <img src="images/spacer.gif" width="0" height="30"/>
</td>
</tr>
<%
        // width="211" height="140"
        String queryString = request.getParameter("query");           
        String startVal    = request.getParameter("startat");         
        String maxresults  = request.getParameter("maxresults");
        String field 	= request.getParameter("field");
        Hits hits = (Hits) request.getSession().getAttribute("CACHED_HITS");         
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
        if(queryString != null)
        {
                // if there is a queryString then its a new search, clear out the cache
                hits = null;
                request.getSession().setAttribute("CACHED_HITS", null);
        }
        if(hits == null)
        {
                hits= searchResultsBean.getSearchResults(field, queryString);
                request.getSession().setAttribute("CACHED_HITS", hits);
        }

        if(hits.length() == 0)
        {
%>
<tr>
<td align="center">
No documents matched your search query.
Click <a href="index.jsp">here</a> to try a different search query.
</td>
</tr>
<%
        }
        else
        {
%>
<tr>
<td>&nbsp;</td>
<td>Author</td>
<td>Event Date</td>
<td>Title</td>
</tr>
<tr>
<td>&nbsp;</td>
<td colspan="3"><img src="images/spacer.gif" width="0" height="5"/></td>
</tr>
<%
                int thispage = maxpage;
                if ((startindex + maxpage) > hits.length()) 
                        thispage = hits.length() - startindex;
                
                Document doc = null;
                String author = null;
                String title = null;
                String eventDate = null;
                String viewBulletinURL = null;
                for (int i = startindex; i < (thispage + startindex); i++) 
                {        
%>
<tr>
<%
                        doc = hits.doc(i);       
                        author = doc.get("author");
                        title = doc.get("title");  
                        eventDate = doc.get("event_date");
                        
                        viewBulletinURL = "viewbulletin.jsp?doc=" + i;                   
%>
        <td>&nbsp;</td>
        <td><%=author%></td>
        <td><%=eventDate%></td>
        <td><a href="<%=viewBulletinURL%>"><%=title%></a></td>
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
<jsp:include page="footer.jsp"/>
