package pacman;

import pacman.engine.GameEngine;
import pacman.input.InputHandler;
import pacman.renderer.GameRenderer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements ActionListener {

    private final GameEngine   engine;
    private final GameRenderer renderer;
    private final Timer        gameLoop;

    public GamePanel() {
        engine   = new GameEngine();
        renderer = new GameRenderer(engine);

        setPreferredSize(new Dimension(GameConstants.BOARD_WIDTH, GameConstants.BOARD_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new InputHandler(engine));

        gameLoop = new Timer(50, this);
        gameLoop.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.draw(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        engine.move();
        repaint();
    }
}
