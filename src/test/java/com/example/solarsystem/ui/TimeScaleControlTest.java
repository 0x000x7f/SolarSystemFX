package com.example.solarsystem.ui;

import com.example.solarsystem.FxTestSupport;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TimeScaleControlTest {
    @BeforeAll
    static void startJavaFX() throws Exception { FxTestSupport.start(); }

    @Test
    void presetPauseAndStatusControlsUpdateBoundProperties() throws Exception {
        FxTestSupport.run(() -> {
            var scale = new SimpleDoubleProperty(1.0);
            var paused = new SimpleBooleanProperty(false);
            var status = new SimpleStringProperty("Ready");
            var control = new TimeScaleControl(scale, paused, status);
            new Scene(control);
            control.applyCss();
            Button preset = (Button) control.lookupAll(".button").stream()
                .filter(node -> node instanceof Button button && button.getText().equals("10x"))
                .findFirst().orElseThrow();
            preset.fire();
            assertEquals(10.0, scale.get());
            ToggleButton pause = (ToggleButton) control.lookup(".toggle-button");
            pause.fire();
            assertTrue(paused.get());
            assertEquals("再開", pause.getText());
            pause.fire();
            assertFalse(paused.get());
            status.set("Updated");
            assertTrue(control.lookupAll(".label").stream()
                .anyMatch(node -> ((Label) node).getText().equals("Updated")));
        });
    }
}
