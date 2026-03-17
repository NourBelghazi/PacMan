import java.awt.event.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class Pacman extends JPanel implements ActionListener, KeyListener {

    class Block {
        int x, y, height, width;
        Image image;
        Image normalImage;
        int startX, startY;
        int respawnTicks = 0;
        char direction = 'U';
        int velocityX  = 0;
        int velocityY  = 0;

        Block(Image image, int x, int y, int height, int width) {
            this.image       = image;
            this.normalImage = image;
            this.x = x; this.y = y;
            this.height = height; this.width = width;
            this.startX = x; this.startY = y;
        }

        void updateDirection(char newDirection) {
            char previousDirection = this.direction;
            this.direction = newDirection;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = previousDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            if (this.direction == 'U') { velocityY = -tileSize / 4; velocityX = 0; }
            if (this.direction == 'D') { velocityY =  tileSize / 4; velocityX = 0; }
            if (this.direction == 'R') { velocityX =  tileSize / 4; velocityY = 0; }
            if (this.direction == 'L') { velocityX = -tileSize / 4; velocityY = 0; }
        }

        void reset() { x = startX; y = startY; }
    }

    private static final int ROW_COUNT    = 21;
    private static final int COLUMN_COUNT = 19;
    private final int tileSize    = 32;
    private final int boardWidth  = tileSize * COLUMN_COUNT;
    private final int boardHeight = tileSize * ROW_COUNT;

    private static final String[] TILE_MAP = {
        "XXXXXXXXXXXXXXXXXXX",
        "XW       X       WX",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "O       bpo       O",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "XW               WX",
        "XXXXXXXXXXXXXXXXXXX"
    };

    private Image wallImage;
    private Image blueGhostImage, redGhostImage, pinkGhostImage, orangeGhostImage;
    private Image scaredGhostImage;
    private Image pacmanUpImage, pacmanDownImage, pacmanLeftImage, pacmanRightImage;
    private Image powerFoodImage;
    private Image cherryImage;

    private HashSet<Block> walls;
    private HashSet<Block> foods;
    private HashSet<Block> powerPellets;
    private HashSet<Block> ghosts;
    private Block pacman;
    private Block cherry      = null;
    private int   cherryTicks = 0;
    private int   totalFood      = 0;
    private int   foodEatenCount = 0;

    private Timer  gameLoop;
    private final char[] directions = {'U', 'D', 'L', 'R'};
    private final Random random     = new Random();

    private int     score                = 0;
    private int     highScore            = 0;
    private int     lives                = 3;
    private int     level                = 1;
    private boolean gameOver             = false;
    private boolean paused               = false;
    private int     powerModeTicks       = 0;
    private int     ghostsEatenThisPower = 0;

    private static final int POWER_DURATION   = 300;
    private static final int GHOST_RESPAWN    = 80;
    private static final int GHOST_BASE_SCORE = 200;
    private static final int CHERRY_DURATION  = 200;
    private static final int CHERRY_SCORE     = 100;

    public Pacman() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        wallImage        = new ImageIcon(getClass().getResource("/images/wall.png")).getImage();
        blueGhostImage   = new ImageIcon(getClass().getResource("/images/blueGhost.png")).getImage();
        redGhostImage    = new ImageIcon(getClass().getResource("/images/redGhost.png")).getImage();
        pinkGhostImage   = new ImageIcon(getClass().getResource("/images/pinkGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("/images/orangeGhost.png")).getImage();
        scaredGhostImage = new ImageIcon(getClass().getResource("/images/scaredGhost.png")).getImage();
        pacmanUpImage    = new ImageIcon(getClass().getResource("/images/pacmanUp.png")).getImage();
        pacmanDownImage  = new ImageIcon(getClass().getResource("/images/pacmanDown.png")).getImage();
        pacmanLeftImage  = new ImageIcon(getClass().getResource("/images/pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("/images/pacmanRight.png")).getImage();
        powerFoodImage   = new ImageIcon(getClass().getResource("/images/powerFood.png")).getImage();
        cherryImage      = new ImageIcon(getClass().getResource("/images/cherry.png")).getImage();

        loadMap();
        randomizeGhostDirections();
        gameLoop = new Timer(50, this);
        gameLoop.start();
    }

    public void loadMap() {
        walls        = new HashSet<>();
        foods        = new HashSet<>();
        powerPellets = new HashSet<>();
        ghosts       = new HashSet<>();
        cherry         = null;
        cherryTicks    = 0;
        foodEatenCount = 0;
        powerModeTicks = 0;

        for (int r = 0; r < ROW_COUNT; r++) {
            for (int c = 0; c < COLUMN_COUNT; c++) {
                char tile = TILE_MAP[r].charAt(c);
                int x = c * tileSize;
                int y = r * tileSize;

                if (tile == 'X') walls.add(new Block(wallImage, x, y, tileSize, tileSize));
                if (tile == 'b') ghosts.add(new Block(blueGhostImage,   x, y, tileSize, tileSize));
                if (tile == 'o') ghosts.add(new Block(orangeGhostImage, x, y, tileSize, tileSize));
                if (tile == 'p') ghosts.add(new Block(pinkGhostImage,   x, y, tileSize, tileSize));
                if (tile == 'r') ghosts.add(new Block(redGhostImage,    x, y, tileSize, tileSize));
                if (tile == 'P') pacman = new Block(pacmanRightImage,   x, y, tileSize, tileSize);
                if (tile == ' ') foods.add(new Block(null, x + 14, y + 14, 4, 4));
                if (tile == 'W') {
                    int pelletSize = tileSize - 16;
                    powerPellets.add(new Block(powerFoodImage, x + 8, y + 8, pelletSize, pelletSize));
                }
            }
        }
        totalFood = foods.size();
    }

    private void randomizeGhostDirections() {
        for (Block ghost : ghosts) {
            ghost.updateDirection(directions[random.nextInt(4)]);
        }
    }

    private void spawnCherry() {
        cherry      = new Block(cherryImage, 9 * tileSize, 3 * tileSize, tileSize, tileSize);
        cherryTicks = CHERRY_DURATION;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }
        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }
        for (Block pp : powerPellets) {
            g.drawImage(pp.image, pp.x, pp.y, pp.width, pp.height, null);
        }
        if (cherry != null) {
            g.drawImage(cherry.image, cherry.x, cherry.y, cherry.width, cherry.height, null);
        }
        for (Block ghost : ghosts) {
            boolean flickering = ghost.respawnTicks > 0 && (ghost.respawnTicks / 5) % 2 == 0;
            if (!flickering) {
                g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
            }
        }
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        drawHUD(g);
    }

    private void drawHUD(Graphics g) {
        if (powerModeTicks > 0) {
            g.setColor(new Color(0, 200, 255));
            int barWidth = (int)((double) powerModeTicks / POWER_DURATION * (boardWidth - tileSize));
            g.fillRect(tileSize / 2, tileSize - 6, barWidth, 5);
        }

        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(Color.WHITE);
        g.drawString("Lives: " + lives,    tileSize / 2,              tileSize / 2);
        g.drawString("Score: " + score,    boardWidth / 3,            tileSize / 2);
        g.drawString("Best: "  + highScore,(int)(boardWidth * 0.62),  tileSize / 2);
        g.drawString("Lvl: "   + level,    boardWidth - tileSize * 2, tileSize / 2);

        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, boardHeight / 2 - 70, boardWidth, 145);

            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            drawCentered(g, "GAME OVER", boardHeight / 2 - 20);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            drawCentered(g, "Score: " + score + "   Best: " + highScore, boardHeight / 2 + 15);

            g.setColor(Color.YELLOW);
            drawCentered(g, "Press ENTER or SPACE to restart", boardHeight / 2 + 45);
        }

        if (paused) {
            g.setColor(new Color(0, 0, 0, 160));
            g.fillRect(0, boardHeight / 2 - 40, boardWidth, 80);
            g.setColor(Color.CYAN);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            drawCentered(g, "PAUSED  –  P to resume", boardHeight / 2 + 10);
        }
    }

    private void drawCentered(Graphics g, String text, int y) {
        int textWidth = g.getFontMetrics().stringWidth(text);
        g.drawString(text, (boardWidth - textWidth) / 2, y);
    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width - 5 &&
               a.x + a.width - 5 > b.x &&
               a.y < b.y + b.height - 5 &&
               a.y + a.height - 5 > b.y;
    }

    private void addScore(int points) {
        score += points;
        if (score > highScore) highScore = score;
    }

    private void activatePowerMode() {
        powerModeTicks       = POWER_DURATION;
        ghostsEatenThisPower = 0;
        for (Block ghost : ghosts) ghost.image = scaredGhostImage;
    }

    private void deactivatePowerMode() {
        for (Block ghost : ghosts) ghost.image = ghost.normalImage;
    }

    private void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        pacman.direction = 'R';
        pacman.image     = pacmanRightImage;
        powerModeTicks   = 0;
        deactivatePowerMode();
        for (Block ghost : ghosts) {
            ghost.reset();
            ghost.respawnTicks = 0;
            ghost.updateDirection(directions[random.nextInt(4)]);
        }
    }

    private void restartGame() {
        score          = 0;
        lives          = 3;
        level          = 1;
        gameOver       = false;
        paused         = false;
        powerModeTicks = 0;
        cherry         = null;
        cherryTicks    = 0;
        loadMap();
        randomizeGhostDirections();
        if (!gameLoop.isRunning()) gameLoop.start();
    }

    public void move() {
        if (paused || gameOver) return;

        movePacman();
        moveGhosts();
        handleGhostCollisions();
        handleFoodCollection();
        handlePowerPelletCollection();
        handleCherryCollection();
        tickTimers();
        checkLevelComplete();
    }

    private void movePacman() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        if      (pacman.x + pacman.width <= 0) pacman.x = boardWidth;
        else if (pacman.x >= boardWidth)        pacman.x = -pacman.width;

        for (Block wall : walls) {
            if (collision(wall, pacman)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
            }
        }
    }

    private void moveGhosts() {
        for (Block ghost : ghosts) {
            if (ghost.y == 9 * tileSize && ghost.x == 4 * tileSize) {
                ghost.updateDirection(directions[random.nextInt(4)]);
            }
            ghost.y += ghost.velocityY;
            ghost.x += ghost.velocityX;

            if      (ghost.x + ghost.width <= 0) ghost.x = boardWidth;
            else if (ghost.x >= boardWidth)       ghost.x = -ghost.width;

            for (Block wall : walls) {
                if (collision(ghost, wall)) {
                    ghost.y -= ghost.velocityY;
                    ghost.x -= ghost.velocityX;
                    ghost.updateDirection(directions[random.nextInt(4)]);
                }
            }
            if (ghost.respawnTicks > 0) {
                ghost.respawnTicks--;
                if (ghost.respawnTicks == 0 && powerModeTicks > 0) ghost.image = scaredGhostImage;
            }
        }
    }

    private void handleGhostCollisions() {
        for (Block ghost : ghosts) {
            if (!collision(ghost, pacman) || ghost.respawnTicks > 0) continue;

            if (powerModeTicks > 0) {
                addScore(GHOST_BASE_SCORE * (1 << ghostsEatenThisPower));
                ghostsEatenThisPower++;
                ghost.reset();
                ghost.image        = ghost.normalImage;
                ghost.respawnTicks = GHOST_RESPAWN;
                ghost.updateDirection(directions[random.nextInt(4)]);
            } else {
                lives--;
                if (lives == 0) { gameOver = true; if (score > highScore) highScore = score; return; }
                resetPositions();
                return;
            }
        }
    }

    private void handleFoodCollection() {
        Block eaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) { eaten = food; break; }
        }
        if (eaten != null) {
            foods.remove(eaten);
            addScore(10);
            foodEatenCount++;
        }
    }

    private void handlePowerPelletCollection() {
        Block eaten = null;
        for (Block pp : powerPellets) {
            if (collision(pacman, pp)) { eaten = pp; break; }
        }
        if (eaten != null) {
            powerPellets.remove(eaten);
            addScore(50);
            activatePowerMode();
        }
    }

    private void handleCherryCollection() {
        if (cherry != null && collision(pacman, cherry)) {
            addScore(CHERRY_SCORE);
            cherry = null; cherryTicks = 0;
        }
        boolean halfFoodEaten = totalFood > 0 && foodEatenCount >= totalFood / 2;
        if (cherry == null && cherryTicks == 0 && halfFoodEaten) spawnCherry();
    }

    private void tickTimers() {
        if (cherryTicks > 0 && --cherryTicks == 0)    cherry = null;
        if (powerModeTicks > 0 && --powerModeTicks == 0) deactivatePowerMode();
    }

    private void checkLevelComplete() {
        if (foods.isEmpty() && powerPellets.isEmpty()) {
            level++;
            loadMap();
            randomizeGhostDirections();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override public void keyTyped(KeyEvent e)   {}
    @Override public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (gameOver) {
            if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_SPACE) restartGame();
            return;
        }
        if (key == KeyEvent.VK_P) { paused = !paused; repaint(); return; }
        if (paused) return;

        switch (key) {
            case KeyEvent.VK_UP:    pacman.updateDirection('U'); pacman.image = pacmanUpImage;    break;
            case KeyEvent.VK_DOWN:  pacman.updateDirection('D'); pacman.image = pacmanDownImage;  break;
            case KeyEvent.VK_RIGHT: pacman.updateDirection('R'); pacman.image = pacmanRightImage; break;
            case KeyEvent.VK_LEFT:  pacman.updateDirection('L'); pacman.image = pacmanLeftImage;  break;
        }
    }
}
