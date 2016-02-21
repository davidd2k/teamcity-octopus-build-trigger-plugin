/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mjrichardson.teamCity.buildTriggers;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.async.AsyncTriggerParameters;
import jetbrains.buildServer.buildTriggers.async.CheckJob;
import jetbrains.buildServer.buildTriggers.async.CheckResult;
import jetbrains.buildServer.serverSide.CustomDataStorage;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.*;

//todo: this needs tests
class DeploymentCompleteCheckJob implements CheckJob<Spec> {
  @NotNull
  private static final Logger LOG = Logger.getInstance(OctopusBuildTrigger.class.getName());

  private final AsyncTriggerParameters asyncTriggerParameters;
  private final String displayName;

  public DeploymentCompleteCheckJob(AsyncTriggerParameters asyncTriggerParameters, String displayName) {
    this.asyncTriggerParameters = asyncTriggerParameters;
    this.displayName = displayName;
  }

  @NotNull
  CheckResult<Spec> getCheckResult(String octopusUrl, String octopusApiKey, String octopusProject,
                                   Boolean triggerOnlyOnSuccessfulDeployment, CustomDataStorage dataStorage) {
    LOG.debug("Checking for new deployments for project " + octopusProject + " on server " + octopusUrl);
    final String dataStorageKey = (octopusUrl + "|" + octopusProject).toLowerCase();

    try {
      final String oldStoredData = dataStorage.getValue(dataStorageKey);
      final Deployments oldDeployments = new Deployments(oldStoredData);
      final Integer connectionTimeout = OctopusBuildTriggerUtil.DEFAULT_CONNECTION_TIMEOUT;//triggerParameters.getConnectionTimeout(); //todo:fix

      OctopusDeploymentsProvider provider = new OctopusDeploymentsProvider(octopusUrl, octopusApiKey, connectionTimeout, LOG);
      final Deployments newDeployments = provider.getDeployments(octopusProject, oldDeployments);

      //only store that one deployment to one environment has happened here, not multiple environment.
      //otherwise, we could inadvertently miss deployments
      final Deployments newStoredData = newDeployments.trimToOnlyHaveMaximumOneChangedEnvironment(oldDeployments, triggerOnlyOnSuccessfulDeployment);

      if (!newDeployments.equals(oldDeployments)) {
        dataStorage.putValue(dataStorageKey, newStoredData.toString());

        //todo: see if its possible to to check the property on the context that says whether its new?
        //http://javadoc.jetbrains.net/teamcity/openapi/current/jetbrains/buildServer/buildTriggers/PolledTriggerContext.html#getPreviousCallTime()
        //do not trigger build after first adding trigger (oldDeployments == null)
        if (oldDeployments.isEmpty()) {
          LOG.debug("No previous data for server " + octopusUrl + ", project " + octopusProject + ": null" + " -> " + newStoredData);
          return SpecCheckResult.createEmptyResult();
        }

        final Deployment deployment = newStoredData.getChangedDeployment(oldDeployments);
        if (triggerOnlyOnSuccessfulDeployment && !deployment.isSuccessful()) {
          LOG.debug("New deployments found, but they weren't successful, and we are only triggering on successful builds. Server " + octopusUrl + ", project " + octopusProject + ": null" + " -> " + newStoredData);
          return SpecCheckResult.createEmptyResult();
        }

        LOG.info("New deployments on " + octopusUrl + " for project " + octopusProject + ": " + oldStoredData + " -> " + newStoredData);
        final Spec spec = new Spec(octopusUrl, octopusProject, deployment.environmentId, deployment.isSuccessful());
        //todo: investigate passing multiple bits to createUpdatedResult()
        return SpecCheckResult.createUpdatedResult(spec);
      }

      LOG.info("No new deployments on " + octopusUrl + " for project " + octopusProject + ": " + oldStoredData + " -> " + newStoredData);
      return SpecCheckResult.createEmptyResult();

    } catch (Exception e) {
      final Spec spec = new Spec(octopusUrl, octopusProject);
      return SpecCheckResult.createThrowableResult(spec, e);
    }
  }

  @NotNull
  public CheckResult<Spec> perform() {
    final Map<String, String> props = asyncTriggerParameters.getTriggerDescriptor().getProperties();

    final String octopusUrl = props.get(OCTOPUS_URL);
    if (StringUtil.isEmptyOrSpaces(octopusUrl)) {
      return SpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty url) in build configuration %s",
        displayName, asyncTriggerParameters.getBuildType()));
    }

    final String octopusApiKey = props.get(OCTOPUS_APIKEY);
    if (StringUtil.isEmptyOrSpaces(octopusApiKey)) {
      return SpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty api key) in build configuration %s",
        displayName, asyncTriggerParameters.getBuildType()));
    }

    final String octopusProject = props.get(OCTOPUS_PROJECT_ID);
    if (StringUtil.isEmptyOrSpaces(octopusProject)) {
      return SpecCheckResult.createErrorResult(String.format("%s settings are invalid (empty project) in build configuration %s",
        displayName, asyncTriggerParameters.getBuildType()));
    }

    final Boolean triggerOnlyOnSuccessfulDeployment = Boolean.parseBoolean(props.get(OCTOPUS_TRIGGER_ONLY_ON_SUCCESSFUL_DEPLOYMENT));

    return getCheckResult(octopusUrl, octopusApiKey, octopusProject,
      triggerOnlyOnSuccessfulDeployment, asyncTriggerParameters.getCustomDataStorage());
  }

  public boolean allowSchedule(@NotNull BuildTriggerDescriptor buildTriggerDescriptor) {
    return false;
  }
}