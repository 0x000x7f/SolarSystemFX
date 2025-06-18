package solar;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.animation.AnimationTimer;
import java.io.InputStream;

public class MainFX extends Application {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private Font notoSansJP;
    private long lastUpdate = 0;
    private SolarSystemManager solarSystem;
    private Camera camera;
    private DebugOverlay debugOverlay;

    @Override
    public void init() {
        // フォントの読み込み
        try (InputStream is = getClass().getResourceAsStream("/fonts/NotoSansJP-Regular.ttf")) {
            if (is != null) {
                notoSansJP = Font.loadFont(is, 24);
            } else {
                System.err.println("フォントファイルが見つかりません");
                notoSansJP = Font.font("MS Gothic", 24);
            }
        } catch (Exception e) {
            System.err.println("フォントの読み込みに失敗しました: " + e.getMessage());
            notoSansJP = Font.font("MS Gothic", 24);
        }

        // システムの初期化
        debugOverlay = new DebugOverlay();
        solarSystem = new SolarSystemManager(debugOverlay);
        camera = new Camera();
    }

    @Override
    public void start(Stage primaryStage) {
        // キャンバスの作成
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // アニメーションタイマーの設定
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }

                double deltaTime = (now - lastUpdate) / 1_000_000_000.0;
                lastUpdate = now;

                // カメラの更新
                camera.update();
                
                // 描画処理
                draw(gc);
            }
        };

        // シーンの設定
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // キーイベントの設定
        scene.setOnKeyPressed(e -> {
            String code = e.getCode().toString();
            camera.handleKeyPress(code);
            
            // デバッグ関連のキー操作
            switch (code) {
                case "D":
                    debugOverlay.toggle();
                    break;
                case "H":
                    debugOverlay.toggleHelp();
                    break;
                case "V":
                    debugOverlay.toggleStructure();
                    break;
                case "T":
                    debugOverlay.toggleBodies();
                    break;
                case "O":
                    debugOverlay.toggleOrbits();
                    break;
                case "C":
                    debugOverlay.toggleCenterLine();
                    break;
                case "B":
                    debugOverlay.toggleDistanceGuide();
                    break;
                case "DIGIT1":
                    solarSystem.toggleVisibility("Mercury");
                    break;
                case "DIGIT2":
                    solarSystem.toggleVisibility("Venus");
                    break;
                case "DIGIT3":
                    solarSystem.toggleVisibility("Earth");
                    break;
                case "DIGIT4":
                    solarSystem.toggleVisibility("Mars");
                    break;
                case "DIGIT5":
                    solarSystem.toggleVisibility("Jupiter");
                    break;
                case "DIGIT6":
                    solarSystem.toggleVisibility("Saturn");
                    break;
                case "DIGIT7":
                    solarSystem.toggleVisibility("Uranus");
                    break;
                case "DIGIT8":
                    solarSystem.toggleVisibility("Neptune");
                    break;
                case "DIGIT9":
                    solarSystem.toggleVisibility("Pluto");
                    break;
                case "DIGIT0":
                    solarSystem.toggleVisibility("Sun");
                    break;
                case "Y":
                    // TODO: YAML出力機能の実装
                    System.out.println("YAML出力機能は未実装です");
                    break;
            }
        });

        // マウスイベントの設定
        canvas.setOnMouseClicked(event -> {
            double x = event.getX();
            double y = event.getY();
            
            // クリック位置の天体を検索
            CelestialBody clickedBody = findBodyAtPosition(x, y);
            if (clickedBody != null) {
                camera.focusOn(clickedBody);
            }
        });

        canvas.setOnMouseDragged(event -> {
            double dx = event.getX() - event.getX();
            double dy = event.getY() - event.getY();
            camera.handleMouseDrag(dx, dy);
        });

        canvas.setOnScroll(event -> {
            double delta = event.getDeltaY();
            camera.handleScroll(delta);
        });
        
        // ステージの設定
        primaryStage.setTitle("Solar System Simulation - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();

        // アニメーション開始
        timer.start();
    }

    private CelestialBody findBodyAtPosition(double screenX, double screenY) {
        // スクリーン座標をワールド座標に変換
        double worldX = (screenX - camera.getX()) / camera.getScale();
        double worldY = (screenY - camera.getY()) / camera.getScale();

        // 天体を検索（大きい順に）
        for (CelestialBody body : solarSystem.getBodies()) {
            double dx = worldX - body.getX();
            double dy = worldY - body.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);
            
            if (distance <= body.getRadius()) {
                return body;
            }
        }
        return null;
    }

    private void draw(GraphicsContext gc) {
        // 背景を黒で塗りつぶす
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        gc.save();
        camera.apply(gc);
        solarSystem.update();
        solarSystem.draw(gc);
        gc.restore();
        debugOverlay.draw(gc, solarSystem, camera);
    }

    public static void main(String[] args) {
        launch(args);
    }
} 