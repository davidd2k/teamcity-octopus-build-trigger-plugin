package com.mjrichardson.teamCity.buildTriggers.DeploymentProcessChanged;

import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

@Test
public class DeploymentProcessChangedSpecCheckResultTest {
    public void create_empty_result_returns_an_object_with_no_updates_nor_errors() {
        UUID correlationId = UUID.randomUUID();
        DeploymentProcessChangedSpecCheckResult result = DeploymentProcessChangedSpecCheckResult.createEmptyResult(correlationId);
        Assert.assertFalse(result.updatesDetected());
        Assert.assertFalse(result.hasCheckErrors());
    }

    public void create_updated_result_returns_an_object_with_updates_but_no_errors() {
        String version = "18";
        String projectId = "the-project-id";
        DeploymentProcessChangedSpec DeploymentProcessChangedSpec = new DeploymentProcessChangedSpec("the-url", version, projectId);
        UUID correlationId = UUID.randomUUID();
        DeploymentProcessChangedSpecCheckResult result = DeploymentProcessChangedSpecCheckResult.createUpdatedResult(DeploymentProcessChangedSpec, correlationId);
        Assert.assertFalse(result.hasCheckErrors());
        Assert.assertTrue(result.updatesDetected());
        DeploymentProcessChangedSpec[] array = result.getUpdated().toArray(new DeploymentProcessChangedSpec[1]);
        Assert.assertEquals(array.length, 1);
        Assert.assertEquals(array[0], DeploymentProcessChangedSpec);
    }

    public void create_throwable_result_returns_an_object_with_errors_but_no_updates() {
        Throwable throwable = new OutOfMemoryError("out of memory exception");
        UUID correlationId = UUID.randomUUID();
        DeploymentProcessChangedSpecCheckResult result = DeploymentProcessChangedSpecCheckResult.createThrowableResult(throwable, correlationId);
        Assert.assertTrue(result.hasCheckErrors());
        Assert.assertFalse(result.updatesDetected());
        Assert.assertEquals(result.getGeneralError(), throwable);
        Assert.assertEquals(result.getGeneralError().getMessage(), "out of memory exception");
    }

    public void create_error_result_returns_an_object_with_errors_but_no_updates() {
        String error = "an error";
        UUID correlationId = UUID.randomUUID();
        DeploymentProcessChangedSpecCheckResult result = DeploymentProcessChangedSpecCheckResult.createErrorResult(error, correlationId);
        Assert.assertTrue(result.hasCheckErrors());
        Assert.assertFalse(result.updatesDetected());
        Assert.assertEquals(result.getGeneralError().getClass(), BuildTriggerException.class);
        Assert.assertEquals(result.getGeneralError().getMessage(), "an error");
    }
}
