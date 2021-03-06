<%--
  ~ Copyright 2016 Matt Richardson.
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~
  ~ Based on code graciously open sourced by JetBrains s.r.o
  ~ (http://svn.jetbrains.org/teamcity/plugins/url-build-trigger/trunk/url-build-trigger-server/resource/buildServerResources/editUrlBuildTrigger.jsp)
  ~
  ~ Original licence:
  ~
  ~ Copyright 2000-2013 JetBrains s.r.o.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<%@ include file="/include.jsp" %>
<%@ page import="com.mjrichardson.teamCity.buildTriggers.BuildTriggerConstants" %>
<%@ page import="com.mjrichardson.teamCity.buildTriggers.UpdateChecker" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="propertiesBean" type="jetbrains.buildServer.controllers.BasePropertiesBean" scope="request"/>

<div class="attentionComment clearfix" style="display:none" id="octopus-plugin-update-notice">
    A new plugin version (<%=UpdateChecker.getLatestVersion()%>) has been released. <a href="<%=UpdateChecker.getUpdateUrl()%>">Upgrade!</a>
</div>

<tr class="noBorder" >
    <td colspan="2">
        <em>Octopus Deployment Complete build trigger will add a build to the queue when a new successful deployment has completed.</em>
    </td>
</tr>

<tr class="noBorder" >
    <th nowrap="nowrap"><label for="<%=BuildTriggerConstants.OCTOPUS_URL%>">Octopus Url: <l:star/></label></th>
    <td nowrap="nowrap">
      <props:textProperty name="<%=BuildTriggerConstants.OCTOPUS_URL%>" className="longField" onchange="window.octopusBuildTrigger.reloadProjectList();" />
      <span class="smallNote">
          e.g. https://example.org
      </span>
       <span class="error" id="error_<%=BuildTriggerConstants.OCTOPUS_URL%>"></span>
    </td>
</tr>

<tr class="noBorder" >
    <th nowrap="nowrap"><label for="<%=BuildTriggerConstants.OCTOPUS_APIKEY%>">API Key: <l:star/></label></th>
    <td nowrap="nowrap">
       <props:textProperty name="<%=BuildTriggerConstants.OCTOPUS_APIKEY%>" className="longField" onchange="window.octopusBuildTrigger.reloadProjectList();" />
       <span class="error" id="error_<%=BuildTriggerConstants.OCTOPUS_APIKEY%>"></span>
    </td>
</tr>

<tr class="noBorder" >
    <th nowrap="nowrap">
        <label for="<%=BuildTriggerConstants.OCTOPUS_PROJECT_ID%>">Project: </label>
        <img src="/img/spinner.gif" id="octopus-build-trigger-busy" style="display:none;vertical-align:middle;float:right" title="loading..." />
    </th>
    <td nowrap="nowrap">
      <props:selectProperty name="<%=BuildTriggerConstants.OCTOPUS_PROJECT_ID%>">
        <props:option value="${propertiesBean.properties['octopus.build.trigger.project.url']}"></props:option>
      </props:selectProperty>
      <span class="error" id="error_<%=BuildTriggerConstants.OCTOPUS_PROJECT_ID%>"></span>
    </td>
</tr>

<tr class="noBorder" >
    <td colspan="2">
      <props:checkboxProperty name="<%=BuildTriggerConstants.OCTOPUS_TRIGGER_ONLY_ON_SUCCESSFUL_DEPLOYMENT%>" />
      <label for="<%=BuildTriggerConstants.OCTOPUS_TRIGGER_ONLY_ON_SUCCESSFUL_DEPLOYMENT%>">Trigger only on successful deployments</label>
    </td>
</tr>

<script type="text/javascript">
    var projectIdPropertyName = '<%=BuildTriggerConstants.OCTOPUS_PROJECT_ID%>';
    var octopusUrlPropertyName = '<%=BuildTriggerConstants.OCTOPUS_URL%>';
    var octopusApiKeyPropertyName = '<%=BuildTriggerConstants.OCTOPUS_APIKEY%>';
    var updateIsAvailable = '<%=UpdateChecker.updateIsAvailable%>';

    <jsp:include page="octopusBuildTrigger.js" />
</script>
