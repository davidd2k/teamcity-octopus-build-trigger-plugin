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

package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil;
import com.mjrichardson.teamCity.buildTriggers.OctopusConnectivityChecker;
import com.mjrichardson.teamCity.buildTriggers.OctopusConnectivityCheckerFactory;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

class DeploymentCompleteTriggerPropertiesProcessor implements PropertiesProcessor {

    private final OctopusConnectivityCheckerFactory octopusConnectivityCheckerFactory;

    public DeploymentCompleteTriggerPropertiesProcessor() {
        this(new OctopusConnectivityCheckerFactory());
    }

    public DeploymentCompleteTriggerPropertiesProcessor(OctopusConnectivityCheckerFactory octopusConnectivityCheckerFactory) {
        this.octopusConnectivityCheckerFactory = octopusConnectivityCheckerFactory;
    }

    public Collection<InvalidProperty> process(Map<String, String> properties) {
        final ArrayList<InvalidProperty> invalidProps = new ArrayList<>();

        final String url = properties.get(OctopusBuildTriggerUtil.OCTOPUS_URL);
        if (StringUtil.isEmptyOrSpaces(url)) {
            invalidProps.add(new InvalidProperty(OctopusBuildTriggerUtil.OCTOPUS_URL, "URL must be specified"));
        }

        final String apiKey = properties.get(OctopusBuildTriggerUtil.OCTOPUS_APIKEY);
        if (StringUtil.isEmptyOrSpaces(apiKey)) {
            invalidProps.add(new InvalidProperty(OctopusBuildTriggerUtil.OCTOPUS_APIKEY, "API Key must be specified"));
        }

        if (invalidProps.size() == 0) {
            checkConnectivity(properties, invalidProps, url, apiKey);
        }
        return invalidProps;
    }

    private void checkConnectivity(Map<String, String> properties, ArrayList<InvalidProperty> invalidProps, String url, String apiKey) {
        try {
            final Integer connectionTimeout = OctopusBuildTriggerUtil.DEFAULT_CONNECTION_TIMEOUT;//triggerParameters.getConnectionTimeout(); //todo:fix
            final OctopusConnectivityChecker connectivityChecker = octopusConnectivityCheckerFactory.create(url, apiKey, connectionTimeout);

            final String err = connectivityChecker.checkOctopusConnectivity();
            if (StringUtil.isNotEmpty(err)) {
                invalidProps.add(new InvalidProperty(OctopusBuildTriggerUtil.OCTOPUS_URL, err));
            }

            final String project = properties.get(OctopusBuildTriggerUtil.OCTOPUS_PROJECT_ID);
            if (StringUtil.isEmptyOrSpaces(project)) {
                invalidProps.add(new InvalidProperty(OctopusBuildTriggerUtil.OCTOPUS_PROJECT_ID, "Project must be specified"));
            }
        } catch (Exception e) {
            invalidProps.add(new InvalidProperty(OctopusBuildTriggerUtil.OCTOPUS_URL, e.getMessage()));
        }
    }
}
