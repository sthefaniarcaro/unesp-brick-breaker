package brickbreaker;

public class Platform {
    private static final int DEFAULT_LIVES = 3;
    private static final int DEFAULT_SCORE = 0;

    private int x;
    private final int y;
    private final int width;
    private final int height;
    private int lives;
    private int score;

    public Platform(int initialX, int y, int width, int height) {
        this.x = initialX;
        this.y = y;
        this.width = width;
        this.height = height;
        this.lives = DEFAULT_LIVES;
        this.score = DEFAULT_SCORE;
    }

    public void setX(int newX, int panelWidth) {
        if (newX < 0) {
            x = 0;
        } else if (newX + width > panelWidth) {
            x = panelWidth - width;
        } else {
            x = newX;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        score += points;
    }

    public int getLives() {
        return lives;
    }

    public void loseLife() {
        if (lives > 0)
            lives--;
    }

    public void gainLife() {
        lives++;
    }

    public void reset() {
        x = 0;
        lives = 3;
        score = 0;
    }
}
