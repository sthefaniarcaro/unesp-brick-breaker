package main;

import java.awt.*;

public class Brick {

    private int x;
    private int y;
    private int width;
    private int height;

    // Variáveis corretas: 'health' para vida e 'color' para a cor
    private int health;
    private Color color;

    // Construtor que aceita a vida e a cor do tijolo
    public Brick(int x, int y, int width, int height, int health, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.health = health;
        this.color = color;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Método que é chamado quando a bola acerta o tijolo
    public void hit() {
        if (health > 0) {
            health--;
        }
    }

    // Verifica se a vida do tijolo chegou a zero
    public boolean isDestroyed() {
        return health <= 0;
    }

    // Desenha o tijolo com cores diferentes dependendo da vida
    public void draw(Graphics g) {
        if (!isDestroyed()) { // Só desenha se a vida for maior que 0

            // Define a cor com base na vida restante
            if (health == 2) {
                g.setColor(this.color.darker()); // Cor mais escura para vida 2
            } else if (health == 1) {
                g.setColor(new Color(148, 0, 211)); // Cor normal para vida 1 (rachado)
            }

            g.fillRect(x, y, width, height);

            // Desenha uma borda preta
            g.setColor(new Color(186, 85, 211));
            g.drawRect(x, y, width, height);
        }
    }
}
