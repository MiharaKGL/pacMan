import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;


//to have PacMan inherit jpanel
// we nee a game loop. after fixing the error we will get actionPerformed methond in the bottom
//key listener to move with arrow keys. will get unimplemented methods after fixing errors
public class PacMan extends JPanel implements ActionListener, KeyListener{

    //specify each images' position - x,y position & width,height - for wall, food, ghost, pacman
    //create a class to represent this object
    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        // x and y starting positions
        int startX;
        int startY;

        //three variables to move the pacman with the arrow key
        char direction = 'U'; //Can be U, D, L, R
        int velocityX = 0;
        int velocityY = 0;

        //Crete the constructor
        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        // create a function - update the direction when arrow keys are pressed
        void updateDirection(char direction) {
            char prevDirection = this.direction; //store the previoud direction
            this.direction = direction;
            updateVelocity(); //update the velocity according to the direction
            //iterate through all the walls
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;//update the direction
                    updateVelocity();
                }
            }
        }

        //define the function -> update velocity
        void updateVelocity() {
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -tileSize/4; // will go 8px. Going up -> Negative
            }
            else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = tileSize/4; // Going down -> positive
            }
            else if (this.direction == 'L') {
                this.velocityX = -tileSize/4;
                this.velocityY = 0;
            }
            else if (this.direction == 'R') {
                this.velocityX = tileSize/4;
                this.velocityY = 0;
            }
        }

        // a reset function to reset the placements when game over
        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    //make the properties private
    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    
    //create variables to store the images
    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;

    private Image pacmanUPImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;


    //tile map - with an array of strings
    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
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
        "X       bpo       X",
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

    //create hash sets
    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    // we need a timer
    Timer gameLoop;

    // to move ghosts randomly
    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();

    //Variables to make interactions between pacman, ghosts and food. Check the number of lives pacman has. and the score
    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    // create a constructor
    PacMan() {
        //set preffered size
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        //set background color Black
        setBackground(Color.BLACK);

        //to make the key presses work
        addKeyListener(this);
        setFocusable(true); // to make sure that the JPanel is the one listening for key presses

        //load the images
        // './' means the we are looking in the same folder that we are in
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();

        pacmanUPImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        //calling
        loadMap();
        //print statements to see how many walls, foods, and ghosts we have
        //System.out.println(walls.size());
        //System.out.println(foods.size());
        //System.out.println(ghosts.size());

        //iterate through each ghost -> to move ghosts randomly
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }

        gameLoop = new Timer(50, this); // 50 is the delay. this is the pacman object
        // every 50ms, we are going to repaint
        // 20 frames per second
        gameLoop.start();

        //key listeners to move with arrow keys

    }

    //create the objects for the wall, ghosts, food , and pacman
    //create a function
    public void loadMap() {
        //initialize all the hash sets
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        //iterate through the map (r -> row, c -> column)
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                String row = tileMap[r]; //get the current row
                char tileMapChar = row.charAt(c); //get the current character

                //where this tile is. need x,y position & width,height(tile size).
                int x = c*tileSize; // to get the x position -> How many columns we are from the left
                int y = r*tileSize; //y -> how many rows from the top

                if (tileMapChar == 'X') {
                    //block wall
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);// add the block to walls hashset
                }
                else if (tileMapChar == 'b') {
                    //blueghost
                    Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'o') {
                    //orangeghost
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'p') {
                    //pinkghost
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'r') {
                    //redghost
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'P') {
                    //PacMan
                    pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                }
                else if (tileMapChar == ' ') {
                    //food
                    Block food = new Block(null, x + 14, y + 14, 4, 4); //draw a rectangle.place food in the tile -> 14 left from y, 14 down from x. width = 4px, height = 4px
                    foods.add(food);
                }

                
            }
        }

    }

    // Draw all the objects on to our game
    //create a function
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // it will invoke the function of the same name from JPanel
        draw(g);
    }

    //create a draw image function
    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        //for the rest of the objects
        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.WHITE); //Set the colour to white
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height); //for food, we don't have an image. We just draw rectangle
        }

        //score
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
        else {
            g.drawString("x" + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize/2, tileSize/2);
        }

    }

    //create function - to actually move the pacman
    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        //check wall collisions
        for (Block wall : walls) {
            if(collision(pacman, wall)) {
               //undo previous move
               pacman.x -= pacman.velocityX;
               pacman.y -= pacman.velocityY;
               break;
            }
        }

        //check ghost collisions
        for (Block ghost : ghosts) {
            //A check to see if the current ghost has collided with pacman
            if (collision(ghost, pacman)) {
                lives -= 1;
                //game over when 3 lives are lost
                if (lives == 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
            }

            //the ghost stuck in row 9. need to force it to go upward
            if (ghost.y == tileSize*9 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }

            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            for (Block wall : walls) {
                if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    // ghost need to change the direction itself immediately if meets a wall
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }

        //make interactions between pacman, ghosts and food. Check the number of lives pacman has. and the score
        //to check food collision
        Block foodEaten = null; //remove the food when pacman ate it
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);

        // to restart when won the game
        if (foods.isEmpty()) {
            loadMap();
            resetPositions();
            
        }
    }

    // to stop against a wall
    public boolean collision(Block a, Block b) {
        // image is a square. Other - triangle
        return a.x < b.x + b.width &&
               a.x + a.width > b.x &&
               a.y < b.y + b.height &&
               a.y + a.height > b.y;
    }

    //Define resetPositions function
    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move(); // call the function
        //Game loop
        repaint(); // it will call paincomponent again
        // to execute this code we need a game loop wich is a timer

        //stop the game when game over
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // we don't want to hold on to the key. So removed
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //to restart the game when any key is pressed after gameover
        if (gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }

        //System.out.println("KeyEvent: " + e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
        }

        //to change the image of pacman according to the direction
        if (pacman.direction == 'U') {
            pacman.image = pacmanUPImage;
        }
        else if (pacman.direction == 'D') {
            pacman.image = pacmanDownImage;
        }
        else if (pacman.direction == 'L') {
            pacman.image = pacmanLeftImage;
        }
        else if (pacman.direction == 'R') {
            pacman.image = pacmanRightImage;
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {
        // we are not using this because no arrowkeys. So removed
    }

}
