<%@ page contentType="text/html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<html>
    <head>
            <title>Martus Amplifier Bulletin View</title>
            <link rel="stylesheet" href="stylesheets/style.css" type="text/css">
    </head>
    <body>
        <jsp:useBean 
            id="search" 
            class="org.martus.amplifier.presentation.search.SearchBean" 
            scope="session"/>
        <c:set var="bulletin" value="${search.results[param.index]}"/>
        <a href="index.jsp"><img src="images/big_logo.gif" border="0" width="184" height="126" alt="Martus Amplifier"/></a>
        <table border="0" width="840">
            <tr>
                <td>
                    <c:if test="${param.index > 0}">
                        <c:url value="viewbulletin.jsp" var="previous">
                            <c:param name="index" value="${param.index - 1}"/>
                        </c:url>
                        <a href="<c:out value="${previous}" />">Previous Bulletin</a>
                    </c:if>
                </td>
                <td><img src="images/spacer.gif" height="0" width="700"/></td>
                <td>
                    <c:if test="${param.index < search.lastIndex}">
                        <c:url value="viewbulletin.jsp" var="next">
                            <c:param name="index" value="${param.index + 1}"/>
                        </c:url>
                        <a href="<c:out value="${next}" />">Next Bulletin</a>
                    </c:if>
                </td>
        </table>
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td>
                    <img src="images/spacer.gif" width="40"/>    
                </td>
                <td>
                    <table width="800" border="1" cellspacing="0" cellpadding="0">
                        <tr>
                            <td>Title:</td><td><c:out value="${bulletin.fields.title}"/></td>
                        </tr>
                            <td>Author:</td><td><c:out value="${bulletin.fields.author}"/></td>
                        </tr>
                            <td>Event Date:</td><td><c:out value="${bulletin.fields.eventDate}"/></td>
                        </tr>
                            <td>Public Info:</td><td><c:out value="${bulletin.fields.publicInfo}"/></td>
                        </tr>
                            <td>Summary:</td><td><c:out value="${bulletin.fields.summary}"/></td>
                        </tr>
                            <td>Location:</td><td><c:out value="${bulletin.fields.location}"/></td>
                        </tr>
                            <td>Entry Date:</td><td><c:out value="${bulletin.fields.entryDate}"/></td>
                        </tr>
                    </table>
                </td>
            </tr>
            <c:if test="${!empty bulletin.attachments}">
                <tr>
                    <td><img src="images/spacer.gif" width="40"/></td>
                    <td>Attachments:</td>
                </tr>
                <tr>
                    <td><img src="images/spacer.gif" width="40"/></td>
                    <td>
                        <ul>
                            <c:forEach items="${bulletin.attachments}" var="attachment">
                                <c:url value="downloadattachment.jsp" var="download">
                                    <c:param name="accountId" value="${bulletin.accountId}"/>
                                    <c:param name="localId" value="${attachment.localId}"/>
                                </c:url>
                                <li><a href="<c:out value="${download}" />"><c:out value="${attachment.label}"/></a></li>
                            </c:forEach>
                        </ul>
                    </td>    
                </tr>
            </c:if>
        </table>
        <br>
        <jsp:include page="footer.jsp"/>
    </body>
</html>
