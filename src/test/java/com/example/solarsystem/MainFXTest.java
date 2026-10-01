package com.example.solarsystem;

import javafx.scene.SubScene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MainFXTest {
    @BeforeAll
    static void startJavaFX() throws Exception { FxTestSupport.start(); }

    @Test
    void applicationStartsWithSeparateWorldAndUiRoots() throws Exception {
        FxTestSupport.run(() -> {
            var application = new MainFX();
            var stage = new Stage();
            try {
                application.start(stage);
                assertTrue(stage.isShowing(), "Application must show a window instead of swallowing startup errors");
                Parent uiRoot = stage.getScene().getRoot();
                SubScene world = (SubScene) uiRoot.getChildrenUnmodifiable().stream()
                    .filter(SubScene.class::isInstance).findFirst().orElseThrow();
                assertNotSame(uiRoot, world.getRoot());
                assertFalse(world.getRoot().getChildrenUnmodifiable().isEmpty());
            } finally {
                application.stop();
                stage.close();
            }
        });
    }

    @Test
    void fxThreadAssertionFailuresReachJUnit() {
        assertThrows(AssertionError.class, () -> FxTestSupport.run(() -> {
            throw new AssertionError("Intentional failure to verify test harness");
        }));
    }
}
