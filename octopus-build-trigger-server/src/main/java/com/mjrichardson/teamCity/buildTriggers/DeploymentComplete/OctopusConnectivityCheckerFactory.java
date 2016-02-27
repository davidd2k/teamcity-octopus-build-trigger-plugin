package com.mjrichardson.teamCity.buildTriggers.DeploymentComplete;

import com.mjrichardson.teamCity.buildTriggers.OctopusConnectivityChecker;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class OctopusConnectivityCheckerFactory {
    public OctopusConnectivityChecker create(String url, String apiKey, Integer connectionTimeout) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return new OctopusConnectivityChecker(url, apiKey, connectionTimeout);
    }
}
