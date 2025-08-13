package org.fabianandiel.services;

import org.fabianandiel.constants.Constants;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceProvider {

    private static final ExecutorService executorService =
           Executors.newFixedThreadPool(4);

    /**
     * private constructor to prevent initialization
     */
    private ExecutorServiceProvider() {
        throw new UnsupportedOperationException(Constants.WARNING_UTILITY_CLASS);
    }

    /**
     * provides the executor service
     * @return an executor service instance
     */
    public static ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * shuts down the executor service
     */
    public static void shutdown() {
        executorService.shutdown();
    }

}
