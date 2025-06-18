package solar;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SolarSystemManager {
    private List<CelestialBody> bodies;
    private Star sun;
    private OrbitRenderer orbitRenderer;
    private CelestialBody focusedBody;
    private DebugOverlay debugOverlay;
    private static final double WINDOW_CENTER_X = 400;  // ウィンドウの中心X
    private static final double WINDOW_CENTER_Y = 300;  // ウィンドウの中心Y

    public SolarSystemManager(DebugOverlay debugOverlay) {
        this.bodies = new ArrayList<>();
        this.debugOverlay = debugOverlay;
        this.orbitRenderer = new OrbitRenderer();
        initializeSolarSystem();
    }

    private void initializeSolarSystem() {
        // 太陽
        sun = new Star("Sun", 0, 0, 30);
        sun.setCenter(WINDOW_CENTER_X, WINDOW_CENTER_Y);
        sun.setColor(Color.YELLOW);
        bodies.add(sun);

        // 水星
        Planet mercury = new Planet("Mercury", 100, 0, 5);
        mercury.setColor(Color.GRAY);
        mercury.setSpeed(0.04);
        bodies.add(mercury);

        // 金星
        Planet venus = new Planet("Venus", 150, 0, 8);
        venus.setColor(Color.ORANGE);
        venus.setSpeed(0.015);
        bodies.add(venus);

        // 地球
        Planet earth = new Planet("Earth", 200, 0, 10);
        earth.setColor(Color.BLUE);
        earth.setSpeed(0.01);
        bodies.add(earth);

        // 火星
        Planet mars = new Planet("Mars", 250, 0, 7);
        mars.setColor(Color.RED);
        mars.setSpeed(0.008);
        bodies.add(mars);

        // 木星
        Planet jupiter = new Planet("Jupiter", 350, 0, 20);
        jupiter.setColor(Color.ORANGE);
        jupiter.setSpeed(0.002);
        bodies.add(jupiter);

        // 土星
        Planet saturn = new Planet("Saturn", 450, 0, 17);
        saturn.setColor(Color.YELLOW);
        saturn.setSpeed(0.0009);
        bodies.add(saturn);

        // 天王星
        Planet uranus = new Planet("Uranus", 550, 0, 12);
        uranus.setColor(Color.CYAN);
        uranus.setSpeed(0.0004);
        bodies.add(uranus);

        // 海王星
        Planet neptune = new Planet("Neptune", 650, 0, 12);
        neptune.setColor(Color.BLUE);
        neptune.setSpeed(0.0001);
        bodies.add(neptune);

        // 冥王星の追加
        Planet pluto = new Planet("Pluto", 750, 0, 4);
        pluto.setColor(Color.LIGHTGRAY);
        pluto.setSpeed(0.00006);  // 最も遅い
        pluto.setEccentricity(0.25);  // 最も楕円軌道
        pluto.setInclination(Math.toRadians(15.5));  // 最も傾いた軌道
        pluto.setCenter(WINDOW_CENTER_X, WINDOW_CENTER_Y);
        bodies.add(pluto);

        // 月の作成
        Moon moon = new Moon("Moon", 20, 3, earth);
        earth.addMoon(moon);
        bodies.add(moon);  // 月もbodiesリストに追加

        // 軌道パラメータの設定
        mercury.setEccentricity(0.206);
        venus.setEccentricity(0.007);
        earth.setEccentricity(0.017);
        mars.setEccentricity(0.094);
        jupiter.setEccentricity(0.049);
        saturn.setEccentricity(0.057);
        uranus.setEccentricity(0.046);
        neptune.setEccentricity(0.011);
        pluto.setEccentricity(0.25);

        // 軌道傾斜の設定（ラジアン）
        mercury.setInclination(Math.toRadians(7.0));
        venus.setInclination(Math.toRadians(3.4));
        earth.setInclination(Math.toRadians(0.0));
        mars.setInclination(Math.toRadians(1.9));
        jupiter.setInclination(Math.toRadians(1.3));
        saturn.setInclination(Math.toRadians(2.5));
        uranus.setInclination(Math.toRadians(0.8));
        neptune.setInclination(Math.toRadians(1.8));
        pluto.setInclination(Math.toRadians(15.5));

        // すべての天体の中心を太陽の位置に設定
        for (CelestialBody body : bodies) {
            if (body != sun) {
                body.setCenter(WINDOW_CENTER_X, WINDOW_CENTER_Y);
            }
        }
    }

    public void update() {
        for (CelestialBody body : bodies) {
            body.update();
        }
    }

    public void draw(GraphicsContext gc) {
        // 軌道の描画
        if (debugOverlay.isShowOrbits()) {
            for (CelestialBody body : bodies) {
                if (body != sun) {
                    orbitRenderer.drawOrbit(gc, body);
                }
            }
        }

        // 中心線の描画
        if (debugOverlay.isShowCenterLine()) {
            gc.setStroke(Color.rgb(255, 0, 0, 0.5));
            // ウィンドウの中心を基準に線を描画
            double centerX = WINDOW_CENTER_X;
            double centerY = WINDOW_CENTER_Y;
            // 縦線
            gc.strokeLine(centerX, centerY - 1000, centerX, centerY + 1000);
            // 横線
            gc.strokeLine(centerX - 1000, centerY, centerX + 1000, centerY);
        }

        // 距離ガイドの描画
        if (debugOverlay.isShowDistance()) {
            gc.setStroke(Color.rgb(0, 255, 0, 0.3));
            for (CelestialBody body : bodies) {
                if (body instanceof Planet) {
                    gc.strokeLine(WINDOW_CENTER_X, WINDOW_CENTER_Y, body.getX(), body.getY());
                }
            }
        }

        // 天体の描画
        for (CelestialBody body : bodies) {
            if (body.isVisible() && debugOverlay.isShowBodies()) {
                body.draw(gc);
            }
        }
    }

    public List<CelestialBody> getBodies() {
        return bodies;
    }

    public Star getSun() {
        return sun;
    }

    public void toggleOrbits() {
        orbitRenderer.toggleOrbits();
    }

    public void toggleLabels() {
        orbitRenderer.toggleLabels();
    }

    public CelestialBody getFocusedBody() {
        return focusedBody;
    }

    public void setFocusedBody(CelestialBody body) {
        this.focusedBody = body;
    }

    public DebugOverlay getDebugOverlay() {
        return debugOverlay;
    }

    public void toggleVisibility(String name) {
        System.out.println("Toggling visibility for: " + name);  // デバッグ出力
        for (CelestialBody body : bodies) {
            System.out.println("Checking body: " + body.getName());  // デバッグ出力
            if (body.getName().equalsIgnoreCase(name)) {
                System.out.println("Found matching body: " + body.getName());  // デバッグ出力
                body.toggleVisible();
                break;
            }
        }
    }
} 