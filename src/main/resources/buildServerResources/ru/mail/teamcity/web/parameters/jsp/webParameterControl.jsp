<%@ include file="/include.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<jsp:useBean id="context" scope="request" type="jetbrains.buildServer.controllers.parameters.ParameterRenderContext"/>
<jsp:useBean id="options" scope="request" type="ru.mail.teamcity.web.parameters.data.Options"/>
<jsp:useBean id="enableEditOnError" scope="request" type="java.lang.Boolean"/>
<jsp:useBean id="errors" scope="request" type="java.util.Map<java.lang.String, java.lang.String>"/>

<link rel="stylesheet" type="text/css"
      href="${teamcityPluginResourcesPath}/ru/mail/teamcity/web/parameters/css/webParameter.css">
<link rel="stylesheet" type="text/css"
      href="${teamcityPluginResourcesPath}/ru/mail/teamcity/web/parameters/css/select2.min.css">

<script type="text/javascript">
    function addScript( src,callback) {
        var s = document.createElement( 'script' );
        s.setAttribute( 'src', src );
        s.onload=callback;
        document.body.appendChild( s );
    }

    function addOptionImage(opt){
        if (!opt.id) {
            return opt.text;
        }
        var optimage = $j(opt.element).data('image');
        if(!optimage){
            return opt.text;
        } else {
            var $opt = $j(
                    '<span><img src="' + optimage + '" style="height:16px;" /> ' + $j(opt.element).text() + '</span>'
            );
            return $opt;
        }
    }

    $j(document).ready(function ($) {
        addScript("${teamcityPluginResourcesPath}/ru/mail/teamcity/web/parameters/js/select2.min.js", function(){
            $j("#${context.id}").select2({
                templateResult: addOptionImage,
                templateSelection: addOptionImage
            });
        });
    });
</script>

<c:choose>
    <c:when test="${empty errors}">
        <c:set var="selectedKey" value="${context.parameter.value}"/>
        <select name="${context.id}" id="${context.id}" style="width:100%;">
            <c:forEach var="option" items="${options.options}">
                <option
                        value="${option.value}"
                        <c:if test="${option.value eq selectedKey}">selected</c:if>
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