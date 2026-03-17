package pacman.engine;

import pacman.GameConstants;
import pacman.ImageLoader;
import pacman.entity.Block;
import pacman.map.GameMap;
import java.awt.Image;
import java.util.HashSet;
import java.util.Random;

public class GameEngine {

    private final GameMap gameMap;
    private final Random  random     = new Random();
    private final char[]  directions = {'U', 'D', 'L', 'R'};

    private final Image scaredGhostImage;
    private final Image pacmanUpImage, pacmanDownImage, pacmanLeftImage, pacmanRightImage;
    private final Image cherryImage;

    private int     score                = 0;
    private int     highScore            = 0;
    private int     lives                = 3;
    private int     level                = 1;
    private boolean gameOver             = false;
    private boolean paused               = false;
    private int     powerModeTicks       = 0;
    private int     ghostsEatenThisPower = 0;
    private int     cherryTicks          = 0;
    private int     foodEatenCount       = 0;
    private Block   cherry               = null;
    private boolean cherrySpawned        = false;

    public GameEngine() {
        pacmanRightImage = ImageLoader.load("/images/pacmanRight.png");
        pacmanUpImage    = ImageLoader.load("/images/pacmanUp.png");
        pacmanDownImage  = ImageLoader.load("/images/pacmanDown.png");
        pacmanLeftImage  = ImageLoader.load("/images/pacmanLeft.png");
        scaredGhostImage = ImageLoader.load("/images/scaredGhost.png");
        cherryImage      = ImageLoader.load("/images/cherry.png");

        gameMap = new GameMap(pacmanRightImage);
        loadLevel();
    }

    private void loadLevel() {
        gameMap.load();
        cherry         = null;
        cherryTicks    = 0;
        cherrySpawned  = false;
        foodEatenCount = 0;
        powerModeTicks = 0;
        randomizeGhostDirections();
    }

    private void randomizeGhostDirections() {
        for (Block ghost : gameMap.ghosts) {
            ghost.updateDirection(directions[random.nextInt(4)], gameMap.walls);
        }
    }

    // ---- Getters for renderer ----

    public HashSet<Block> getWalls()        { return gameMap.walls; }
    public HashSet<Block> getFoods()        { return gameMap.foods; }
    public HashSet<Block> getPowerPellets() { return gameMap.powerPellets; }
    public HashSet<Block> getGhosts()       { return gameMap.ghosts; }
    public Block          getPacman()       { return gameMap.pacman; }
    public Block          getCherry()       { return cherry; }

    public int     getScore()          { return score; }
    public int     getHighScore()      { return highScore; }
    public int     getLives()          { return lives; }
    public int     getLevel()          { return level; }
    public boolean isGameOver()        { return gameOver; }
    public boolean isPaused()          { return paused; }
    public int     getPowerModeTicks() { return powerModeTicks; }

    // ---- Input actions ----

    public void changePacmanDirection(char dir) {
        gameMap.pacman.updateDirection(dir, gameMap.walls);
        switch (gameMap.pacman.direction) {
            case 'U': gameMap.pacman.image = pacmanUpImage;    break;
            case 'D': gameMap.pacman.image = pacmanDownImage;  break;
            case 'R': gameMap.pacman.image = pacmanRightImage; break;
            case 'L': gameMap.pacman.image = pacmanLeftImage;  break;
        }
    }

    public void togglePause()  { paused = !paused; }

    public void restartGame() {
        score    = 0;
        lives    = 3;
        level    = 1;
        gameOver = false;
        paused   = false;
        loadLevel();
    }

    // ---- Game loop ----

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
        Block pacman = gameMap.pacman;
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        if      (pacman.x + pacman.width <= 0)         pacman.x = GameConstants.BOARD_WIDTH;
        else if (pacman.x >= GameConstants.BOARD_WIDTH) pacman.x = -pacman.width;

