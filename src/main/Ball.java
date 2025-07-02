package main;

public class Ball {

    private int x;
    private int y;
    private int diameter;
    private int dx;
    private int dy;

    public Ball(int x, int y, int diameter, int dx, int dy) {
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        this.dx = dx;
        this.dy = dy;
    }

    public void move() {
        x += dx;
        y += dy;
    }

    public void reset(int startX, int startY) {
        x = startX;
        y = startY;
    }

    public void reverseX() {
        dx = -dx;
    }

    public void reverseY() {
        dy = -dy;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDiameter() {
        return diameter;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }
}
