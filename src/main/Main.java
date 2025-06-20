package brickbreaker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main extends JPanel implements MouseMotionListener, KeyListener {
    private Platform platform;
    private Ball ball;
    private BrickManager brickManager;
    private Phase currentPhase;
    private Timer timer;
    private boolean gameStarted = false;
    private boolean gameOver = false;
    private boolean gameWon = false;
    private int phase = 1;
    private static int currentDelay = 12;
    private static final int MIN_DELAY = 8;

    public Main() {
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.BLACK);
        platform = new Platform(350, 550, 100, 20);
        ball = new Ball(390, 300, 20, 3, 3);
        currentPhase = new Phase1();
        brickManager = currentPhase.createBricks(800);
        addMouseMotionListener(this);
        setFocusable(true);
        addKeyListener(this);

        timer = new Timer(currentDelay, _ -> {
            if (gameStarted)
                gameLoop();
        });
        timer.start();
    }

    private void gameLoop() {
        if (gameOver)
            return;

        ball.move();

        // Ball collision with walls
        if (ball.getX() <= 0 || ball.getX() + ball.getDiameter() >= getWidth()) {
            ball.reverseX();
        }
        if (ball.getY() <= 0) {
            ball.reverseY();
        }

        // Ball collision with platform
        Rectangle ballRect = new Rectangle(ball.getX(), ball.getY(), ball.getDiameter(), ball.getDiameter());
        Rectangle platformRect = new Rectangle(platform.getX(), platform.getY(), platform.getWidth(),
                platform.getHeight());
        if (ballRect.intersects(platformRect)) {
            ball.reverseY();

            // Increase speed (decrease delay)
            if (currentDelay > MIN_DELAY) {
                currentDelay--;
                timer.setDelay(currentDelay);
            }
        }

        // Ball collision with bricks
        Brick hitBrick = brickManager.checkCollision(ballRect);
        if (hitBrick != null) {
            ball.reverseY();
            platform.addScore(10);
        }

        if (brickManager.allDestroyed()) {
            if (phase < 3) {
                phase++;
                ball = new Ball(390, 300, 20, 3, 3);
                if (phase == 2)
                    currentPhase = new Phase2();
                else if (phase == 3)
                    currentPhase = new Phase3();
                brickManager = currentPhase.createBricks(800);
                currentDelay = 12; // Reset speed for new phase
                timer.setDelay(currentDelay);
            } else {
                // Win after phase 3
                gameWon = true;
                timer.stop();
            }
            repaint();
            return;
        }

        // Ball touches bottom
        if (ball.getY() + ball.getDiameter() >= getHeight()) {
            platform.loseLife();
            ball.reset(390, 300);

            currentDelay = 12;
            timer.setDelay(currentDelay);

            if (platform.getLives() <= 0) {
                gameOver = true;
            }
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.CYAN);
        g.fillRect(platform.getX(), platform.getY(), platform.getWidth(), platform.getHeight());

        g.setColor(Color.RED);
        g.fillOval(ball.getX(), ball.getY(), ball.getDiameter(), ball.getDiameter());

        brickManager.draw(g);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Lives: " + platform.getLives(), 0, 20);
        g.drawString("Score: " + platform.getScore(), 120, 20);
        g.drawString("Phase: " + phase, 240, 20);

        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("Game Over", 290, 270);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Press SPACE to restart", 220, 320);
        } else if (gameWon && phase == 3) {
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("You Win!!!", 290, 250);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Score: " + platform.getScore(), 320, 290);
            g.drawString("Press SPACE to reset", 230, 340);
        } else if (gameWon) {
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("You Win!", 320, 270);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Press SPACE for next phase", 200, 320);
        } else if (!gameStarted) {
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("Press SPACE to start", 220, 300);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int mouseX = e.getX() - platform.getWidth() / 2;
        platform.setX(mouseX, getWidth());
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                platform = new Platform(350, 550, 100, 20);
                ball = new Ball(390, 300, 20, 3, 3);
                currentPhase = new Phase1();
                brickManager = currentPhase.createBricks(800);
                gameOver = false;
                gameStarted = true;
                gameWon = false;
                phase = 1;
                currentDelay = 12; // Reset speed on restart
                timer.setDelay(currentDelay);
                repaint();
            } else if (!gameStarted) {
                gameStarted = true;
                repaint();
            } else if (gameWon) {
                // Reset after winning all phases
                platform = new Platform(350, 550, 100, 20);
                ball = new Ball(390, 300, 20, 3, 3);
                currentPhase = new Phase1();
                brickManager = currentPhase.createBricks(800);
                gameOver = false;
                gameStarted = false;
                gameWon = false;
                phase = 1;
                currentDelay = 12;
                timer.setDelay(currentDelay);
                repaint();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Brick Breaker");
        Main panel = new Main();

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        panel.requestFocusInWindow();
    }
}