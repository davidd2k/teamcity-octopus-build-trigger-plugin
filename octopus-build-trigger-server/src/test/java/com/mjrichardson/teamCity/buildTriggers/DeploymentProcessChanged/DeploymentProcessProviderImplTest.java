package com.mjrichardson.teamCity.buildTriggers.DeploymentProcessChanged;

import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeAnalyticsTracker;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeCacheManager;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeContentProviderFactory;
import com.mjrichardson.teamCity.buildTriggers.Fakes.FakeMetricRegistry;
import com.mjrichardson.teamCity.buildTriggers.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Test
public class DeploymentProcessProviderImplTest {
    final String octopusUrl = "http://baseUrl";
    final String octopusApiKey = "API-key";
    final String realOctopusUrl = "http://windows10vm.local/";
    final String realOctopusApiKey = "API-H3CUOOWJ1XMWBUHSMASYIPAW20";

    static String ProjectWithNoProcess = "Projects-181";
    static String ProjectWithLatestDeploymentSuccessful = "Projects-24";
    static String ProjectThatDoesNotExist = "Projects-00";

    @Test(groups = {"needs-real-server"})
    public void get_deployment_process_version_from_real_server() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ProjectNotFoundException, DeploymentProcessProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new HttpContentProviderFactory(realOctopusUrl, realOctopusApiKey, OctopusBuildTriggerUtil.getConnectionTimeoutInMilliseconds(), new FakeCacheManager(), new FakeMetricRegistry());
        DeploymentProcessProviderImpl deploymentProcessProviderImpl = new DeploymentProcessProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        String newVersion = deploymentProcessProviderImpl.getDeploymentProcessVersion(ProjectWithLatestDeploymentSuccessful);
        Assert.assertNotNull(newVersion);
    }

    public void get_deployment_process_version() throws ProjectNotFoundException, DeploymentProcessProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        DeploymentProcessProviderImpl deploymentProcessProviderImpl = new DeploymentProcessProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        String newVersion = deploymentProcessProviderImpl.getDeploymentProcessVersion(ProjectWithLatestDeploymentSuccessful);
        Assert.assertEquals(newVersion, "2");
    }

    public void get_deployment_process_version_for_project_with_no_process() throws ProjectNotFoundException, DeploymentProcessProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        DeploymentProcessProviderImpl deploymentProcessProviderImpl = new DeploymentProcessProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());
        String newVersion = deploymentProcessProviderImpl.getDeploymentProcessVersion(ProjectWithNoProcess);
        Assert.assertEquals(newVersion, "0");
    }

    @Test(expectedExceptions = ProjectNotFoundException.class)
    public void get_deployment_process_version_with_invalid_project() throws ProjectNotFoundException, DeploymentProcessProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, octopusApiKey);
        DeploymentProcessProviderImpl deploymentProcessProviderImpl = new DeploymentProcessProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());

        deploymentProcessProviderImpl.getDeploymentProcessVersion(ProjectThatDoesNotExist);
    }

    @Test(expectedExceptions = InvalidOctopusUrlException.class)
    public void get_deployment_process_version_with_octopus_url_with_invalid_host() throws ProjectNotFoundException, DeploymentProcessProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory("http://octopus.example.com", octopusApiKey);
        DeploymentProcessProviderImpl deploymentProcessProviderImpl = new DeploymentProcessProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());

        deploymentProcessProviderImpl.getDeploymentProcessVersion(ProjectWithLatestDeploymentSuccessful);
    }

    @Test(expectedExceptions = InvalidOctopusUrlException.class)
    public void get_deployment_process_version_with_octopus_url_with_invalid_path() throws ProjectNotFoundException, DeploymentProcessProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl + "/not-an-octopus-instance", octopusApiKey);
        DeploymentProcessProviderImpl deploymentProcessProviderImpl = new DeploymentProcessProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());

        deploymentProcessProviderImpl.getDeploymentProcessVersion(ProjectWithLatestDeploymentSuccessful);
    }

    @Test(expectedExceptions = InvalidOctopusApiKeyException.class)
    public void get_deployment_process_version_with_invalid_octopus_api_key() throws ProjectNotFoundException, DeploymentProcessProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(octopusUrl, "invalid-api-key");
        DeploymentProcessProviderImpl deploymentProcessProviderImpl = new DeploymentProcessProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());

        deploymentProcessProviderImpl.getDeploymentProcessVersion(ProjectWithLatestDeploymentSuccessful);
    }

    @Test(expectedExceptions = DeploymentProcessProviderException.class)
    public void rethrows_throwable_exceptions_as_deployment_process_provider_exception() throws ProjectNotFoundException, DeploymentProcessProviderException, InvalidOctopusApiKeyException, InvalidOctopusUrlException {
        HttpContentProviderFactory contentProviderFactory = new FakeContentProviderFactory(new OutOfMemoryError());
        DeploymentProcessProviderImpl deploymentProcessProviderImpl = new DeploymentProcessProviderImpl(contentProviderFactory, new FakeAnalyticsTracker());

        deploymentProcessProviderImpl.getDeploymentProcessVersion(ProjectWithLatestDeploymentSuccessful);
    }
}