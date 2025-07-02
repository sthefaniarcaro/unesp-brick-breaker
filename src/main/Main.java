package main;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class Main extends JPanel implements MouseMotionListener, KeyListener {

    private Random random = new Random();
    private Platform platform;
    private Ball ball;
    private BrickManager brickManager;
    private Phase currentPhase;
    private Timer timer;
    private boolean gameStarted = false;
    private boolean gameOver = false;
    private boolean gameWon = false;
    private int phase = 1;
    private boolean isPaused = false;
    private static int currentDelay = 12;
    private static final int MIN_DELAY = 8;

    public Main() {
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.BLACK);
        platform = new Platform(350, 550, 100, 20);
        int speedX = random.nextBoolean() ? 3 : -3;
        ball = new Ball(390, 300, 20, speedX, -3);
        currentPhase = new Phase1();
        brickManager = currentPhase.createBricks(800);
        addMouseMotionListener(this);
        setFocusable(true);
        addKeyListener(this);

        timer = new Timer(currentDelay, e -> {
            if (gameStarted) {
                gameLoop();
            }
        });
        timer.start();
    }

    private void gameLoop() {
        if (gameOver) {
            return;
        }

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

            int platformCenter = platform.getX() + platform.getWidth() / 2;
            int ballCenter = ball.getX() + ball.getDiameter() / 2;

            int collisionPoint = ballCenter - platformCenter;

            // O valor '5' controla o quão "sensível" é o ângulo. Pode ajustar.
            int newDx = (int) (collisionPoint * 0.15);

            final int MAX_SPEED_X = 5;
            if (newDx > MAX_SPEED_X) {
                newDx = MAX_SPEED_X;
            }
            if (newDx < -MAX_SPEED_X) {
                newDx = -MAX_SPEED_X;
            }

            if (newDx == 0) {
                newDx = 1;
            }

            ball.setDx(newDx);

            if (currentDelay > MIN_DELAY) {
                currentDelay--;
                timer.setDelay(currentDelay);
            }
        }

        // Ball collision with bricks
        Brick hitBrick = brickManager.checkCollision(ballRect);
        if (hitBrick != null) {
            hitBrick.hit();
            ball.reverseY();
            platform.addScore(10);
        }

        if (brickManager.allDestroyed()) {
            if (phase < 3) {
                phase++;
                int speedX = random.nextBoolean() ? 3 : -3;
                ball = new Ball(390, 300, 20, speedX, -3);
                if (phase == 2) {
                    currentPhase = new Phase2();
                } else if (phase == 3) {
                    currentPhase = new Phase3();
                }
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
            platform.removeScore(10);
            if (platform.getLives() > 0) {
                int speedX = random.nextBoolean() ? 3 : -3;
                ball = new Ball(390, 300, 20, speedX, -3);
            }

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
        g.setColor(new Color(106, 90, 205));
        g.fillRect(platform.getX(), platform.getY(), platform.getWidth(), platform.getHeight());
        if (gameStarted) {
            g.setColor(new Color(255, 215, 0));
            g.fillOval(ball.getX(), ball.getY(), ball.getDiameter(), ball.getDiameter());
        }

        brickManager.draw(g);

        Font fontHUD = new Font("Arial", Font.BOLD, 18);
        int hudY = 25; // Posição vertical do placar no topo da tela

        g.setColor(Color.WHITE);
        g.setFont(fontHUD);

        String livesText = "Vidas: " + platform.getLives();
        g.drawString(livesText, 20, hudY);
        String phaseText = "Fase: " + phase;
        drawCenteredString(g, phaseText, fontHUD, hudY);
        String scoreText = "Score: " + platform.getScore();
        FontMetrics metrics = g.getFontMetrics(fontHUD);
        int scoreWidth = metrics.stringWidth(scoreText);
        g.drawString(scoreText, getWidth() - scoreWidth - 20, hudY);
        if (gameStarted && !isPaused && !gameOver && !gameWon) {
            Font fontDica = new Font("Arial", Font.PLAIN, 12);
            g.setFont(fontDica);

            g.setColor(Color.GRAY);

            String dicaPausa = "Pressione ESPAÇO para pausar";

            // Desenha o texto no canto inferior esquerdo
            // X = 10 (margem esquerda)
            // Y = getHeight() - 10 (margem de baixo para cima)
            g.drawString(dicaPausa, 10, getHeight() - 10);
        } else if (gameOver) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());

            Font fontGameOver = new Font("Arial", Font.BOLD, 60);
            Font fontReset = new Font("Arial", Font.ITALIC, 20);
            Color corDerrota = new Color(178, 34, 34);

            g.setColor(corDerrota);
            drawCenteredString(g, "GAME OVER", fontGameOver, 270);

            g.setColor(Color.WHITE);

            drawCenteredString(g, "Pressione ESPAÇO para reiniciar", fontReset, 320);
        } else if (isPaused) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());

            Font fontPausa = new Font("Arial", Font.BOLD, 50);
            g.setColor(Color.WHITE);
            drawCenteredString(g, "JOGO PAUSADO", fontPausa, getHeight() / 2);

        } else if (gameWon && phase == 3) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());

            Font fontVitoria = new Font("Arial", Font.BOLD, 60);
            Font fontScore = new Font("Arial", Font.PLAIN, 28);
            Font fontReset = new Font("Arial", Font.ITALIC, 20);
            Color corDourada = new Color(255, 215, 0); // Um tom de dourado

            g.setColor(corDourada);
            drawCenteredString(g, "VOCÊ VENCEU!", fontVitoria, 250);

            g.setColor(Color.WHITE);
            drawCenteredString(g, "Score: " + platform.getScore(), fontScore, 310);
            drawCenteredString(g, "Pressione ESPAÇO para reiniciar", fontReset, 350);

        } else if (!gameStarted) {
            Font fontTitulo = new Font("Arial", Font.BOLD, 60);
            Font fontSubtitulo = new Font("Arial", Font.PLAIN, 22);

            g.setColor(Color.WHITE);
            drawCenteredString(g, "BRICK BREAKER", fontTitulo, getHeight() / 2 - 40);

            g.setColor(Color.LIGHT_GRAY);
            drawCenteredString(g, "Pressione ESPAÇO para iniciar", fontSubtitulo, getHeight() / 2 + 20);
        }
    }

    private void drawCenteredString(Graphics g, String text, Font font, int y) {
        FontMetrics metrics = g.getFontMetrics(font);
        int x = (getWidth() - metrics.stringWidth(text)) / 2; // Calcula o X para centralizar
        g.setFont(font);
        g.drawString(text, x, y);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (gameStarted && !isPaused && !gameOver && !gameWon) {
            int mouseX = e.getX() - platform.getWidth() / 2;
            platform.setX(mouseX, getWidth());
            repaint();
        }
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
                int speedX = random.nextBoolean() ? 3 : -3;
                ball = new Ball(390, 300, 20, speedX, -3);
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
                int speedX = random.nextBoolean() ? 3 : -3;
                ball = new Ball(390, 300, 20, speedX, -3);
                currentPhase = new Phase1();
                brickManager = currentPhase.createBricks(800);
                gameOver = false;
                gameStarted = false;
                gameWon = false;
                phase = 1;
                currentDelay = 12;
                timer.setDelay(currentDelay);
                repaint();
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {

                if (gameStarted && !gameOver && !gameWon) {
                    if (isPaused) {

                        isPaused = false;
                        timer.start();
                    } else {

                        isPaused = true;
                        timer.stop();
                    }
                    repaint();
                }
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
