package main;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BrickManager {
    private List<Brick> bricks = new ArrayList<>();

    public BrickManager(int rows, int cols, int panelWidth) {
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
                bricks.add(new Brick(x, y, brickWidth, brickHeight));
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
                brick.setDestroyed(true);
                return brick;
            }
        }
        return null;
    }

    public boolean allDestroyed() {
        for (Brick brick : bricks) {
            if (!brick.isDestroyed())
                return false;
        }
        return true;
    }
}