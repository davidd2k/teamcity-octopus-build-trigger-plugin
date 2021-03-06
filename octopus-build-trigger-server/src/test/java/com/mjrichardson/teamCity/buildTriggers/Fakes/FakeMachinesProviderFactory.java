package com.mjrichardson.teamCity.buildTriggers.Fakes;

import com.mjrichardson.teamCity.buildTriggers.BuildTriggerProperties;
import com.mjrichardson.teamCity.buildTriggers.MachineAdded.MachinesProvider;
import com.mjrichardson.teamCity.buildTriggers.MachineAdded.MachinesProviderFactory;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class FakeMachinesProviderFactory extends MachinesProviderFactory {
    private final MachinesProvider machinesProvider;

    public FakeMachinesProviderFactory(MachinesProvider machinesProvider) {
        super(new FakeAnalyticsTracker(), new FakeCacheManager(), new FakeMetricRegistry());
        this.machinesProvider = machinesProvider;
    }

    @Override
    public MachinesProvider getProvider(String octopusUrl, String octopusApiKey, BuildTriggerProperties buildTriggerProperties) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return this.machinesProvider;
    }
}
