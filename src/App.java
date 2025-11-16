import java.awt.Image;

import javax.swing.JFrame; //Jframe is our window

public class App {
    public static void main(String[] args) throws Exception {
        //width and height of the window
        //19 columns, 21 rows. Each tile is 32px * 32px
        //width of the window = 19 columns * 32px
        //height of the window = 21 rows * 32px
        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32;
        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;

        //Creating the window
        JFrame frame = new JFrame("Pac Man");
        //frame.setVisible(true); //to make the window visible
        frame.setSize(boardWidth, boardHeight);//Set the size
        frame.setLocationRelativeTo(null); //to set the window to appear at the center of the screen
        frame.setResizable(false); //to make the user cannot resize the window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//to terminate if the player click x on the window
        
        

        //Create an instance of the JPanel
        PacMan pacmanGame = new PacMan();
        frame.add(pacmanGame); // add the panel to our window
        frame.pack();// to make sure that we get the full size of the JPanel within our window
        pacmanGame.requestFocus();
        frame.setVisible(true);
    }
}
