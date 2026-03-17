package pacman.map;

import pacman.GameConstants;
import pacman.ImageLoader;
import pacman.entity.Block;
import java.awt.Image;
import java.util.HashSet;

public class GameMap {

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

    private final Image wallImage;
    private final Image blueGhostImage, redGhostImage, pinkGhostImage, orangeGhostImage;
    private final Image powerFoodImage;
    private final Image pacmanRightImage;

    public HashSet<Block> walls;
    public HashSet<Block> foods;
    public HashSet<Block> powerPellets;
    public HashSet<Block> ghosts;
    public Block          pacman;
    public int            totalFood;

    public GameMap(Image pacmanRightImage) {
        this.wallImage        = ImageLoader.load("/images/wall.png");
        this.blueGhostImage   = ImageLoader.load("/images/blueGhost.png");
        this.redGhostImage    = ImageLoader.load("/images/redGhost.png");
        this.pinkGhostImage   = ImageLoader.load("/images/pinkGhost.png");
        this.orangeGhostImage = ImageLoader.load("/images/orangeGhost.png");
        this.powerFoodImage   = ImageLoader.load("/images/powerFood.png");
        this.pacmanRightImage = pacmanRightImage;
    }

    public void load() {
        int ts = GameConstants.TILE_SIZE;
        walls        = new HashSet<>();
        foods        = new HashSet<>();
        powerPellets = new HashSet<>();
        ghosts       = new HashSet<>();

        for (int row = 0; row < GameConstants.ROW_COUNT; row++) {
            for (int col = 0; col < GameConstants.COLUMN_COUNT; col++) {
                char tile = TILE_MAP[row].charAt(col);
                int  x    = col * ts;
                int  y    = row * ts;

                switch (tile) {
                    case 'X': walls.add(new Block(wallImage,        x, y, ts, ts)); break;
                    case 'b': ghosts.add(new Block(blueGhostImage,   x, y, ts, ts)); break;
                    case 'o': ghosts.add(new Block(orangeGhostImage, x, y, ts, ts)); break;
                    case 'p': ghosts.add(new Block(pinkGhostImage,   x, y, ts, ts)); break;
                    case 'r': ghosts.add(new Block(redGhostImage,    x, y, ts, ts)); break;
                    case 'P': pacman = new Block(pacmanRightImage,   x, y, ts, ts); break;
                    case ' ': foods.add(new Block(null, x + 14, y + 14, 4, 4));      break;
                    case 'W':
                        int ps = ts - 16;
                        powerPellets.add(new Block(powerFoodImage, x + 8, y + 8, ps, ps));
                        break;
                }
            }
        }
        totalFood = foods.size();
    }
}
