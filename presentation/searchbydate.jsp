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
                                <option>January</option>
                                </select>
                                <select name="start_day">
                                <option>1</option>
                                <option>2</option>
                                <option>3</option>
                                <option>4</option>
                                <option>5</option>
                                </select>
                                <input name="start_year" size="4" value="2002">
                        </p>
                        <p>                                
                                and
                                <select name="end_month">
                                <option>January</option>
                                <option selected>December</option>
                                </select>
                                <select name="end_day">
                                <option>1</option>
                                <option>2</option>
                                <option>3</option>
                                <option>4</option>
                                <option>5</option>
                                <option selected>31</option>
                                </select>
                                <input name="end_year" size="4" value="2002">
                                <input type="submit" value="Search"/>
			</p>
			<p>
				<input name="maxresults" size="4" value="100"/>&nbsp;Results Per Page&nbsp;
			</p>
	        </form>
        </td>
        </tr>
        <tr>
        <td align="right">
                <a href="index.jsp">Basic search</a>
        </td>
        </tr>
        </table>
</center>
<jsp:include page="footer.jsp"/>

