package pacman.input;

import pacman.engine.GameEngine;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputHandler extends KeyAdapter {

    private final GameEngine engine;

    public InputHandler(GameEngine engine) {
        this.engine = engine;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (engine.isGameOver()) {
            if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_SPACE) engine.restartGame();
            return;
        }

        if (key == KeyEvent.VK_P) { engine.togglePause(); return; }

        if (engine.isPaused()) return;

        switch (key) {
            case KeyEvent.VK_UP:    engine.changePacmanDirection('U'); break;
            case KeyEvent.VK_DOWN:  engine.changePacmanDirection('D'); break;
            case KeyEvent.VK_RIGHT: engine.changePacmanDirection('R'); break;
            case KeyEvent.VK_LEFT:  engine.changePacmanDirection('L'); break;
        }
    }
}
