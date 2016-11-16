<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="context" scope="request" type="jetbrains.buildServer.controllers.parameters.ParameterEditContext"/>
<jsp:useBean id="parsers" scope="request"
             type="java.util.ArrayList<ru.mail.teamcity.web.parameters.parser.OptionParser>"/>
<jsp:useBean id="availableMethods" scope="request"
             type="java.util.List<ru.mail.teamcity.web.parameters.manager.RequestConfiguration.Method>"/>
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

<%--Advanced options start --%>

<tr>
    <th colspan="2">
        <div class="advancedSettingsToggle" id="advancedSettingsToggle" style="height: 0;">
            <i class="icon-wrench"></i><a href="javascript://" showdiscardchangesmessage="false">Show advanced
            request options</a>
        </div>
    </th>
</tr>

<tr class="advancedSetting advancedSettingHighlight hidden">
    <th><label for="timeout">Request timeout:</label></th>
    <td>
        <props:textProperty name="timeout" className="longField"/>
        <span class="smallNote">Request timeout in milliseconds</span>
    </td>
</tr>

<tr class="advancedSetting advancedSettingHighlight hidden">
    <th><label>Request method:</label></th>
    <td>
        <props:selectProperty name="method">
            <c:forEach var="method" items="${availableMethods}">
                <props:option value="${method.name()}">${method.name()}</props:option>
            </c:forEach>
        </props:selectProperty>
        <span class="smallNote">Select request method for getting options.</span>
    </td>
</tr>

<tr id="payloadRow" class="advancedSetting advancedSettingHighlight hidden">
    <th><label>Payload:</label></th>
    <td>
        <div class="completionIconWrapper">
            <props:multilineProperty name="payload" linkTitle="POST Data"
                                     cols="60" rows="3" className="longField autocompletionProperty"
                                     expanded="false"/>
        </div>
        <span class="smallNote">Data payload for sending to the server.</span>
    </td>
</tr>

<tr class="advancedSetting advancedSettingHighlight hidden">
    <th><label>Headers:</label></th>
    <td>
        <div class="completionIconWrapper">
            <props:multilineProperty name="headers" linkTitle="Request headers"
                                     cols="60" rows="3" className="longField autocompletionProperty"
                                     expanded="false"/>
        </div>
        <span class="smallNote">New line separated list of headers.<br>Each line should be in format &lt;name&gt;:&lt;value&gt;.</span>
    </td>
</tr>

<tr class="advancedSetting advancedSettingHighlight hidden">
    <th rowspan="2" style="vertical-align: middle"><label>Basic Authorization:</label></th>
    <td>
        <div class="completionIconWrapper">
            <props:textProperty name="username" className="longField autocompletionProperty"/>
            <div class="completionIconWrapper">
                <span class="smallNote">Username</span>
    </td>
</tr>

<tr class="advancedSetting advancedSettingHighlight hidden">
    <td>
        <div class="completionIconWrapper">
            <props:textProperty name="password" className="longField autocompletionProperty"/>
            <div class="completionIconWrapper">
        <span class="smallNote">Password (unfortunately it doesn't work with masked input)<br>
            For now you can use another %PARAMETER% as a reference, which could be 'Password' type.
        </span>
    </td>
</tr>

<%--Advanced options end --%>

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
    <th><label>JSON transformation:</label></th>
    <td>
        <div class="completionIconWrapper">
            <props:multilineProperty name="transform" linkTitle="JSON transformation"
                                     cols="60" rows="3" className="longField autocompletionProperty"
                                     expanded="false"/>
        </div>
        <span class="smallNote">Enter here required json transformation that should be applied.
            You can read official documentation for <a target="_blank" href="https://github.com/bazaarvoice/jolt">jolt</a>
            or test it on the <a target="_blank" href="http://jolt-demo.appspot.com">demo</a> site.</span>
    </td>
</tr>

<tr>
    <th><label for="multiple">Allow multiple:<l:star/></label></th>
    <td>
        <props:checkboxProperty name="multiple"/>
        <label for="multiple">Allow to select multiple items.</label>
    </td>
</tr>

<tr id="multipleSeparator">
    <th><label for="multipleSeparator">Value separator:</label></th>
    <td>
        <props:textProperty name="multipleSeparator" className="longField"/>
        <span class="smallNote">Specify multiple selected items separator. Leave blank to use ','.</span>
    </td>
</tr>

<tr>
    <th><label for="tagSupport">Tag support:<l:star/></label></th>
    <td>
        <props:checkboxProperty name="tagSupport"/>
        <label for="tagSupport">Allow to create new items, that aren't in the initial list of options.</label>
    </td>
</tr>

<tr>
    <th><label for="enableEditOnError">Enable edit on error:<l:star/></label></th>
    <td>
        <props:checkboxProperty name="enableEditOnError"/>
        <label for="enableEditOnError">Enable manual edit in case of web service is not available.</label>
    </td>
</tr>


<script type="text/javascript">

    var webParameterConfig = {
        init: function () {
            $('multiple').on('change', this.toggleMultipleSeparator);
            this.toggleMultipleSeparator();

            $j('#method').on('change', this.updatePayloadRowVisibility);
            this.updatePayloadRowVisibility();
            this.expandPayload();

            this.expandHeaders();
            this.expandTransformation();

            this.addAutocomplete();
            this.advancedSettingsToggle();

            BS.MultilineProperties.updateVisible();
        },

        toggleMultipleSeparator: function () {
            if ($('multiple').checked) {
                BS.Util.show('multipleSeparator');
            } else {
                BS.Util.hide('multipleSeparator');
            }
        },

        gup: function (name, url) {
            if (!url) url = location.href;
            name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
            var regexS = "[\\?&]" + name + "=([^&#]*)";
            var regex = new RegExp(regexS);
            var results = regex.exec(url);
            return results == null ? null : results[1];
        },

        addAutocomplete: function () {
            var idQueryParam = this.gup('id');
            BS.AvailableParams.attachPopups('settingsId=' + idQueryParam, 'autocompletionProperty');
        },

        advancedSettingsToggle: function () {
            var that = this;
            $j('#advancedSettingsToggle').click(function () {
                $j(".advancedSetting").toggle();
                that.updatePayloadRowVisibility();
            });
            BS.MultilineProperties.updateVisible();
        },

        updatePayloadRowVisibility: function () {
            var visible = ($j('#method').is(":visible")) && ($j('#method').val() === 'POST');
            visible ? $j("#payloadRow").show() : $j("#payloadRow").hide();
            BS.MultilineProperties.updateVisible();
        },

        expandPayload: function () {
            if ($j('#payload').val()) {
                BS.MultilineProperties.setVisible('payload', true);
            }
        },

        expandHeaders: function () {
            if ($j('#headers').val()) {
                BS.MultilineProperties.setVisible('headers', true);
            }
        },
        expandTransformation: function () {
            if ($j('#transform').val()) {
                BS.MultilineProperties.setVisible('transform', true);
            }
        }
    };

    webParameterConfig.init();
</script>