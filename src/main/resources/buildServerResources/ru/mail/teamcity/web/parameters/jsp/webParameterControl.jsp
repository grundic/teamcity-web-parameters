<%@ include file="/include.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<jsp:useBean id="context" scope="request" type="jetbrains.buildServer.controllers.parameters.ParameterRenderContext"/>
<jsp:useBean id="options" scope="request" type="ru.mail.teamcity.web.parameters.data.Options"/>
<jsp:useBean id="values" scope="request" type="java.util.Collection"/>
<jsp:useBean id="multiple" scope="request" type="java.lang.Boolean"/>
<jsp:useBean id="tagSupport" scope="request" type="java.lang.Boolean"/>
<jsp:useBean id="enableEditOnError" scope="request" type="java.lang.Boolean"/>
<jsp:useBean id="errors" scope="request" type="java.util.Map<java.lang.String, java.lang.String>"/>

<link rel="stylesheet" type="text/css"
      href="${teamcityPluginResourcesPath}/ru/mail/teamcity/web/parameters/css/webParameter.css">
<link rel="stylesheet" type="text/css"
      href="${teamcityPluginResourcesPath}/ru/mail/teamcity/web/parameters/css/select2.min.css">
<link rel="stylesheet" type="text/css"
      href="${teamcityPluginResourcesPath}/ru/mail/teamcity/web/parameters/css/select2-bootstrap.min.css">

<script type="text/javascript">
    function addScript(src, callback) {
        var scriptElement = document.createElement('script');
        scriptElement.setAttribute('src', src);
        scriptElement.onload = callback;
        document.body.appendChild(scriptElement);
    }

    $j(document).ready(function ($) {
        addScript("${teamcityPluginResourcesPath}/ru/mail/teamcity/web/parameters/js/select2.full.min.js", function () {
            addScript("${teamcityPluginResourcesPath}/ru/mail/teamcity/web/parameters/js/webParametersControl.js", function () {
                WebParametersControl.init("${context.id}");
            });
        });
    });
</script>

<c:choose>
    <c:when test="${empty errors}">
        <c:set var="selectedKey" value="${context.parameter.value}"/>

        <select
                name="${context.id}" id="${context.id}" style="width:100%;"
                <c:if test="${multiple}">multiple="multiple"</c:if>
                <c:if test="${tagSupport}">data-tags="true"</c:if>
        >
            <c:forEach var="option" items="${options.options}">
                <option value="${option.value}"
                        <c:if test="${values.contains(option.value)}">selected</c:if>
                        <c:if test="${not option.enabled}">disabled</c:if>
                        <c:if test="${not empty option.image}">data-image="${option.image}"</c:if>
                >
                    <c:out value="${option.key}"/>
                </option>
            </c:forEach>
        </select>
    </c:when>

    <c:otherwise>
        <div class="web-param-error">
            <c:forEach var="error" items="${errors}">
                <h3>${error.key}</h3>
                <span>${error.value}</span>
            </c:forEach>
        </div>

        <c:if test="${enableEditOnError == true}">
            <div>
                <label for="${context.id}"><c:out value="Manual value input"/></label>
                <forms:textField
                        name="${context.id}"
                        id="${context.id}"
                        style="width: 100%;"
                        value=""
                        noAutoComplete="true"
                        className="buildTypeParams"
                        expandable="true"
                />
            </div>
        </c:if>

    </c:otherwise>
</c:choose>