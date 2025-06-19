package com.example.solarsystem.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import com.example.solarsystem.data.CelestialDataLoader;
import com.example.solarsystem.data.CelestialDataLoader.CelestialData;
import com.example.solarsystem.data.CelestialDataLoader.CelestialBodyData;
import com.example.solarsystem.data.CelestialDataLoader.OrbitData;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.List;

public class SolarSystemManager {
    private static final Logger LOGGER = Logger.getLogger(SolarSystemManager.class.getName());
    private static final double G = 6.67430e-11; // 万有引力定数
    private static final double TIME_SCALE = 1.0; // 時間スケール（1.0 = 実時間）

    private final ObservableList<CelestialBody> bodies;
    private final Map<String, CelestialBody> bodyMap;
    private final DoubleProperty timeScale;
    private final BooleanProperty isPaused;
    private final StringProperty statusMessage;
    private double currentTime;
    private long lastUpdateTime;  // 追加：最後の更新時間

    public SolarSystemManager() {
        this.bodies = FXCollections.observableArrayList();
        this.bodyMap = new HashMap<>();
        this.timeScale = new SimpleDoubleProperty(TIME_SCALE);
        this.isPaused = new SimpleBooleanProperty(false);
        this.statusMessage = new SimpleStringProperty("");
        this.currentTime = 0.0;
        this.lastUpdateTime = 0;  // 初期化

        // データの読み込み
        loadCelestialBodies();
    }

    private void loadCelestialBodies() {
        try {
            CelestialData data = CelestialDataLoader.loadData();
            LOGGER.info("天体データを読み込みました: " + data.celestialBodies.size() + "個の天体");

            for (CelestialBodyData bodyData : data.celestialBodies) {
                try {
                    CelestialBody body = new CelestialBody(bodyData);
                    bodies.add(body);
                    bodyMap.put(bodyData.id, body);
                    LOGGER.info("天体を追加しました: " + bodyData.name);
                } catch (Exception e) {
                    LOGGER.warning("天体の追加に失敗しました: " + bodyData.name + " - " + e.getMessage());
                }
            }

            // 初期位置の計算
            calculateInitialPositions();
            statusMessage.set("天体データの読み込みが完了しました");

        } catch (Exception e) {
            LOGGER.severe("天体データの読み込みに失敗しました: " + e.getMessage());
            statusMessage.set("エラー: 天体データの読み込みに失敗しました");
        }
    }

    private void calculateInitialPositions() {
        for (CelestialBody body : bodies) {
            if (body.getOrbitData() != null) {
                OrbitData orbit = body.getOrbitData();
                // 初期角度が設定されていない場合は0を使用
                double angle = orbit.initialAngle;
                // 半径が設定されていない場合は長半径を使用
                double radius = orbit.radius > 0 ? orbit.radius : orbit.semiMajorAxis;
                double x = radius * Math.cos(angle);
                double z = radius * Math.sin(angle);
                body.updatePosition(new Point3D(x, 0, z));
            }
        }
    }

    public void update(long now) {
        if (isPaused.get()) {
            return;
        }
        if (lastUpdateTime == 0) {
            lastUpdateTime = now;
            return;
        }
        double deltaTime = (now - lastUpdateTime) / 1_000_000_000.0 * timeScale.get();
        lastUpdateTime = now;

        // 天体の位置を更新
        for (CelestialBody body : bodies) {
            if (body.isVisible()) {
                body.updatePosition(deltaTime);
            }
        }

        // 重力の影響を計算
        calculateGravitationalForces();
    }

    private void calculateGravitationalForces() {
        for (CelestialBody body1 : bodies) {
            if (!body1.isVisible()) continue;

            Point3D totalForce = new Point3D(0, 0, 0);
            for (CelestialBody body2 : bodies) {
                if (body1 == body2 || !body2.isVisible()) continue;

                Point3D force = calculateGravitationalForce(body1, body2);
                totalForce = totalForce.add(force);
            }

            // 速度と位置の更新
            Point3D currentVelocity = body1.velocityProperty().get();
            Point3D newVelocity = currentVelocity.add(totalForce.multiply(timeScale.get()));
            Point3D currentPosition = body1.positionProperty().get();
            Point3D newPosition = currentPosition.add(newVelocity.multiply(timeScale.get()));

            body1.updateVelocity(newVelocity);
            body1.updatePosition(newPosition);
        }
    }

    private Point3D calculateGravitationalForce(CelestialBody body1, CelestialBody body2) {
        Point3D pos1 = body1.positionProperty().get();
        Point3D pos2 = body2.positionProperty().get();
        Point3D direction = pos2.subtract(pos1);
        double distance = direction.magnitude();

        if (distance == 0) return new Point3D(0, 0, 0);

        double forceMagnitude = G * body1.massProperty().get() * body2.massProperty().get() 
                              / (distance * distance);
        return direction.normalize().multiply(forceMagnitude);
    }

    // プロパティのゲッター
    public ObservableList<CelestialBody> getBodies() { return bodies; }
    public DoubleProperty timeScaleProperty() { return timeScale; }
    public BooleanProperty isPausedProperty() { return isPaused; }
    public StringProperty statusMessageProperty() { return statusMessage; }

    // 天体の取得
    public CelestialBody getBody(String id) { return bodyMap.get(id); }

    // 時間スケールの設定
    public void setTimeScale(double newTimeScale) {
        if (newTimeScale < 0) {
            newTimeScale = 0;
        } else if (newTimeScale > 100.0) {
            newTimeScale = 100.0;
        }
        
        if (Math.abs(timeScale.get() - newTimeScale) > 0.0001) {
            timeScale.set(newTimeScale);
        }
    }

    // 一時停止の切り替え
    public void pause() {
        if (!isPaused.get()) {
            isPaused.set(true);
            lastUpdateTime = 0;  // 一時停止時にlastUpdateTimeをリセット
        }
    }

    public void resume() {
        if (isPaused.get()) {
            isPaused.set(false);
            lastUpdateTime = System.nanoTime();  // 再開時に現在時刻を設定
        }
    }

    // リセット
    public void reset() {
        isPaused.set(false);
        timeScale.set(1.0);
        lastUpdateTime = 0;
        currentTime = 0;
        
        // 天体の状態をリセット
        for (CelestialBody body : bodies) {
            body.reset();
        }
        
        // 初期位置を再計算
        calculateInitialPositions();
    }

    public List<CelestialBody> getCelestialBodies() {
        return bodies;
    }

    // 現在の時間を取得（テスト用）
    public double getCurrentTime() {
        return currentTime;
    }
} 