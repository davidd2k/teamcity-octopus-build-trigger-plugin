package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.Deployments;
import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.DeploymentsProvider;
import com.mjrichardson.teamCity.buildTriggers.DeploymentComplete.DeploymentsProviderException;
import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusApiKeyException;
import com.mjrichardson.teamCity.buildTriggers.InvalidOctopusUrlException;
import com.mjrichardson.teamCity.buildTriggers.ProjectNotFoundException;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

public class FakeDeploymentsProviderThatThrowsExceptions implements DeploymentsProvider {
    public FakeDeploymentsProviderThatThrowsExceptions() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    }

    @Override
    public Deployments getDeployments(String octopusProject, Deployments oldDeployments) throws DeploymentsProviderException, ProjectNotFoundException, InvalidOctopusApiKeyException, InvalidOctopusUrlException, ParseException {
        throw new ProjectNotFoundException(octopusProject);
    }
}
