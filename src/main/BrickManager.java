package main;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BrickManager {

    private final List<Brick> bricks = new ArrayList<>();

    public BrickManager(int rows, int cols, int panelWidth) {
        Random rand = new Random(); // Crie uma instância do Random

        int brickWidth = 60;
        int brickHeight = 25;
        int spacing = 10;
        int totalWidth = cols * brickWidth + (cols - 1) * spacing;
        int startX = (panelWidth - totalWidth) / 2;
        int startY = 60;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = startX + col * (brickWidth + spacing);
                int y = startY + row * (brickHeight + spacing);

                if (rand.nextInt(100) < 25) { // 25% de chance de ser um tijolo forte
                    // Tijolo forte (vida 2)
                    bricks.add(new Brick(x, y, brickWidth, brickHeight, 2, new Color(128, 0, 128)));
                } else {
                    // Tijolo normal (vida 1)
                    bricks.add(new Brick(x, y, brickWidth, brickHeight, 1, new Color(148, 0, 211)));
                }
            }
        }
    }

    public void draw(Graphics g) {
        for (Brick brick : bricks) {
            brick.draw(g);
        }
    }

    public Brick checkCollision(Rectangle ballRect) {
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && brick.getBounds().intersects(ballRect)) {
                return brick;
            }
        }
        return null;
    }

    public boolean allDestroyed() {
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                return false;
            }
        }
        return true;
    }
}
