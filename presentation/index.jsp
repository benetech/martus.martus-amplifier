<%@ page contentType="text/html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<html>
    <head>
        <title>Martus Amplifier Search Query</title>
        <link rel="stylesheet" href="stylesheets/style.css" type="text/css">
        <script language="Javascript" src="scripts/validation.js"></script>
    </head>
    
    <body>
        <jsp:useBean 
            id="search" 
            class="org.martus.amplifier.presentation.search.SearchBean"
            scope="session"/>

        <center>
            <table border="0" cellspacing="0" cellpadding="0">
            <tr>
            <td align="center">
                <img src="images/big_logo.gif" width="184" height="126" alt="Martus Amplifier"/>
            </tr>
            <tr>
            <td> 
                <form name="search" action="searchresults.jsp" onsubmit="return validateSubmission(this)" method="post">
                    <p>
                        <input name="query" size="44"/>&nbsp;<input type="submit" value="Search"/>
                    </p>
                    <p>
                        <input name="maxresults" size="4" value="100"/>&nbsp;Results Per Page&nbsp;
                        &nbsp;Field:&nbsp;
                        <select name="field" value="title">
                            <c:forEach items="${search.searchFields}" var="field">
                                <option value="<c:out value="${field.indexId}"/>">
                                    <c:out value="${field.displayName}"/>
                                </option>
                            </c:forEach>
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
    </body>
</html>

