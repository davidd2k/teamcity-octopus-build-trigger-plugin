package com.mjrichardson.teamCity.buildTriggers.MachineAdded;

import com.mjrichardson.teamCity.buildTriggers.AnalyticsTracker;
import com.mjrichardson.teamCity.buildTriggers.CustomAsyncBuildTrigger;
import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import jetbrains.buildServer.buildTriggers.async.AsyncTriggerParameters;
import jetbrains.buildServer.buildTriggers.async.CheckJob;
import jetbrains.buildServer.buildTriggers.async.CheckJobCreationException;
import jetbrains.buildServer.buildTriggers.async.CheckResult;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.mjrichardson.teamCity.buildTriggers.OctopusBuildTriggerUtil.*;

class MachineAddedAsyncBuildTrigger implements CustomAsyncBuildTrigger<MachineAddedSpec> {
    private final String displayName;
    private final int pollIntervalInSeconds;
    private final AnalyticsTracker analyticsTracker;

    public MachineAddedAsyncBuildTrigger(String displayName, int pollIntervalInSeconds, AnalyticsTracker analyticsTracker) {
        this.displayName = displayName;
        this.pollIntervalInSeconds = pollIntervalInSeconds;
        this.analyticsTracker = analyticsTracker;
    }

    @NotNull
    public BuildTriggerException makeTriggerException(@NotNull Throwable throwable) {
        throw new BuildTriggerException(displayName + " failed with error: " + throwable.getMessage(), throwable);
    }

    @NotNull
    public String getRequestorString(@NotNull MachineAddedSpec machineAddedSpec) {
        return machineAddedSpec.getRequestorString();
    }

    public int getPollInterval(@NotNull AsyncTriggerParameters parameters) {
        return pollIntervalInSeconds;
    }

    @NotNull
    public CheckJob<MachineAddedSpec> createJob(@NotNull final AsyncTriggerParameters asyncTriggerParameters) throws CheckJobCreationException {
        return new MachineAddedCheckJob(displayName,
                asyncTriggerParameters.getBuildType().toString(),
                asyncTriggerParameters.getCustomDataStorage(),
                asyncTriggerParameters.getTriggerDescriptor().getProperties(),
                analyticsTracker);
    }

    @NotNull
    public CheckResult<MachineAddedSpec> createCrashOnSubmitResult(@NotNull Throwable throwable) {
        return MachineAddedSpecCheckResult.createThrowableResult(throwable);
    }

    public String describeTrigger(BuildTriggerDescriptor buildTriggerDescriptor) {
        Map<String, String> properties = buildTriggerDescriptor.getProperties();
        return String.format("Wait for a new machine to be added to server %s.",
                properties.get(OCTOPUS_URL));
    }

    @Override
    public Map<String, String> getProperties(MachineAddedSpec machineAddedSpec) {
        HashMap hashMap = new HashMap();
        hashMap.put(BUILD_PROPERTY_MACHINE_NAME, machineAddedSpec.machineName);
        hashMap.put(BUILD_PROPERTY_MACHINE_ID, machineAddedSpec.machineId);
        hashMap.put(BUILD_PROPERTY_MACHINE_ENVIRONMENT_IDS, machineAddedSpec.environmentIds);
        hashMap.put(BUILD_PROPERTY_MACHINE_ROLE_IDS, machineAddedSpec.roleIds);
        return hashMap;
    }
}
