<%@ page import = "javax.servlet.*, javax.servlet.http.*, java.io.*, org.apache.lucene.search.Hits, org.martus.amplifier.presentation.search.SearchResultsBean, org.apache.lucene.document.Document" %>
<head>
        <title>Martus Amplifier Bulletin View</title>
        <link rel="stylesheet" href="stylesheets/style.css" type="text/css">
</head>
<%
        String documentNumberString = request.getParameter("doc");
        int documentNumber = 0;
        try
        {
                documentNumber = Integer.parseInt(documentNumberString);
        }
        catch(Exception e)
        {}
        Hits hits = (Hits) request.getSession().getAttribute("CACHED_HITS");
        Document document = hits.doc(documentNumber);
        String title = document.get("title");
        String author = document.get("author");
        String eventDate = document.get("event_date");
        String publicInfo = document.get("public_info");
        String summary = document.get("summary");
        String location = document.get("location");
        String entryDate = document.get("entry_date");
%>
<img src="images/big_logo.gif" width="184" height="126" alt="Martus Amplifier"/>
<table border="0" cellspacing="0" cellpadding="0">
<tr>
<td>
        <img src="images/spacer.gif" width="40"/>    
</td>
<td>
        <table width="800" border="1" cellspacing="0" cellpadding="0">
        <tr>
        <td>Title:</td><td><%=title%></td>
        </tr>
        <td>Author:</td><td><%=author%></td>
        </tr>
        <td>Event Date:</td><td><%=eventDate%></td>
        </tr>
        <td>Public Info:</td><td><%=publicInfo%></td>
        </tr>
        <td>Summary:</td><td><%=summary%></td>
        </tr>
        <td>Location:</td><td><%=location%></td>
        </tr>
        <td>Entry Date:</td><td><%=entryDate%></td>
        </tr>
        </table>
</td>
</tr>
</table>
<br>
<jsp:include page="footer.jsp"/>
