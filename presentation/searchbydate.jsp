<%@ page contentType="text/html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<html>

    <head>
            <title>Martus Amplifier Date Search</title>
            <link rel="stylesheet" href="stylesheets/style.css" type="text/css">
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
                    <form name="search" action="validatedates.jsp" method="get">
                        <p>
                            The 
                            <select name="field">
                                <c:forEach items="${search.dateSearchFields}" var="field">
                                    <option value="<c:out value="${field.indexId}"/>">
                                        <c:out value="${field.displayName}"/>
                                    </option>
                                </c:forEach>
                            </select>
                            is 
			</p>
			<p> &nbsp;&nbsp;&nbsp;between 
                            <select name="startMonth">
                                <c:forEach begin="0" end="11" var="i">
                                    <option value="<c:out value="${i}"/>">
                                        <c:out value="${search.monthNames[i]}"/>
                                    </option>
                                </c:forEach>
                            </select>
                            <select name="startDay">
                                <c:forEach begin="1" end="31" var="i">
                                    <option><c:out value="${i}"/></option>
                                </c:forEach>
                            </select>
			    <select name = "startYear">
				<c:forEach begin="1900" end="2003" var="i">
                                    <option><c:out value="${i}"/></option>
                                </c:forEach>
                            </select>
                            
                        </p>
                        <p>                                
                            &nbsp;&nbsp;&nbsp;and
                            <select name="endMonth">
                                <c:forEach begin="0" end="11" var="i">
                                    <option value="<c:out value="${i}"/>">
                                        <c:out value="${search.monthNames[i]}"/>
                                    </option>
                                </c:forEach>
                            </select>
                            <select name="endDay">
                                <c:forEach begin="1" end="31" var="i">
                                    <option><c:out value="${i}"/></option>
                                </c:forEach>
                            </select>
			    <select name = "endYear">
				<c:forEach begin="1900" end="2003" var="i">
                                    <option><c:out value="${i}"/></option>
                                </c:forEach>
                            </select>
                            
                            <input type="submit" value="Search"/>
                        </p><p></p>
                        <p>
                            <input name="resultsPerPage" size="4" value="100"/>&nbsp;Results Per Page&nbsp;
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
    </body>
</html>

