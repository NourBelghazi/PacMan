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
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');

        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');

        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');


        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');

        }
        if (pacman.direction == 'U') {
            pacman.image = pacmanUpImage;
        }
        if (pacman.direction == 'D') {
            pacman.image = pacmanDownImage;
        }
        if (pacman.direction == 'R') {
            pacman.image = pacmanRightImage;
        }
        if (pacman.direction == 'L') {
            pacman.image = pacmanLeftImage;
        }
    }

    class Block {
        int x;
        int y;
        int height;
        int width;
        Image image;
        int startX;
        int startY;

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
            if (this.direction == 'U') {
                velocityY = -tileSize / 4;
                velocityX = 0;
            }
            if (this.direction == 'D') {
                velocityY = tileSize / 4;
                velocityX = 0;
            }
            if (this.direction == 'R') {
                velocityX = tileSize / 4;
                velocityY = 0;
            }
            if (this.direction == 'L') {
                velocityX = -tileSize / 4;
                velocityY = 0;
            }

        }


        Block(Image image, int x, int y, int height, int width) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.height = height;
            this.width = width;
            this.startX = x;
            this.startY = y;

        }

    }


    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = tileSize * columnCount;
    private int boardHeight = tileSize * rowCount;

    private Image wallImage;
    private Image blueGhostImage;
    private Image redGhostImage;
    private Image pinkGhostImage;
    private Image orangeGhostImage;
    private Image scaredGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;
    private String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
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
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();
    int score =0;
    int lives =3;
    boolean gameOver = false;

    public Pacman() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        wallImage = new ImageIcon(getClass().getResource("/images/wall.png")).getImage();

        blueGhostImage = new ImageIcon(getClass().getResource("/images/blueGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("/images/redGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("/images/pinkGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("/images/orangeGhost.png")).getImage();
        scaredGhostImage = new ImageIcon(getClass().getResource("/images/scaredGhost.png")).getImage();

        pacmanUpImage = new ImageIcon(getClass().getResource("/images/pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("/images/pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("/images/pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("/images/pacmanRight.png")).getImage();
        loadMap();
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }

        gameLoop = new Timer(50, this);
        gameLoop.start();

    }

    public void loadMap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c * tileSize;
                int y = r * tileSize;

                if (tileMapChar == 'X') {
                    walls.add(new Block(wallImage, x, y, tileSize, tileSize));
                }
                if (tileMapChar == 'b') {
                    ghosts.add(new Block(blueGhostImage, x, y, tileSize, tileSize));
                }
                if (tileMapChar == 'o') {
                    ghosts.add(new Block(orangeGhostImage, x, y, tileSize, tileSize));
                }

                if (tileMapChar == 'p') {
                    ghosts.add(new Block(pinkGhostImage, x, y, tileSize, tileSize));
                }
                if (tileMapChar == 'r') {
                    ghosts.add(new Block(redGhostImage, x, y, tileSize, tileSize));
                } else if (tileMapChar == 'P') {
                    pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                }
                if (tileMapChar == ' ') {
                    foods.add(new Block(null, x + 14, y + 14, 4, 4));
                }


            }
        }

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }
        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }
        g.setColor(Color.YELLOW);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
        else {
            g.drawString("x" + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize/2, tileSize/2);
        }



    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width - 5 &&
                a.x + a.width - 5 > b.x &&
                a.y < b.y + b.height - 5 &&
                a.y + a.height - 5 > b.y;
    }

    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        for (Block wall : walls) {
            if (collision(wall, pacman)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
            }

        }

        for (Block ghost : ghosts) {
            if(ghost.y==9*tileSize && ghost.x==4*tileSize){
                char newDirection = directions[random.nextInt(4)];
                ghost.updateDirection(newDirection);

            }
            ghost.y += ghost.velocityY;
            ghost.x += ghost.velocityX;

            for (Block wall : walls) {
                if (collision(ghost, wall)||ghost.x<=0||ghost.x+ghost.width>=boardWidth) {
                    ghost.y -= ghost.velocityY;
                    ghost.x -= ghost.velocityX;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }



        }
        Block foodEaten = null;

        for(Block food:foods){
            if(collision(pacman,food)) {
                score+=10;
                foodEaten = food;
            }
        }
        foods.remove(foodEaten);


    }
}
