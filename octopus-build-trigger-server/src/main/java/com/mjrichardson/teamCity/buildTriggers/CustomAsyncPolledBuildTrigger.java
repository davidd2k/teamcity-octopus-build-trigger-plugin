package com.mjrichardson.teamCity.buildTriggers;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.ServiceLocator;
import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import jetbrains.buildServer.buildTriggers.PolledTriggerContext;
import jetbrains.buildServer.buildTriggers.async.CheckJobStatusStorage;
import jetbrains.buildServer.buildTriggers.async.CheckResult;
import jetbrains.buildServer.buildTriggers.async.DetectionException;
import jetbrains.buildServer.buildTriggers.async.impl.AsyncPolledBuildTrigger;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.users.SUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class CustomAsyncPolledBuildTrigger<TItem> extends AsyncPolledBuildTrigger<TItem> {
    private final Logger log;
    private final CustomAsyncBuildTrigger<TItem> asyncBuildTrigger;
    private final ServiceLocator serviceLocator;

    public CustomAsyncPolledBuildTrigger(ExecutorService executorService,
                                         Logger log,
                                         CheckJobStatusStorage<TItem> storage,
                                         int pollInterval,
                                         CustomAsyncBuildTrigger<TItem> asyncBuildTrigger,
                                         ServiceLocator serviceLocator) {
        super(executorService, log, storage, pollInterval, asyncBuildTrigger);
        this.log = log;
        this.asyncBuildTrigger = asyncBuildTrigger;
        this.serviceLocator = serviceLocator;
    }

    @Override
    public void triggerBuild(@NotNull PolledTriggerContext context) throws BuildTriggerException {
        CheckResult result = checkJobStatus(context.getBuildType(), context.getTriggerDescriptor(), context.getCustomDataStorage());
        if (result != null) {
            JobResult jobResult = mapCheckResultToJobResult(result);

            if (jobResult.shouldTriggerBuild()) {
                SBuildType buildType = serviceLocator.getSingletonService(ProjectManager.class)
                        .findBuildTypeByExternalId(context.getBuildType().getExternalId());
                SUser user = null;
                final BuildCustomizer customizer = serviceLocator.getSingletonService(BuildCustomizerFactory.class)
                        .createBuildCustomizer(buildType, user);
                customizer.setParameters(jobResult.properties);
                try {
                    log.info("Creating build promotion");
                    BuildPromotionEx promotion = (BuildPromotionEx)customizer.createPromotion();
                    log.info("Build created - " + promotion.getId());

                    final BuildTypeEx bt = (promotion).getBuildType();

                    log.info("Adding to queue");
                    SQueuedBuild addResult = bt.addToQueue(promotion, jobResult.triggeredBy);
                    log.info("Added to queue - " + addResult.toString());
                }
                catch(Exception e) {
                    log.error("Failed to create the build promotion", e);
                }
            }

            if(jobResult.ex != null) {
                throw jobResult.ex;
            }
        }
    }

    @NotNull
    private JobResult mapCheckResultToJobResult(@NotNull CheckResult<TItem> checkResult) {
        JobResult summary = new JobResult();
        Throwable generalError = checkResult.getGeneralError();
        if(generalError != null) {
            summary.ex = asyncBuildTrigger.makeTriggerException(generalError);
        } else {
            Collection<DetectionException> errors = checkResult.getCheckErrors().values();
            if(!errors.isEmpty()) {
                DetectionException cause = errors.iterator().next();
                summary.ex = asyncBuildTrigger.makeTriggerException(cause);
            }
        }

        Collection<TItem> updated = checkResult.getUpdated();
        if (!updated.isEmpty()) {
            this.log.debug("changes detected in " + updated);
            TItem update = updated.iterator().next();
            summary.triggeredBy = asyncBuildTrigger.getRequestorString(update);
            summary.properties = asyncBuildTrigger.getProperties(update);
        } else {
            this.log.debug("changes not detected");
        }

        return summary;
    }

    private class JobResult {
        @Nullable
        private String triggeredBy;
        @Nullable
        private BuildTriggerException ex;
        @NotNull
        private Map<String, String> properties;

        public boolean shouldTriggerBuild() {
            return triggeredBy != null;
        }
    }
}