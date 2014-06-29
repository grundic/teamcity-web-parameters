<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<jsp:useBean id="context" scope="request" type="jetbrains.buildServer.controllers.parameters.ParameterRenderContext"/>
<jsp:useBean id="options" scope="request"
             type="java.util.List<ru.mail.teamcity.web.patameters.controller.WebParameterController.Entry>"/>

<c:set var="selectedKey" value="${context.parameter.value}"/>
<forms:select name="${context.id}" id="${context.id}" style="width:100%">
    <c:forEach var="option" items="${options}">
        <c:set var="selected" value="${option.key eq selectedKey}"/>
        <forms:option value="${option.key}" selected="${selected}">
            <c:out value="${option.value}"/>
        </forms:option>
    </c:forEach>
</forms:select>