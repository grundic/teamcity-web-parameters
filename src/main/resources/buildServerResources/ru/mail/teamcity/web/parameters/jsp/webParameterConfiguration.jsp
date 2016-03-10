<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="context" scope="request" type="jetbrains.buildServer.controllers.parameters.ParameterEditContext"/>
<jsp:useBean id="parsers" scope="request"
             type="java.util.ArrayList<ru.mail.teamcity.web.parameters.parser.OptionParser>"/>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<tr>
    <th><label for="url">URL:<l:star/></label></th>
    <td>
        <props:textProperty
                name="url"
                expandable="${true}"/>
    <span class="smallNote">
      URL of web service for reading results
    </span>
    </td>
</tr>

<tr>
    <th><label>Response format:<l:star/></label></th>
    <td>
        <c:forEach var="parser" items="${parsers}">
            <props:radioButtonProperty name="format" id="${parser.id}" value="${parser.id}"/>
            <label for="${parser.id}">${parser.id}</label>
            <br/>
        </c:forEach>
    </td>
</tr>

<tr>
    <th><label for="enableEditOnError">Enable edit on error:<l:star/></label></th>
    <td>
        <props:checkboxProperty name="enableEditOnError"/>
    <span class="smallNote">
      Enable manual edit in case of web service is not available
    </span>
    </td>
</tr>
