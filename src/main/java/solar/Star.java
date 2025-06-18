package solar;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

public class Star extends CelestialBody {
    private static final Color STAR_COLOR = Color.YELLOW;
    private static final Color GLOW_COLOR = Color.rgb(255, 200, 0, 0.3);

    public Star(String name, double x, double y, double radius) {
        super(name, x, y, radius);
        this.color = STAR_COLOR;
    }

    @Override
    public void draw(GraphicsContext gc) {
        // 光る効果を描画
        RadialGradient gradient = new RadialGradient(
            0, 0, x, y, radius * 2,
            false, javafx.scene.paint.CycleMethod.NO_CYCLE,
            new Stop(0, STAR_COLOR),
            new Stop(1, GLOW_COLOR)
        );
        gc.setFill(gradient);
        gc.fillOval(x - radius * 2, y - radius * 2, radius * 4, radius * 4);

        // 中心の星を描画
        gc.setFill(STAR_COLOR);
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
    }
} 