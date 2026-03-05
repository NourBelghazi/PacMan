import java.awt.event.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class Pacman extends JPanel{

    class Block{
        int x;
        int y;
        int height;
        int width;
        Image image;
        int startX;
        int startY;

         Block(Image image, int x, int y, int height, int width){
            this.image=image;
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
    private int tileSize =32;
    private int boardWidth = tileSize*columnCount;
    private int boardHeight = tileSize*rowCount;

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

    public Pacman(){
        setPreferredSize(new Dimension(boardWidth,boardHeight));
        setBackground(Color.BLACK);

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

    }

        public void loadMap(){
            walls = new HashSet<Block>();
            foods = new HashSet<Block>();
            ghosts = new HashSet<Block>();

            for(int r = 0; r<rowCount; r++){
                for(int c = 0; c<columnCount; c++){
                    String row = tileMap[r];
                    char tileMapChar = row.charAt(c);

                    int x = c*tileSize;
                    int y = r*tileSize;

                    if(tileMapChar == 'X'){
                        walls.add(new Block(wallImage, x, y, tileSize,tileSize));
                    }
                    if(tileMapChar == 'b'){
                        ghosts.add(new Block(blueGhostImage, x, y, tileSize,tileSize));
                    }
                    if(tileMapChar == 'o'){
                        ghosts.add(new Block(orangeGhostImage, x, y, tileSize,tileSize));
                    }

                    if(tileMapChar == 'p'){
                        ghosts.add(new Block(pinkGhostImage, x, y, tileSize,tileSize));
                    }
                    if(tileMapChar == 'r'){
                        ghosts.add(new Block(redGhostImage, x, y, tileSize,tileSize));
                    }
                    else if(tileMapChar == 'P'){
                        pacman = new Block( pacmanRightImage, x, y, tileSize,tileSize);
                    }
                    if(tileMapChar == ' '){
                        foods.add(new Block(null, x+14, y+14, 4,4));
                    }


                }
            }

        }
        public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        g.drawImage(pacman.image,pacman.x, pacman.y, pacman.width, pacman.height, null);


        for(Block ghost:ghosts){
            g.drawImage(ghost.image,pacman.x, ghost.y, ghost.width, ghost.height, null);
        }

    }


    }
