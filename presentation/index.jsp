<%@ page import = "java.util.List, java.util.Iterator, org.martus.amplifier.presentation.search.SearchQueryBean" %>
<jsp:useBean id="searchQueryBean" class="org.martus.amplifier.presentation.search.SearchQueryBean" />
<head>
        <title>Martus Amplifier Search Query</title>
        <link rel="stylesheet" href="stylesheets/style.css" type="text/css">
</head>
<script language="Javascript">
onSubmit()
{
        if(document.search.query = "")
                alert("Please enter a query hitting the search button.");
        else
                search.submit();
}
</script>
<%
//clear cached hits
        request.getSession().setAttribute("CACHED_HITS", null);
%>
<center>
	<table border="1" cellspacing="0" cellpadding="0">
	<tr>
	<td align="center">
		<img src="images/big_logo.gif" width="184" height="126" alt="Martus Amplifier"/>
	</tr>
	<tr>
	<td> 
		<form name="search" action="searchresults.jsp" method="get">
			<p>
				<input name="query" size="44"/>&nbsp;<input type="submit" value="Search"/>
			</p>
			<p>
				<input name="maxresults" size="4" value="100"/>&nbsp;Results Per Page&nbsp;
				&nbsp;Field:&nbsp;
				<select name="field" value="title">
				<%
					List searchFields = searchQueryBean.getSearchFields();
					if(searchFields != null)
					{
						Iterator searchFieldsIterator = searchFields.iterator();
						String fieldName = null;
						while(searchFieldsIterator.hasNext())
						{
							fieldName = (String) searchFieldsIterator.next();
				%>
					<option><%=fieldName%></option>			 
				<% 		}
                                        }
				%>
				</select>
			</p>
                        <p>
                                <input type="checkbox" name="case_sensitive"/> Case Sensitive
                                <input type="checkbox" name="whole_word"/> Whole Word Only
                        </p>
	        </form>
        </td>
        </tr>
        <tr>
        <td align="center">
        <a href="bugreports.html">Bug Reports</a>&nbsp;|&nbsp;<a href="faq.html">FAQ</a>&nbsp;|&nbsp;<a href="searchbydate.jsp">Search by Date</a>
        </td>
        </tr>
        </table>
</center>
<jsp:include page="footer.jsp"/>

