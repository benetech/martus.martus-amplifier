<head>
        <title>Martus Amplifier Date Search</title>
        <link rel="stylesheet" href="stylesheets/style.css" type="text/css">
</head>
<%
//clear cached hits
        request.getSession().setAttribute("CACHED_HITS", null);
%>
<center>
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
	<td align="center">
		<img src="images/big_logo.gif" width="184" height="126" alt="Martus Amplifier"/>
	</tr>
	<tr>
	<td> 
		<form name="search" action="searchresults.jsp" method="get">
			<p>
				The 
                                <select name="field">
                                <option>entry date</option>
                                <option>event date</option>
                                </select>
                                is between 
                                <select name="start_month">
                                <option>Beginning of time</option>
                                <option>January</option>
                                </select> (Month)
                                <select name="start_day">
                                </select>
                                <input name="query" size="44"/>&nbsp;<input type="submit" value="Search"/>
			</p>
			<p>
				<input name="maxresults" size="4" value="100"/>&nbsp;Results Per Page&nbsp;
			</p>
	        </form>
        </td>
        </tr>
        </table>
</center>
<jsp:include page="footer.jsp"/>

