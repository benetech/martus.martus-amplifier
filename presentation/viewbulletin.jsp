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
                </td>
                <td><img src="images/spacer.gif" height="0" width="700"/></td>
                <td>
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
                            <td>Title:</td><td><c:out value="${bulletin.title}"/></td>
                        </tr>
                            <td>Author:</td><td><c:out value="${bulletin.author}"/></td>
                        </tr>
                            <td>Event Date:</td><td><c:out value="${bulletin.eventDate}"/></td>
                        </tr>
                            <td>Public Info:</td><td><c:out value="${bulletin.publicInfo}"/></td>
                        </tr>
                            <td>Summary:</td><td><c:out value="${bulletin.summary}"/></td>
                        </tr>
                            <td>Location:</td><td><c:out value="${bulletin.location}"/></td>
                        </tr>
                            <td>Entry Date:</td><td><c:out value="${bulletin.entryDate}"/></td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
        <br>
        <jsp:include page="footer.jsp"/>
    </body>
</html>
