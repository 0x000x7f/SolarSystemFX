package com.example.solarsystem.model;

import com.example.solarsystem.FxTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SolarSystemManagerTest {
    private SolarSystemManager manager;
    private static final long START = 1_000_000_000L;

    @BeforeAll
    static void startJavaFX() throws Exception { FxTestSupport.start(); }

    @BeforeEach
    void createManager() throws Exception {
        FxTestSupport.run(() -> manager = new SolarSystemManager());
    }

    @Test
    void pauseAndResumePublishState() throws Exception {
        FxTestSupport.run(() -> {
            assertFalse(manager.isPausedProperty().get());
            manager.pause();
            assertTrue(manager.isPausedProperty().get());
            manager.resume();
            assertFalse(manager.isPausedProperty().get());
        });
    }

    @Test
    void timeScaleNotifiesOnlyOnChange() throws Exception {
        FxTestSupport.run(() -> {
            int[] changes = {0};
            manager.timeScaleProperty().addListener((obs, oldValue, newValue) -> changes[0]++);
            manager.setTimeScale(0.5);
            assertEquals(0.5, manager.timeScaleProperty().get());
            manager.setTimeScale(0.5);
            assertEquals(1, changes[0]);
        });
    }

    @Test
    void propertiesCanBeUpdatedExternally() throws Exception {
        FxTestSupport.run(() -> {
            manager.timeScaleProperty().set(2.0);
            manager.isPausedProperty().set(true);
            assertEquals(2.0, manager.timeScaleProperty().get());
            assertTrue(manager.isPausedProperty().get());
        });
    }

    @Test
    void firstUpdateInitializesClockAndLaterUpdatesAccumulateScaledSeconds() throws Exception {
        FxTestSupport.run(() -> {
            manager.update(START);
            assertEquals(0.0, manager.getCurrentTime());
            manager.update(START + 500_000_000L);
            assertEquals(0.5, manager.getCurrentTime(), 1e-9);
            manager.setTimeScale(2.0);
            manager.update(START + 1_500_000_000L);
            assertEquals(2.5, manager.getCurrentTime(), 1e-9);
        });
    }

    @Test
    void pauseExcludesElapsedPausedTime() throws Exception {
        FxTestSupport.run(() -> {
            manager.update(START);
            manager.update(START + 1_000_000_000L);
            manager.pause();
            manager.update(START + 3_000_000_000L);
            assertEquals(1.0, manager.getCurrentTime(), 1e-9);
            manager.resume();
            manager.update(START + 4_000_000_000L);
            assertEquals(1.0, manager.getCurrentTime(), 1e-9);
            manager.update(START + 5_000_000_000L);
            assertEquals(2.0, manager.getCurrentTime(), 1e-9);
        });
    }

    @Test
    void pauseViaBoundPropertyAlsoExcludesPausedTime() throws Exception {
        FxTestSupport.run(() -> {
            manager.update(START);
            manager.update(START + 1_000_000_000L);
            manager.isPausedProperty().set(true);
            manager.update(START + 3_000_000_000L);
            manager.isPausedProperty().set(false);
            manager.update(START + 4_000_000_000L);
            assertEquals(1.0, manager.getCurrentTime(), 1e-9);
            manager.update(START + 5_000_000_000L);
            assertEquals(2.0, manager.getCurrentTime(), 1e-9);
        });
    }

    @Test
    void resetRestartsTheClockAndRestoresControls() throws Exception {
        FxTestSupport.run(() -> {
            manager.update(START);
            manager.update(START + 1_000_000_000L);
            manager.setTimeScale(10);
            manager.pause();
            manager.reset();
            assertEquals(0.0, manager.getCurrentTime());
            assertEquals(1.0, manager.timeScaleProperty().get());
            assertFalse(manager.isPausedProperty().get());
            manager.update(START + 2_000_000_000L);
            assertEquals(0.0, manager.getCurrentTime());
            manager.update(START + 3_000_000_000L);
            assertEquals(1.0, manager.getCurrentTime(), 1e-9);
        });
    }

    @Test
    void timeScaleSetterClampsToExistingZeroToHundredContract() throws Exception {
        FxTestSupport.run(() -> {
            manager.setTimeScale(-1.0);
            assertEquals(0.0, manager.timeScaleProperty().get());
            manager.setTimeScale(0.05);
            assertEquals(0.05, manager.timeScaleProperty().get());
            manager.setTimeScale(200.0);
            assertEquals(100.0, manager.timeScaleProperty().get());
        });
    }
}
