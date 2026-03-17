package pacman;

public final class GameConstants {

    public static final int TILE_SIZE    = 32;
    public static final int ROW_COUNT    = 21;
    public static final int COLUMN_COUNT = 19;
    public static final int BOARD_WIDTH  = TILE_SIZE * COLUMN_COUNT;
    public static final int BOARD_HEIGHT = TILE_SIZE * ROW_COUNT;

    public static final int FOOD_SCORE       = 10;
    public static final int POWER_SCORE      = 50;
    public static final int CHERRY_SCORE     = 100;
    public static final int GHOST_BASE_SCORE = 200;

    public static final int POWER_DURATION  = 300;
    public static final int CHERRY_DURATION = 200;
    public static final int GHOST_RESPAWN   = 80;

    private GameConstants() {}
}
