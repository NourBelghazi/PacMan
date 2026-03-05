import javax.swing.JFrame;
public class App {
    public static void main(String[] args){
        int rowCount = 21;
        int columnCount = 25;
        int tileSize =32;
        int boardWidth = tileSize*rowCount;
        int boardHeight = tileSize*columnCount;

        JFrame frame = new JFrame("Pacman");

        frame.setSize(boardWidth, boardHeight);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Pacman pacman = new Pacman();
        frame.add(pacman);
        frame.pack();
        pacman.requestFocus();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }
}
