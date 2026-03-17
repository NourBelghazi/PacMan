import java.awt.event.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class Pacman extends JPanel implements ActionListener, KeyListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (gameOver) {
            if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_SPACE) restartGame();
            return;
        }

        if (key == KeyEvent.VK_P) {
            paused = !paused;
            repaint();
            return;
        }

        if (paused) return;

        switch (key) {
            case KeyEvent.VK_UP:    pacman.updateDirection('U'); pacman.image = pacmanUpImage;    break;
            case KeyEvent.VK_DOWN:  pacman.updateDirection('D'); pacman.image = pacmanDownImage;  break;
            case KeyEvent.VK_RIGHT: pacman.updateDirection('R'); pacman.image = pacmanRightImage; break;
            case KeyEvent.VK_LEFT:  pacman.updateDirection('L'); pacman.image = pacmanLeftImage;  break;
        }
    }

    private void restartGame() {
        score          = 0;
        lives          = 3;
        gameOver       = false;
        paused         = false;
        powerModeTicks = 0;
        cherry         = null;
        cherryTicks    = 0;
        loadMap();
        for (Block ghost : ghosts) {
            ghost.updateDirection(directions[random.nextInt(4)]);
        }
        if (!gameLoop.isRunning()) gameLoop.start();
    }

    class Block {
        int x, y, height, width;
        Image image;
        Image normalImage;
        int startX, startY;
        int respawnTicks = 0;
        char direction = 'U';
        int velocityX = 0;
        int velocityY = 0;

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
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

        void reset() {
            x = startX;
            y = startY;
        }

        Block(Image image, int x, int y, int height, int width) {
            this.image       = image;
            this.normalImage = image;
            this.x = x; this.y = y;
            this.height = height; this.width = width;
            this.startX = x; this.startY = y;
        }
    }

    private int rowCount    = 21;
    private int columnCount = 19;
    private int tileSize    = 32;
    private int boardWidth  = tileSize * columnCount;
    private int boardHeight = tileSize * rowCount;

    private Image wallImage;
    private Image blueGhostImage, redGhostImage, pinkGhostImage, orangeGhostImage;
    private Image scaredGhostImage;
    private Image pacmanUpImage, pacmanDownImage, pacmanLeftImage, pacmanRightImage;
    private Image powerFoodImage;
    private Image cherryImage;

    private String[] tileMap = {
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

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> powerPellets;
    HashSet<Block> ghosts;
    Block pacman;
    Block cherry      = null;
    int   cherryTicks = 0;
    int   totalFood      = 0;
    int   foodEatenCount = 0;

    private static final int CHERRY_DURATION = 200;
    private static final int CHERRY_SCORE    = 100;

    Timer  gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'};
    Random random     = new Random();

    int     score            = 0;
    int     lives            = 3;
    boolean gameOver         = false;
    boolean paused           = false;
    int     powerModeTicks   = 0;
    int     ghostsEatenThisPower = 0;

    private static final int POWER_DURATION   = 300;
    private static final int GHOST_RESPAWN    = 80;
    private static final int GHOST_BASE_SCORE = 200;

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
        for (Block ghost : ghosts) {
            ghost.updateDirection(directions[random.nextInt(4)]);
        }
        gameLoop = new Timer(50, this);
        gameLoop.start();
    }

    public void loadMap() {
        walls        = new HashSet<>();
        foods        = new HashSet<>();
        powerPellets = new HashSet<>();
        ghosts       = new HashSet<>();
        cherry       = null;
        cherryTicks    = 0;
        foodEatenCount = 0;

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                char tile = tileMap[r].charAt(c);
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

    private void spawnCherry() {
        int centerX = 9 * tileSize;
        int centerY = 3 * tileSize;
        cherry      = new Block(cherryImage, centerX, centerY, tileSize, tileSize);
        cherryTicks = CHERRY_DURATION;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block ghost : ghosts) {
            boolean flickering = ghost.respawnTicks > 0 && (ghost.respawnTicks / 5) % 2 == 0;
            if (!flickering) {
                g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
            }
        }
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

        if (powerModeTicks > 0) {
            g.setColor(new Color(0, 200, 255));
            int barWidth = (int)((double) powerModeTicks / POWER_DURATION * (boardWidth - tileSize));
            g.fillRect(tileSize / 2, tileSize - 6, barWidth, 5);
        }

        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(Color.WHITE);
        g.drawString("x" + lives + "  Score: " + score, tileSize / 2, tileSize / 2);

        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, boardHeight / 2 - 70, boardWidth, 145);

            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            String gameOverText = "GAME OVER";
            int textWidth = g.getFontMetrics().stringWidth(gameOverText);
            g.drawString(gameOverText, (boardWidth - textWidth) / 2, boardHeight / 2 - 20);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            String scoreText = "Score: " + score;
            textWidth = g.getFontMetrics().stringWidth(scoreText);
            g.drawString(scoreText, (boardWidth - textWidth) / 2, boardHeight / 2 + 15);

            g.setColor(Color.YELLOW);
            String restartText = "Press ENTER or SPACE to restart";
            textWidth = g.getFontMetrics().stringWidth(restartText);
            g.drawString(restartText, (boardWidth - textWidth) / 2, boardHeight / 2 + 45);
        }

        if (paused) {
            g.setColor(new Color(0, 0, 0, 160));
            g.fillRect(0, boardHeight / 2 - 40, boardWidth, 80);
            g.setColor(Color.CYAN);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            String pausedText = "PAUSED  –  P to resume";
            int textWidth = g.getFontMetrics().stringWidth(pausedText);
            g.drawString(pausedText, (boardWidth - textWidth) / 2, boardHeight / 2 + 10);
        }
    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width - 5 &&
               a.x + a.width - 5 > b.x &&
               a.y < b.y + b.height - 5 &&
               a.y + a.height - 5 > b.y;
    }

    private void activatePowerMode() {
        powerModeTicks       = POWER_DURATION;
        ghostsEatenThisPower = 0;
        for (Block ghost : ghosts) {
            ghost.image = scaredGhostImage;
        }
    }

    private void deactivatePowerMode() {
        for (Block ghost : ghosts) {
            ghost.image = ghost.normalImage;
        }
    }

    public void move() {
        if (paused || gameOver) return;

        for (Block ghost : ghosts) {
            if (collision(ghost, pacman) && ghost.respawnTicks == 0) {
                if (powerModeTicks > 0) {
                    int comboScore = GHOST_BASE_SCORE * (1 << ghostsEatenThisPower);
                    score += comboScore;
                    ghostsEatenThisPower++;
                    ghost.reset();
                    ghost.image        = ghost.normalImage;
                    ghost.respawnTicks = GHOST_RESPAWN;
                    ghost.updateDirection(directions[random.nextInt(4)]);
                } else {
                    lives--;
                    if (lives == 0) { gameOver = true; return; }
                    pacman.x = pacman.startX;
                    pacman.y = pacman.startY;
                    for (Block g : ghosts) { g.x = g.startX; g.y = g.startY; }
                    powerModeTicks = 0;
                    deactivatePowerMode();
                }
            }
        }

        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        for (Block wall : walls) {
            if (collision(wall, pacman)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
            }
        }

        for (Block ghost : ghosts) {
            if (ghost.y == 9 * tileSize && ghost.x == 4 * tileSize) {
                ghost.updateDirection(directions[random.nextInt(4)]);
            }
            ghost.y += ghost.velocityY;
            ghost.x += ghost.velocityX;
            for (Block wall : walls) {
                if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
                    ghost.y -= ghost.velocityY;
                    ghost.x -= ghost.velocityX;
                    ghost.updateDirection(directions[random.nextInt(4)]);
                }
            }
            if (ghost.respawnTicks > 0) {
                ghost.respawnTicks--;
                if (ghost.respawnTicks == 0 && powerModeTicks > 0) {
                    ghost.image = scaredGhostImage;
                }
            }
        }

        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                score += 10;
                foodEaten = food;
                foodEatenCount++;
            }
        }
        foods.remove(foodEaten);

        Block pelletEaten = null;
        for (Block pp : powerPellets) {
            if (collision(pacman, pp)) {
                score += 50;
                pelletEaten = pp;
            }
        }
        if (pelletEaten != null) {
            powerPellets.remove(pelletEaten);
            activatePowerMode();
        }

        if (cherry != null && collision(pacman, cherry)) {
            score += CHERRY_SCORE;
            cherry      = null;
            cherryTicks = 0;
        }

        boolean halfFoodEaten = totalFood > 0 && foodEatenCount >= totalFood / 2;
        if (cherry == null && cherryTicks == 0 && halfFoodEaten) {
            spawnCherry();
        }

        if (cherryTicks > 0 && --cherryTicks == 0) {
            cherry = null;
        }

        if (powerModeTicks > 0 && --powerModeTicks == 0) {
            deactivatePowerMode();
        }
    }
}
