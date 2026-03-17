package pacman.entity;

import pacman.GameConstants;
import java.awt.Image;
import java.util.HashSet;

public class Block {

    public int   x, y, width, height;
    public Image image;
    public Image normalImage;
    public int   startX, startY;
    public char  direction    = 'U';
    public int   velocityX    = 0;
    public int   velocityY    = 0;
    public int   respawnTicks = 0;

    public Block(Image image, int x, int y, int width, int height) {
        this.image       = image;
        this.normalImage = image;
        this.x = x;         this.y = y;
        this.width = width; this.height = height;
        this.startX = x;    this.startY = y;
    }

    public void updateDirection(char newDirection, HashSet<Block> walls) {
        char previousDirection = this.direction;
        this.direction = newDirection;
        updateVelocity();
        this.x += this.velocityX;
        this.y += this.velocityY;
        for (Block wall : walls) {
            if (collides(this, wall)) {
                this.x -= this.velocityX;
                this.y -= this.velocityY;
                this.direction = previousDirection;
                updateVelocity();
                return;
            }
        }
    }

    public void updateVelocity() {
        int speed = GameConstants.TILE_SIZE / 4;
        switch (direction) {
            case 'U': velocityX =  0;     velocityY = -speed; break;
            case 'D': velocityX =  0;     velocityY =  speed; break;
            case 'R': velocityX =  speed; velocityY =  0;     break;
            case 'L': velocityX = -speed; velocityY =  0;     break;
        }
    }

    public void reset() {
        x = startX;
        y = startY;
    }

    public static boolean collides(Block a, Block b) {
        return a.x              < b.x + b.width  - 5 &&
               a.x + a.width  - 5 > b.x              &&
               a.y              < b.y + b.height - 5 &&
               a.y + a.height - 5 > b.y;
    }
}
