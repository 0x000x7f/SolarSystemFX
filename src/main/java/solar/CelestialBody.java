package solar;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class CelestialBody {
    protected String name;
    protected double x;
    protected double y;
    protected double radius;
    protected double angle;
    protected double speed;
    protected double distance;  // 中心からの距離
    protected double centerX;   // 公転の中心X座標
    protected double centerY;   // 公転の中心Y座標
    protected Color color;
    protected double eccentricity = 0.0;  // 軌道離心率
    protected double inclination = 0.0;   // 軌道傾斜角（ラジアン）
    protected boolean visible = true;

    public CelestialBody(String name, double distance, double angle, double radius) {
        this.name = name;
        this.distance = distance;
        this.angle = angle;
        this.radius = radius;
        this.speed = 0.01;
        this.color = Color.WHITE;
        this.centerX = 0;
        this.centerY = 0;
        updatePosition();
    }

    public void update() {
        if (!visible) return;
        updatePosition();
    }

    public void updatePosition() {
        // 軌道上の位置を計算
        double r = distance * (1 - eccentricity * eccentricity) /
                  (1 + eccentricity * Math.cos(angle));
        
        // 軌道傾斜を考慮した位置計算
        double baseX = r * Math.cos(angle);
        double baseY = r * Math.sin(angle);
        double rotatedX = baseX * Math.cos(inclination);
        double rotatedY = baseY;

        // 中心位置を加算
        x = centerX + rotatedX;
        y = centerY + rotatedY;

        // 角度を更新（速度に基づいて）
        angle += speed;
        if (angle > 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
    }

    public void draw(GraphicsContext gc) {
        if (!visible) return;
        gc.setFill(color);
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    public void setCenter(double centerX, double centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
        updatePosition();
    }

    public String getName() {
        return name;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public double getDistance() {
        return distance;
    }

    public double getAngle() {
        return angle;
    }

    public Color getColor() {
        return color;
    }

    public double getEccentricity() {
        return eccentricity;
    }

    public double getInclination() {
        return inclination;
    }

    public CelestialBody getParent() {
        return null;  // デフォルトでは親を持たない
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setEccentricity(double eccentricity) {
        this.eccentricity = eccentricity;
    }

    public void setInclination(double inclination) {
        this.inclination = inclination;
    }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean v) { this.visible = v; }
    public void toggleVisible() { this.visible = !this.visible; }
} 