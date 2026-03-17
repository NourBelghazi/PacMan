package pacman.renderer;

import pacman.GameConstants;
import pacman.engine.GameEngine;
import pacman.entity.Block;
import java.awt.*;

public class GameRenderer {

    private final GameEngine engine;

    public GameRenderer(GameEngine engine) {
        this.engine = engine;
    }

    public void draw(Graphics g) {
        for (Block wall : engine.getWalls()) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.WHITE);
        for (Block food : engine.getFoods()) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        for (Block pp : engine.getPowerPellets()) {
            g.drawImage(pp.image, pp.x, pp.y, pp.width, pp.height, null);
        }

        Block cherry = engine.getCherry();
        if (cherry != null) {
            g.drawImage(cherry.image, cherry.x, cherry.y, cherry.width, cherry.height, null);
        }

        for (Block ghost : engine.getGhosts()) {
            boolean flickering = ghost.respawnTicks > 0 && (ghost.respawnTicks / 5) % 2 == 0;
            if (!flickering) {
                g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
            }
        }

        Block pacman = engine.getPacman();
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        drawHUD(g);
    }

    private void drawHUD(Graphics g) {
        int ts = GameConstants.TILE_SIZE;
        int bw = GameConstants.BOARD_WIDTH;
        int bh = GameConstants.BOARD_HEIGHT;

        if (engine.getPowerModeTicks() > 0) {
            g.setColor(new Color(0, 200, 255));
            int barWidth = (int)((double) engine.getPowerModeTicks() / GameConstants.POWER_DURATION * (bw - ts));
            g.fillRect(ts / 2, ts - 6, barWidth, 5);
        }

        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(Color.WHITE);
        g.drawString("Lives: " + engine.getLives(),     ts / 2,           ts / 2);
        g.drawString("Score: " + engine.getScore(),     bw / 3,           ts / 2);
        g.drawString("Best: "  + engine.getHighScore(), (int)(bw * 0.62), ts / 2);
        g.drawString("Lvl: "   + engine.getLevel(),     bw - ts * 2,      ts / 2);

        if (engine.isGameOver()) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, bh / 2 - 70, bw, 145);

            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            drawCentered(g, "GAME OVER", bh / 2 - 20);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            drawCentered(g, "Score: " + engine.getScore() + "   Best: " + engine.getHighScore(), bh / 2 + 15);

            g.setColor(Color.YELLOW);
            drawCentered(g, "Press ENTER or SPACE to restart", bh / 2 + 45);
        }

        if (engine.isPaused()) {
            g.setColor(new Color(0, 0, 0, 160));
            g.fillRect(0, bh / 2 - 40, bw, 80);
            g.setColor(Color.CYAN);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            drawCentered(g, "PAUSED  –  P to resume", bh / 2 + 10);
        }
    }

    private void drawCentered(Graphics g, String text, int y) {
        int textWidth = g.getFontMetrics().stringWidth(text);
        g.drawString(text, (GameConstants.BOARD_WIDTH - textWidth) / 2, y);
    }
}
