<%@ page import = "javax.servlet.*, javax.servlet.http.*, java.io.*, org.apache.lucene.search.Hits, org.martus.amplifier.presentation.search.SearchResultsBean" %>
<jsp:useBean id="searchResultsBean" class="org.martus.amplifier.presentation.search.SearchResultsBean" />
<%
    String queryString = request.getParameter("query");           //get the search criteria
    Hits hits = null;         
    String startVal    = request.getParameter("startat");         //get the start index
    String maxresults  = request.getParameter("maxresults");      //get max results per page
    int maxpage = 50;
    int startindex = 0;

    try 
    {
        maxpage    = Integer.parseInt(maxresults);    //parse the max results first
        startindex = Integer.parseInt(startVal);      //then the start index  
    } 
    catch (Exception e) 
    { } 
    //we don't care if something happens we'll just start at 0
                              //or end at 50
    
    if (queryString == null)
            throw new ServletException("no query "+       //if you don't have a query then
                                       "specified");      //you probably played on the 
    if(hits == null)
        hits= searchResultsBean.getSearchResults(queryString);
    
%>
                <table>
                <tr>
                        <td>Document</td>
                        <td>Summary</td>
                </tr>
<%
                if ((startindex + maxpage) > hits.length()) 
		{
                	thispage = hits.length() - startindex;      // set the max index to maxpage or last
                }                                                   // actual search result whichever is less

                for (int i = startindex; i < (thispage + startindex); i++) 
		{  // for each element
%>
                <tr>
<%
                        Document doc = hits.doc(i);                     
                        String doctitle = doc.get("title");            
                        String url = doc.get("summary");                   
                        if ((doctitle == null) || doctitle.equals(""))
                                doctitle = url;
%>
                        <td><a href="<%=url%>"><%=doctitle%></a></td>
                        <td><%=doc.get("summary")%></td>
                </tr>

<%                if ( (startindex + maxpage) < hits.length()) {   //if there are more results...display 
                                                                   //the more link

                        String moreurl="results.jsp?query=" + queryString +  //construct the "more" link
                                       "&maxresults=" + maxpage + 
                                       "&startat=" + (startindex + maxpage);
%>
                <tr>
                        <td></td><td><a href="<%=moreurl%>">More Results>></a></td>
                </tr>
<%
                }
%>
                </table>

<%       }
%>

