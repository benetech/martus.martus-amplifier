<%@ page contentType="text/html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<html>
    <head>
            <title>Martus Amplifier Search Results</title>
            <link rel="stylesheet" href="stylesheets/style.css" type="text/css">
    </head>
    <body>
        <jsp:useBean 
            id="search" 
            class="org.martus.amplifier.presentation.search.SearchBean"
            scope="session"/>
        <jsp:setProperty name="search" property="*"/>
        
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
        <c:choose>
            <c:when test="${empty search.results}">
                <tr>
                    <td align="center">
                    No documents matched your search query.
                    Click <a href="index.jsp">here</a> to try a different search query.
                    </td>
                </tr>
            </c:when>
            <c:otherwise>
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
                <c:forEach begin="${search.startIndex}" end="${search.lastIndexInCurrentPage}" var="i">
                    <tr>
                        <td>&nbsp;</td>
                        <td><c:out value="${search.results[i].author}"/></td>
                        <td><c:out value="${search.results[i].eventDate}"/></td>
                        <c:url value="viewbulletin.jsp" var="resultDetail">
                            <c:param name="index" value="${i}"/>
                        </c:url>
                        <td><a href="<c:out value="${resultDetail}"/>"><c:out value="${search.results[i].title}"/></a></td>
                    </tr>
                </c:forEach> 
            </c:otherwise>
        </c:choose>
        </table>
        <jsp:include page="footer.jsp"/>
    </body>
</html>
