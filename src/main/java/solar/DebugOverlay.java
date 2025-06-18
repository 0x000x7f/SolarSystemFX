package solar;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import java.util.ArrayList;
import java.util.List;

public class DebugOverlay {
    private boolean visible = false;
    private boolean showHelp = false;
    private boolean showStructure = false;
    private boolean showBodies = true;
    private boolean showOrbits = true;
    private boolean showCenterLine = false;
    private boolean showDistance = false;
    private Font font;
    private List<String> debugMessages = new ArrayList<>();
    private static final int PADDING = 10;
    private static final int LINE_HEIGHT = 20;

    public DebugOverlay() {
        // デフォルトフォントの設定
        this.font = Font.font("Monospaced", 12);
    }

    public void toggle() {
        visible = !visible;
    }

    public void toggleHelp() {
        showHelp = !showHelp;
    }

    public void toggleStructure() {
        showStructure = !showStructure;
    }

    public void toggleBodies() {
        showBodies = !showBodies;
    }

    public void toggleOrbits() {
        showOrbits = !showOrbits;
    }

    public void toggleCenterLine() {
        showCenterLine = !showCenterLine;
    }

    public void toggleDistanceGuide() {
        showDistance = !showDistance;
    }

    public void draw(GraphicsContext gc, SolarSystemManager solarSystem, Camera camera) {
        if (!visible) return;

        gc.save();
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(10, 10, 300, 200);
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.LEFT);

        // カメラ情報
        gc.fillText(String.format("Camera: (%.1f, %.1f) Scale: %.2f", 
            camera.getX(), camera.getY(), camera.getScale()), 20, 30);

        // フォーカス情報
        CelestialBody focused = solarSystem.getFocusedBody();
        if (focused != null) {
            gc.fillText(String.format("Focus: %s (%.1f, %.1f)", 
                focused.getName(), focused.getX(), focused.getY()), 20, 50);
        }

        // 天体の表示状態
        gc.fillText("天体の表示状態:", 20, 80);
        int y = 100;
        for (CelestialBody body : solarSystem.getBodies()) {
            gc.fillText(String.format("%s: %s", 
                body.getName(), body.isVisible() ? "表示" : "非表示"), 30, y);
            y += 20;
        }

        // 表示制御状態
        gc.fillText("表示制御:", 20, y + 20);
        gc.fillText(String.format("天体: %s", showBodies ? "ON" : "OFF"), 30, y + 40);
        gc.fillText(String.format("軌道: %s", showOrbits ? "ON" : "OFF"), 30, y + 60);
        gc.fillText(String.format("中心線: %s", showCenterLine ? "ON" : "OFF"), 30, y + 80);
        gc.fillText(String.format("距離ガイド: %s", showDistance ? "ON" : "OFF"), 30, y + 100);

        // ヘルプ表示
        if (showHelp) {
            gc.fillText("操作キー:", 20, y + 140);
            gc.fillText("D: デバッグ表示", 30, y + 160);
            gc.fillText("H: ヘルプ表示", 30, y + 180);
            gc.fillText("V: 構造表示", 30, y + 200);
            gc.fillText("T: 天体表示切替", 30, y + 220);
            gc.fillText("O: 軌道表示切替", 30, y + 240);
            gc.fillText("C: 中心線表示切替", 30, y + 260);
            gc.fillText("B: 距離ガイド表示切替", 30, y + 280);
            gc.fillText("0-9: 個別天体の表示切替", 30, y + 300);
        }

        gc.restore();
    }

    private void addDebugInfo(String category, String message) {
        if (category.isEmpty()) {
            debugMessages.add("  " + message);
        } else {
            debugMessages.add(category + ": " + message);
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isShowingHelp() {
        return showHelp;
    }

    public boolean isShowingStructure() {
        return showStructure;
    }

    public boolean isShowBodies() {
        return showBodies;
    }

    public boolean isShowOrbits() {
        return showOrbits;
    }

    public boolean isShowCenterLine() {
        return showCenterLine;
    }

    public boolean isShowDistance() {
        return showDistance;
    }
} 