        for (Block wall : gameMap.walls) {
            if (Block.collides(wall, pacman)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
            }
        }
    }

    private void moveGhosts() {
        int ts = GameConstants.TILE_SIZE;
        int bw = GameConstants.BOARD_WIDTH;

        for (Block ghost : gameMap.ghosts) {
            if (ghost.y == 9 * ts && ghost.x == 4 * ts) {
                ghost.updateDirection(directions[random.nextInt(4)], gameMap.walls);
            }
            ghost.y += ghost.velocityY;
            ghost.x += ghost.velocityX;

            if      (ghost.x + ghost.width <= 0) ghost.x = bw;
            else if (ghost.x >= bw)              ghost.x = -ghost.width;

            for (Block wall : gameMap.walls) {
                if (Block.collides(ghost, wall)) {
                    ghost.y -= ghost.velocityY;
                    ghost.x -= ghost.velocityX;
                    ghost.updateDirection(directions[random.nextInt(4)], gameMap.walls);
                }
            }

            if (ghost.respawnTicks > 0) {
                ghost.respawnTicks--;
                if (ghost.respawnTicks == 0 && powerModeTicks > 0) ghost.image = scaredGhostImage;
            }
        }
    }

    private void handleGhostCollisions() {
        for (Block ghost : gameMap.ghosts) {
            if (!Block.collides(ghost, gameMap.pacman) || ghost.respawnTicks > 0) continue;

            if (powerModeTicks > 0) {
                addScore(GameConstants.GHOST_BASE_SCORE * (1 << ghostsEatenThisPower));
                ghostsEatenThisPower++;
                ghost.reset();
                ghost.image        = ghost.normalImage;
                ghost.respawnTicks = GameConstants.GHOST_RESPAWN;
                ghost.updateDirection(directions[random.nextInt(4)], gameMap.walls);
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
        for (Block food : gameMap.foods) {
            if (Block.collides(gameMap.pacman, food)) { eaten = food; break; }
        }
        if (eaten != null) {
            gameMap.foods.remove(eaten);
            addScore(GameConstants.FOOD_SCORE);
            foodEatenCount++;
        }
    }

    private void handlePowerPelletCollection() {
        Block eaten = null;
        for (Block pp : gameMap.powerPellets) {
            if (Block.collides(gameMap.pacman, pp)) { eaten = pp; break; }
        }
        if (eaten != null) {
            gameMap.powerPellets.remove(eaten);
            addScore(GameConstants.POWER_SCORE);
            activatePowerMode();
        }
    }

    private void handleCherryCollection() {
        if (cherry != null && Block.collides(gameMap.pacman, cherry)) {
            addScore(GameConstants.CHERRY_SCORE);
            cherry      = null;
            cherryTicks = 0;
        }
        boolean halfFoodEaten = gameMap.totalFood > 0 && foodEatenCount >= gameMap.totalFood / 2;
        if (!cherrySpawned && cherry == null && cherryTicks == 0 && halfFoodEaten) {
            spawnCherry();
            cherrySpawned = true;
        }
    }

    private void tickTimers() {
        if (cherryTicks > 0    && --cherryTicks    == 0) cherry = null;
        if (powerModeTicks > 0 && --powerModeTicks == 0) deactivatePowerMode();
    }

    private void checkLevelComplete() {
        if (gameMap.foods.isEmpty() && gameMap.powerPellets.isEmpty()) {
            level++;
            loadLevel();
        }
    }

    private void addScore(int points) {
        score += points;
        if (score > highScore) highScore = score;
    }

    private void activatePowerMode() {
        powerModeTicks       = GameConstants.POWER_DURATION;
        ghostsEatenThisPower = 0;
        for (Block ghost : gameMap.ghosts) ghost.image = scaredGhostImage;
    }

    private void deactivatePowerMode() {
        for (Block ghost : gameMap.ghosts) ghost.image = ghost.normalImage;
    }

    private void resetPositions() {
        Block pacman   = gameMap.pacman;
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        pacman.direction = 'R';
        pacman.image     = pacmanRightImage;
        powerModeTicks   = 0;
        deactivatePowerMode();
        for (Block ghost : gameMap.ghosts) {
            ghost.reset();
            ghost.respawnTicks = 0;
            ghost.updateDirection(directions[random.nextInt(4)], gameMap.walls);
        }
    }

    private void spawnCherry() {
        int ts = GameConstants.TILE_SIZE;
        cherry      = new Block(cherryImage, 9 * ts, 3 * ts, ts, ts);
        cherryTicks = GameConstants.CHERRY_DURATION;
    }
}
