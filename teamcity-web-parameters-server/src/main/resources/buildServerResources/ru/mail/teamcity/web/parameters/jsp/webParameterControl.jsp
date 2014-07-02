<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<jsp:useBean id="context" scope="request" type="jetbrains.buildServer.controllers.parameters.ParameterRenderContext"/>
<jsp:useBean id="options" scope="request" type="ru.mail.teamcity.web.parameters.data.Options"/>
<jsp:useBean id="errors" scope="request" type="java.util.Map<java.lang.String, java.lang.String>"/>

<c:choose>
    <c:when test="${empty errors}">
        <c:set var="selectedKey" value="${context.parameter.value}"/>
        <forms:select name="${context.id}" id="${context.id}" style="width:100%">
            <c:forEach var="option" items="${options.options}">
                <c:set var="selected" value="${option.key eq selectedKey}"/>
                <forms:option value="${option.key}" selected="${selected}">
                    <c:out value="${option.value}"/>
                </forms:option>
            </c:forEach>
        </forms:select>
    </c:when>

    <c:otherwise>
        <div style="background-color: #F50000;color: #D9D9D9; width:100%; border-radius: 15px; border: 2px solid;">
            <c:forEach var="error" items="${errors}">
                <h4>${error.key}:</h4>
                <h4>${error.value}</h4>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>