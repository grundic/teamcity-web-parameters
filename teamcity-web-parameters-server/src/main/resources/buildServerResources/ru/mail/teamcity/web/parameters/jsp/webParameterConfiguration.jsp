<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props"  %>
<jsp:useBean id="context" scope="request" type="jetbrains.buildServer.controllers.parameters.ParameterEditContext"/>

<tr>
    <th><label for="url">URL:<l:star/></label></th>
    <td>
        <props:textProperty
                name="url"
                expandable="${true}"/>
    <span class="smallNote">
      URL of wev-service for reading results
    </span>
    </td>
</tr>