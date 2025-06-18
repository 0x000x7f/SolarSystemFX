package solar;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;

public class Camera {
    private double x = 0;
    private double y = 0;
    private double scale = 1.0;
    private static final double MIN_SCALE = 0.1;
    private static final double MAX_SCALE = 10.0;
    private static final double SCALE_FACTOR = 0.1;
    private static final double PAN_FACTOR = 10.0;
    private static final double FOCUS_SCALE = 2.0;  // フォーカス時のスケール
    private static final double FOCUS_DURATION = 0.5;  // フォーカス移動の時間（秒）
    private CelestialBody focusedBody = null;
    private double targetX = 0;
    private double targetY = 0;
    private double targetScale = 1.0;
    private double focusProgress = 1.0;  // 1.0 = フォーカス完了

    public void apply(GraphicsContext gc) {
        gc.save();
        Affine transform = new Affine();
        transform.appendTranslation(x, y);
        transform.appendScale(scale, scale);
        gc.setTransform(transform);
    }

    public void update() {
        if (focusProgress < 1.0) {
            focusProgress += 0.016;  // 約60FPSを想定
            if (focusProgress > 1.0) focusProgress = 1.0;
            
            // イージング関数を使用して滑らかな移動
            double t = easeInOutCubic(focusProgress);
            x = lerp(x, targetX, t);
            y = lerp(y, targetY, t);
            scale = lerp(scale, targetScale, t);
        }
    }

    public void focusOn(CelestialBody body) {
        if (body == null) {
            reset();
            return;
        }
        
        focusedBody = body;
        targetX = -body.getX();
        targetY = -body.getY();
        targetScale = FOCUS_SCALE;
        focusProgress = 0.0;
    }

    public void handleKeyPress(String key) {
        switch (key) {
            case "UP":
                y += PAN_FACTOR;
                break;
            case "DOWN":
                y -= PAN_FACTOR;
                break;
            case "LEFT":
                x += PAN_FACTOR;
                break;
            case "RIGHT":
                x -= PAN_FACTOR;
                break;
            case "PLUS":
            case "EQUALS":
                scale = Math.min(scale + SCALE_FACTOR, MAX_SCALE);
                break;
            case "MINUS":
                scale = Math.max(scale - SCALE_FACTOR, MIN_SCALE);
                break;
            case "R":
                reset();
                break;
            case "F":
                if (focusedBody != null) {
                    focusOn(focusedBody);  // 現在のフォーカス天体に再度フォーカス
                }
                break;
        }
    }

    public void handleMouseDrag(double dx, double dy) {
        x += dx;
        y += dy;
        focusedBody = null;  // ドラッグでフォーカス解除
    }

    public void handleScroll(double delta) {
        double newScale = scale + (delta > 0 ? SCALE_FACTOR : -SCALE_FACTOR);
        scale = Math.max(MIN_SCALE, Math.min(newScale, MAX_SCALE));
        focusedBody = null;  // ズームでフォーカス解除
    }

    public void reset() {
        x = 0;
        y = 0;
        scale = 1.0;
        focusedBody = null;
        focusProgress = 1.0;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getScale() {
        return scale;
    }

    public CelestialBody getFocusedBody() {
        return focusedBody;
    }

    // 補助メソッド
    private double lerp(double start, double end, double t) {
        return start + (end - start) * t;
    }

    private double easeInOutCubic(double t) {
        return t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;
    }
} 