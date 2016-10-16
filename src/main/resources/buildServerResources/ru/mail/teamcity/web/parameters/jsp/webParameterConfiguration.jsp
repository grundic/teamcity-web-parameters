<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="context" scope="request" type="jetbrains.buildServer.controllers.parameters.ParameterEditContext"/>
<jsp:useBean id="parsers" scope="request"
             type="java.util.ArrayList<ru.mail.teamcity.web.parameters.parser.OptionParser>"/>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>


<tr>
    <th><label for="url">URL:<l:star/></label></th>
    <td class="paramValue noBorder">
        <div class="completionIconWrapper">
            <props:textProperty name="url" className="longField autocompletionProperty"/>
        </div>
        <span class="smallNote">URL of web service for reading results</span>
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
    <th><label for="multiple">Allow multiple:<l:star/></label></th>
    <td>
        <props:checkboxProperty name="multiple"/>
        <label for="multiple">Allow to select multiple items</label>
    </td>
</tr>

<tr id="multipleSeparator">
    <th><label for="valueSeparator">Value separator:</label></th>
    <td>
        <props:textProperty name="valueSeparator" className="longField"/>
        <span class="smallNote">Specify multiple selected items separator. Leave blank to use ','.</span>
    </td>
</tr>

<tr>
    <th><label for="enableEditOnError">Enable edit on error:<l:star/></label></th>
    <td>
        <props:checkboxProperty name="enableEditOnError"/>
        <label for="enableEditOnError">Enable manual edit in case of web service is not available</label>
    </td>
</tr>


<script type="text/javascript">
    (function () {
        var update = function () {
            if ($('multiple').checked) {
                BS.Util.show('multipleSeparator');
            } else {
                BS.Util.hide('multipleSeparator');
            }
        };

        var gup = function (name, url) {
            if (!url) url = location.href;
            name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
            var regexS = "[\\?&]" + name + "=([^&#]*)";
            var regex = new RegExp(regexS);
            var results = regex.exec(url);
            return results == null ? null : results[1];
        };

        var addAutocomplete = function () {
            var idQueryParam = gup('id');
            BS.AvailableParams.attachPopups('settingsId=' + idQueryParam, 'autocompletionProperty');
        };


        $('multiple').on('change', update);
        update();
        addAutocomplete();
    })();
</script>