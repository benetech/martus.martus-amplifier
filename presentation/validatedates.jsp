<%@ page contentType="text/html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<jsp:useBean id="startDateValidator" class="org.martus.amplifier.presentation.search.DateValidator">
    <jsp:setProperty name="startDateValidator" property="day" param="startDay"/>
    <jsp:setProperty name="startDateValidator" property="month" param="startMonth"/>
    <jsp:setProperty name="startDateValidator" property="year" param="startYear"/>
</jsp:useBean>

<jsp:useBean id="endDateValidator" class="org.martus.amplifier.presentation.search.DateValidator">
    <jsp:setProperty name="endDateValidator" property="day" param="endDay"/>
    <jsp:setProperty name="endDateValidator" property="month" param="endMonth"/>
    <jsp:setProperty name="endDateValidator" property="year" param="endYear"/>
</jsp:useBean>

<fmt:formatDate 
    value="${startDateValidator.date}" 
    dateStyle="short" 
    var="startDateString"/>

<fmt:formatDate 
    value="${endDateValidator.date}" 
    dateStyle="short" 
    var="endDateString"/>
    
<c:redirect url="searchresults.jsp">
    <c:param name="field" value="${param.field}" />
    <c:param name="resultsPerPage" value="${param.resultsPerPage}" />
    <c:param name="startDateString" value="${startDateString}" />
    <c:param name="endDateString" value="${endDateString}" />
</c:redirect>
