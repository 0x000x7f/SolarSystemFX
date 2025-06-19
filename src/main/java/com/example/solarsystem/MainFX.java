package com.example.solarsystem;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.animation.AnimationTimer;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.PerspectiveCamera;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleStringProperty;
import com.example.solarsystem.model.SolarSystemManager;
import com.example.solarsystem.model.CelestialBody;
import com.example.solarsystem.ui.TimeScaleControl;
import com.example.solarsystem.ui.CelestialInfoPanel;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.WindowEvent;
import javafx.stage.Window;
import javafx.stage.StageStyle;

public class MainFX extends Application {
    private Stage primaryStage;
    private Scene scene;
    private StackPane root;
    private SolarSystemManager solarSystemManager;
    private SubScene subScene;
    private PerspectiveCamera camera;
    private CelestialInfoPanel infoPanel;
    private TimeScaleControl timeScaleControl;
    private AnimationTimer animationTimer;
    private SimpleStringProperty statusMessage;

    private void createAnimation() {
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                solarSystemManager.update(System.nanoTime());
            }
        };
    }

    private void createUI() {
        root = new StackPane();
        root.setPrefSize(1920, 1080);  // 明示的にサイズを設定

        // 3Dビューの設定
        subScene = new SubScene(root, 1920, 1080, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.BLACK);
        subScene.widthProperty().bind(root.widthProperty());  // 幅をバインド
        subScene.heightProperty().bind(root.heightProperty()); // 高さをバインド

        // カメラの設定
        camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-1000);
        subScene.setCamera(camera);

        // 天体の3Dモデルをrootに追加
        for (CelestialBody body : solarSystemManager.getCelestialBodies()) {
            root.getChildren().add(body.getSphere());
        }

        // 3DビューをStackPaneに追加
        StackPane.setAlignment(subScene, Pos.CENTER);
        root.getChildren().add(subScene);

        // 情報パネルの作成
        infoPanel = new CelestialInfoPanel(FXCollections.observableArrayList(solarSystemManager.getCelestialBodies()));
        StackPane.setAlignment(infoPanel, Pos.CENTER_LEFT);
        StackPane.setMargin(infoPanel, new Insets(0, 0, 0, 20));
        root.getChildren().add(infoPanel);

        // ステータスメッセージの初期化
        statusMessage = new SimpleStringProperty("");

        // 時間スケールコントロールの作成
        timeScaleControl = new TimeScaleControl(
            solarSystemManager.timeScaleProperty(),
            solarSystemManager.isPausedProperty(),
            statusMessage
        );
        StackPane.setAlignment(timeScaleControl, Pos.BOTTOM_CENTER);
        StackPane.setMargin(timeScaleControl, new Insets(0, 0, 20, 0));
        root.getChildren().add(timeScaleControl);

        // 一時停止ボタン
        Button pauseButton = new Button("一時停止");
        pauseButton.setOnAction(e -> {
            if (solarSystemManager.isPausedProperty().get()) {
                solarSystemManager.resume();
                pauseButton.setText("一時停止");
            } else {
                solarSystemManager.pause();
                pauseButton.setText("再開");
            }
        });

        // シーンの作成
        scene = new Scene(root, 1920, 1080);
        scene.setFill(Color.BLACK);

        // マウスイベントの設定
        setupMouseEvents();

        // キーボードショートカットの設定
        setupKeyboardShortcuts();

        // アニメーションテイマーの設定
        createAnimation();
        animationTimer.start();
    }

    public void start(Stage primaryStage) {
        try {
            this.primaryStage = primaryStage;
            solarSystemManager = new SolarSystemManager();
            createUI();
            primaryStage.setTitle("Solar System Simulation");
            primaryStage.setScene(scene);
            primaryStage.setWidth(1920);  // 明示的にサイズを設定
            primaryStage.setHeight(1080);
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.show();

            // デバッグ出力でサイズを確認
            primaryStage.setOnShown(e -> {
                System.out.println("Stage size: " + primaryStage.getWidth() + "x" + primaryStage.getHeight());
                System.out.println("Scene size: " + scene.getWidth() + "x" + scene.getHeight());
                System.out.println("SubScene size: " + subScene.getWidth() + "x" + subScene.getHeight());
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error starting application: " + e.getMessage());
        }
    }

    private void setupMouseEvents() {
        scene.setOnMouseMoved(event -> {
            if (infoPanel.isVisible()) {
                Node target = event.getPickResult().getIntersectedNode();
                if (target != null) {
                    CelestialBody body = findCelestialBodyFromNode(target);
                    if (body != null) {
                        body.setHovered(true);
                    }
                }
            }
        });

        scene.setOnMouseClicked(event -> {
            Node target = event.getPickResult().getIntersectedNode();
            if (target != null) {
                CelestialBody body = findCelestialBodyFromNode(target);
                if (body != null) {
                    solarSystemManager.getCelestialBodies().forEach(b -> b.setSelected(false));
                    body.setSelected(true);
                    infoPanel.selectCelestialBody(body);
                }
            }
        });
    }

    private void setupKeyboardShortcuts() {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case SPACE:
                    if (solarSystemManager.isPausedProperty().get()) {
                        solarSystemManager.resume();
                    } else {
                        solarSystemManager.pause();
                    }
                    break;
                case DIGIT1:
                    solarSystemManager.setTimeScale(0.1);
                    break;
                case DIGIT2:
                    solarSystemManager.setTimeScale(1.0);
                    break;
                case DIGIT3:
                    solarSystemManager.setTimeScale(10.0);
                    break;
                case DIGIT4:
                    solarSystemManager.setTimeScale(100.0);
                    break;
                case R:
                    solarSystemManager.reset();
                    break;
                case I:
                    infoPanel.setVisible(!infoPanel.isVisible());
                    break;
            }
        });
    }

    private CelestialBody findCelestialBodyFromNode(Node node) {
        return solarSystemManager.getCelestialBodies().stream()
            .filter(body -> body.getSphere() == node)
            .findFirst()
            .orElse(null);
    }

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error launching application: " + e.getMessage());
        }
    }
} 