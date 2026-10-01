package com.example.solarsystem.ui;

import com.example.solarsystem.FxTestSupport;
import com.example.solarsystem.model.SolarSystemManager;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CelestialInfoPanelTest {
    @BeforeAll
    static void startJavaFX() throws Exception { FxTestSupport.start(); }

    @Test
    void selectingLoadedBodyUpdatesDisplayedName() throws Exception {
        FxTestSupport.run(() -> {
            var manager = new SolarSystemManager();
            assertFalse(manager.getBodies().isEmpty(), "Default planet data must load");
            var panel = new CelestialInfoPanel(manager.getBodies());
            new Scene(panel);
            panel.applyCss();
            var body = manager.getBodies().get(0);
            panel.selectCelestialBody(body);
            assertSame(body, panel.getSelectedBody());
            assertTrue(panel.lookupAll(".label").stream().anyMatch(node ->
                ((Label) node).getText().equals("名前: " + body.nameProperty().get())));
            panel.showPanel(false);
            assertFalse(panel.isVisible());
            panel.showPanel(true);
            assertTrue(panel.isVisible());
        });
    }
}
