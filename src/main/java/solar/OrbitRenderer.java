package solar;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

public class OrbitRenderer {
    private static final double ORBIT_ALPHA = 0.3;  // 軌道の透明度
    private static final double ORBIT_STROKE_WIDTH = 1.0;  // 軌道の線の太さ
    private boolean showOrbits = true;  // 軌道表示フラグ
    private boolean showLabels = true;  // ラベル表示フラグ

    public void drawOrbit(GraphicsContext gc, CelestialBody body) {
        if (!body.isVisible()) return;

        // 軌道の色を20%明るく
        Color orbitColor = body.getColor();
        Color brighterColor = orbitColor.brighter().brighter(); // 20%明るく
        gc.setStroke(brighterColor);
        gc.setLineWidth(1.0);

        // 楕円軌道の描画
        double a = body.getDistance();
        double e = body.getEccentricity();
        double b = a * Math.sqrt(1 - e * e);

        gc.beginPath();
        for (double angle = 0; angle <= 2 * Math.PI; angle += 0.01) {
            double r = a * (1 - e * e) / (1 + e * Math.cos(angle));
            
            // 軌道傾斜を考慮
            double baseX = r * Math.cos(angle);
            double baseY = r * Math.sin(angle);
            double rotatedX = baseX * Math.cos(body.getInclination());
            double rotatedY = baseY;

            double x = body.getCenterX() + rotatedX;
            double y = body.getCenterY() + rotatedY;

            if (angle == 0) {
                gc.moveTo(x, y);
            } else {
                gc.lineTo(x, y);
            }
        }
        gc.closePath();
        gc.stroke();

        // ラベルの描画
        if (showLabels) {
            drawOrbitLabel(gc, body);
        }
    }

    private void drawOrbitLabel(GraphicsContext gc, CelestialBody body) {
        if (!body.isVisible()) return;

        // 衛星のラベルは非表示
        if (body.getParent() != null) return;

        // 惑星のラベルを軌道上に配置
        double labelAngle = body.getAngle() + Math.PI/4; // 45度の位置

        // 楕円軌道を考慮した位置計算
        double r = body.getDistance() * (1 - body.getEccentricity() * body.getEccentricity()) /
                  (1 + body.getEccentricity() * Math.cos(labelAngle));
        
        // 軌道傾斜を考慮
        double baseX = r * Math.cos(labelAngle);
        double baseY = r * Math.sin(labelAngle);
        double rotatedX = baseX * Math.cos(body.getInclination());
        double rotatedY = baseY;

        double x = body.getCenterX() + rotatedX;
        double y = body.getCenterY() + rotatedY;

        // 軌道と同じ色（20%明るく）でラベルを描画
        Color orbitColor = body.getColor();
        Color brighterColor = orbitColor.brighter().brighter(); // 20%明るく
        gc.setFill(brighterColor);
        gc.setFont(Font.font("Arial", 12));
        gc.fillText(body.getName(), x, y);
    }

    public void toggleOrbits() {
        showOrbits = !showOrbits;
    }

    public void toggleLabels() {
        showLabels = !showLabels;
    }

    public boolean isShowingOrbits() {
        return showOrbits;
    }

    public boolean isShowingLabels() {
        return showLabels;
    }
} 