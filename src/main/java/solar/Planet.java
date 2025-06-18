package solar;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class Planet extends CelestialBody {
    private List<Moon> moons;
    private Color atmosphereColor;

    public Planet(String name, double x, double y, double radius) {
        super(name, x, y, radius);
        this.moons = new ArrayList<>();
        this.atmosphereColor = Color.rgb(200, 200, 255, 0.2);
        setPlanetColor(name);
    }

    private void setPlanetColor(String name) {
        switch (name.toLowerCase()) {
            case "mercury":
                color = Color.GRAY;
                break;
            case "venus":
                color = Color.rgb(255, 200, 100);
                break;
            case "earth":
                color = Color.rgb(100, 150, 255);
                break;
            case "mars":
                color = Color.rgb(255, 100, 100);
                break;
            case "jupiter":
                color = Color.rgb(255, 200, 100);
                break;
            case "saturn":
                color = Color.rgb(255, 220, 150);
                break;
            case "uranus":
                color = Color.rgb(200, 255, 255);
                break;
            case "neptune":
                color = Color.rgb(100, 100, 255);
                break;
            default:
                color = Color.WHITE;
        }
    }

    public void addMoon(Moon moon) {
        moons.add(moon);
    }

    @Override
    public void draw(GraphicsContext gc) {
        // 大気圏を描画
        gc.setFill(atmosphereColor);
        gc.fillOval(x - radius * 1.2, y - radius * 1.2, radius * 2.4, radius * 2.4);

        // 惑星本体を描画
        super.draw(gc);

        // 月を描画
        for (Moon moon : moons) {
            moon.draw(gc);
        }
    }

    @Override
    public void update() {
        super.update();
        for (Moon moon : moons) {
            moon.update();
        }
    }

    public List<Moon> getMoons() {
        return moons;
    }
} 