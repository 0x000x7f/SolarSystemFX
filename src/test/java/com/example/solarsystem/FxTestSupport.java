package com.example.solarsystem;

import javafx.application.Platform;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/** Runs assertions on the FX thread and propagates failures to JUnit. */
public final class FxTestSupport {
    private static boolean started;

    private FxTestSupport() {}

    public static synchronized void start() throws Exception {
        if (started) return;
        CountDownLatch ready = new CountDownLatch(1);
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            ready.countDown();
        });
        if (!ready.await(15, TimeUnit.SECONDS)) {
            throw new AssertionError("JavaFX startup timed out");
        }
        started = true;
    }

    public static void run(Runnable action) throws Exception {
        if (Platform.isFxApplicationThread()) {
            action.run();
            return;
        }
        FutureTask<Void> task = new FutureTask<>(action, null);
        Platform.runLater(task);
        try {
            task.get(15, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof Error error) throw error;
            if (e.getCause() instanceof Exception exception) throw exception;
            throw e;
        }
    }
}
