package solar;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Moon extends CelestialBody {
    private static final Color MOON_COLOR = Color.rgb(200, 200, 200);
    private Planet parent;  // 親となる惑星（地球）
    private double angle;   // 親惑星からの角度

    public Moon(String name, double distance, double radius, Planet parent) {
        super(name, 0, 0, radius);
        this.parent = parent;
        this.color = MOON_COLOR;
        this.speed = 0.02;  // 月は惑星より速く回転
        this.angle = 0;
        this.x = parent.getX() + distance;  // 初期位置を設定
        this.y = parent.getY();
    }

    @Override
    public void update() {
        // 親惑星（地球）の位置を基準に月の位置を更新
        angle += speed;
        double distance = 20;  // 地球からの距離
        x = parent.getX() + Math.cos(angle) * distance;
        y = parent.getY() + Math.sin(angle) * distance;
    }

    @Override
    public void draw(GraphicsContext gc) {
        // 月の表面のクレーター効果を描画
        gc.setFill(color);
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);

        // クレーターを描画
        gc.setFill(Color.rgb(180, 180, 180));
        gc.fillOval(x - radius * 0.3, y - radius * 0.3, radius * 0.6, radius * 0.6);
        gc.fillOval(x + radius * 0.2, y + radius * 0.2, radius * 0.4, radius * 0.4);
    }

    public Planet getParent() {
        return parent;
    }
} 