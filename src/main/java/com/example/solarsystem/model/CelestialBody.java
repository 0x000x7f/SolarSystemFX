package com.example.solarsystem.model;

import javafx.beans.property.*;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import com.example.solarsystem.data.CelestialDataLoader.CelestialBodyData;
import com.example.solarsystem.data.CelestialDataLoader.OrbitData;
import com.example.solarsystem.data.CelestialDataLoader.RotationData;

public class CelestialBody {
    private final String id;
    private final StringProperty name;
    private final StringProperty type;
    private final DoubleProperty mass;
    private final DoubleProperty radius;
    private final ObjectProperty<Point3D> position;
    private final ObjectProperty<Point3D> velocity;
    private final BooleanProperty visible;
    private final BooleanProperty orbitVisible;
    private final Sphere sphere;
    private final PhongMaterial material;
    private final Rotate rotationTransform;
    private final Rotate orbitTransform;
    private final OrbitData orbitData;
    private final RotationData rotationData;
    private final String texturePath;
    private final String colorHex;
    private final BooleanProperty selected;
    private final BooleanProperty hovered;

    public CelestialBody(CelestialBodyData data) {
        this.id = data.id;
        this.name = new SimpleStringProperty(data.name);
        this.type = new SimpleStringProperty(data.type);
        this.mass = new SimpleDoubleProperty(data.mass);
        this.radius = new SimpleDoubleProperty(data.radius);
        this.position = new SimpleObjectProperty<>(new Point3D(0, 0, 0));
        this.velocity = new SimpleObjectProperty<>(new Point3D(0, 0, 0));
        this.visible = new SimpleBooleanProperty(true);
        this.orbitVisible = new SimpleBooleanProperty(true);
        this.orbitData = data.orbit;
        this.rotationData = data.rotation;
        this.texturePath = data.texture;
        this.colorHex = data.color;

        this.selected = new SimpleBooleanProperty(false);
        this.hovered = new SimpleBooleanProperty(false);

        // 3Dモデルの初期化
        this.sphere = new Sphere(data.radius);
        this.material = new PhongMaterial();
        if (texturePath != null && !texturePath.isEmpty()) {
            // TODO: テクスチャの読み込みと設定
        } else {
            material.setDiffuseColor(Color.web(colorHex));
        }
        sphere.setMaterial(material);

        // 回転の初期化
        this.rotationTransform = new Rotate();
        if (rotationData != null && rotationData.axis != null) {
            rotationTransform.setAxis(new Point3D(
                rotationData.axis.x,
                rotationData.axis.y,
                rotationData.axis.z
            ));
        }
        sphere.getTransforms().add(rotationTransform);

        // 軌道の初期化
        this.orbitTransform = new Rotate();
        if (orbitData != null) {
            orbitTransform.setAxis(new Point3D(0, 1, 0));
            orbitTransform.setAngle(orbitData.inclination);
        }
        sphere.getTransforms().add(orbitTransform);

        // 選択状態の変更を監視
        selected.addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                material.setDiffuseColor(Color.web(colorHex).brighter());
            } else {
                material.setDiffuseColor(Color.web(colorHex));
            }
        });

        // ホバー状態の変更を監視
        hovered.addListener((obs, oldVal, newVal) -> {
            if (newVal && !selected.get()) {
                material.setDiffuseColor(Color.web(colorHex).brighter().brighter());
            } else if (!selected.get()) {
                material.setDiffuseColor(Color.web(colorHex));
            }
        });
    }

    // プロパティのゲッター
    public String getId() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty typeProperty() { return type; }
    public DoubleProperty massProperty() { return mass; }
    public DoubleProperty radiusProperty() { return radius; }
    public ObjectProperty<Point3D> positionProperty() { return position; }
    public ObjectProperty<Point3D> velocityProperty() { return velocity; }
    public BooleanProperty visibleProperty() { return visible; }
    public BooleanProperty orbitVisibleProperty() { return orbitVisible; }
    public Sphere getSphere() { return sphere; }
    public OrbitData getOrbitData() { return orbitData; }
    public RotationData getRotationData() { return rotationData; }
    public BooleanProperty selectedProperty() { return selected; }
    public BooleanProperty hoveredProperty() { return hovered; }

    public boolean isVisible() {
        return visible.get();
    }

    public void setVisible(boolean visible) {
        this.visible.set(visible);
        sphere.setVisible(visible);
    }

    public void updatePosition(double deltaTime) {
        if (orbitData != null) {
            // 軌道運動の更新
            double angle = orbitData.initialAngle + (deltaTime * orbitData.period);
            double x = Math.cos(angle) * orbitData.radius;
            double z = Math.sin(angle) * orbitData.radius;
            position.set(new Point3D(x, 0, z));
        }
    }

    public void updatePosition(Point3D newPosition) {
        position.set(newPosition);
        sphere.setTranslateX(newPosition.getX());
        sphere.setTranslateY(newPosition.getY());
        sphere.setTranslateZ(newPosition.getZ());
    }

    public void updateVelocity(Point3D newVelocity) {
        velocity.set(newVelocity);
    }

    // 回転の更新
    public void updateRotation(double angle) {
        if (rotationData != null) {
            rotationTransform.setAngle(angle);
        }
    }

    // 軌道の更新
    public void updateOrbit(double angle) {
        if (orbitData != null) {
            orbitTransform.setAngle(angle);
        }
    }

    // テクスチャの更新
    public void updateTexture(String texturePath) {
        // TODO: テクスチャの動的更新
    }

    // 色の更新
    public void updateColor(String colorHex) {
        material.setDiffuseColor(Color.web(colorHex));
    }

    // 選択状態の設定
    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    // ホバー状態の設定
    public void setHovered(boolean hovered) {
        this.hovered.set(hovered);
    }

    // 選択状態の取得
    public boolean isSelected() {
        return selected.get();
    }

    // ホバー状態の取得
    public boolean isHovered() {
        return hovered.get();
    }

    // 状態のリセット
    public void reset() {
        position.set(new Point3D(0, 0, 0));
        velocity.set(new Point3D(0, 0, 0));
        sphere.setTranslateX(0);
        sphere.setTranslateY(0);
        sphere.setTranslateZ(0);

        if (rotationData != null) {
            rotationTransform.setAngle(0);
        }

        if (orbitData != null) {
            orbitTransform.setAngle(orbitData.inclination);
        }

        selected.set(false);
        hovered.set(false);
    }
} 