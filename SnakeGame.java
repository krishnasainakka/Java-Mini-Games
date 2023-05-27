package JavaGames;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class SnakeGame extends Frame implements ActionListener {
    int init = 0;

    //CREATING A GIRD BOARD
    private static final int CELL_SIZE = 30;
    private static final int ROWS = 24;
    private static final int COLS = 24;

    //SIDE PANEL DIMENSIONS
    private static final int S_WIDTH = 360;
    private static final int S_HEIGHT = COLS * CELL_SIZE+40;

    //WINDOW DIMENSIONS
    private static final int SCREEN_WIDTH = ROWS * CELL_SIZE + S_WIDTH;
    private static final int SCREEN_HEIGHT = COLS * CELL_SIZE+40;

    //GAMEBOARD DIMENSIONS
    private static final int BOARD_WIDTH = ROWS * CELL_SIZE;
    private static final int BOARD_HEIGHT = COLS * CELL_SIZE;
    private static final int GAME_UNITS = (BOARD_WIDTH*BOARD_HEIGHT)/CELL_SIZE;
    static final int DELAY = 100;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    boolean gameCompleted = true;
   // boolean paused = false;
    Timer timer;
    Random random;
    private final  Panel gameBoard;
    private final  Panel dashboard;
    private BufferedImage image;
    public SnakeGame(){
        random = new Random();
        this.setFocusable(true);
        //startGame();



        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent we){
                System.exit(0);
            }
        });

        //CREATING TWO PANELS
        setLayout(new BorderLayout());

        gameBoard = new Panel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                paintGameBoard(g);
            }
        };
        gameBoard.setPreferredSize(new Dimension(BOARD_WIDTH,BOARD_HEIGHT));
        gameBoard.setBackground(Color.black);
        add(gameBoard, BorderLayout.CENTER);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_A :
                    case KeyEvent.VK_LEFT :
                        if(direction != 'R') {
                            direction = 'L' ;
                        }
                        break;
                    case KeyEvent.VK_D :
                    case KeyEvent.VK_RIGHT :
                        if(direction != 'L') {
                            direction = 'R' ;
                        }
                        break;
                    case KeyEvent.VK_W :
                    case KeyEvent.VK_UP :
                        if(direction != 'D') {
                            direction = 'U' ;
                        }
                        break;
                    case KeyEvent.VK_S :
                    case KeyEvent.VK_DOWN :
                        if(direction != 'U') {
                            direction = 'D' ;
                        }
                        break;
                    case KeyEvent.VK_ENTER :
                        if(gameCompleted == true && running == false) {
                            resetGame();
                        }
                        break;
                }
            }
        });

        dashboard = new Panel(){
            private static final Font LARGE_FONT = new Font("Tahoma", Font.BOLD, 30);

            private static final Font MEDIUM_FONT = new Font("Tahoma", Font.BOLD, 20);

            private static final Font SMALL_FONT = new Font("Tahoma", Font.BOLD, 12);


            private static final int SCOREBOARD_OFFSET = 350;

            private static final int CONTROLS_OFFSET = 450;

            private static final int MESSAGE_STRIDE = 30;

            private static final int SMALL_OFFSET = 30;

            private static final int LARGE_OFFSET = 50;


            @Override
            public void paint(Graphics g) {
                super.paint(g);
                if (image != null) {
                    g.drawImage(image, 0, 0, this.getWidth(), this.getHeight()/3, null);
                }
                else{
                    System.out.println("no image");
                }

                g.setColor(Color.WHITE);
//
                g.setFont(LARGE_FONT);
                g.drawString("Snake Game", getWidth() / 2 - g.getFontMetrics().stringWidth("Snake Game") / 2, 300);
//
                g.setFont(MEDIUM_FONT);
                g.drawString("Score Board", SMALL_OFFSET, SCOREBOARD_OFFSET);
                g.drawString("Controls", SMALL_OFFSET, CONTROLS_OFFSET);
//
                g.setFont(SMALL_FONT);
//
                int drawY = SCOREBOARD_OFFSET;
//
                g.drawString("Total Score: " + applesEaten, LARGE_OFFSET, drawY += MESSAGE_STRIDE);

                drawY = CONTROLS_OFFSET;
                g.drawString("Move Up: W / Up Arrowkey", LARGE_OFFSET, drawY += MESSAGE_STRIDE);
                g.drawString("Move Down: S / Down Arrowkey", LARGE_OFFSET, drawY += MESSAGE_STRIDE);
                g.drawString("Move Left: A / Left Arrowkey", LARGE_OFFSET, drawY += MESSAGE_STRIDE);
                g.drawString("Move Right: D / Right Arrowkey", LARGE_OFFSET, drawY += MESSAGE_STRIDE);

                g.drawString("Designed By", getWidth() / 2 - g.getFontMetrics().stringWidth("Designed By") / 2,
                        drawY += 60);
                g.setFont(MEDIUM_FONT);
                g.drawString("Krishna sai", getWidth() / 2 - g.getFontMetrics().stringWidth("Krishna sai") / 2,
                        drawY += 25);
            }
        };
        dashboard.setPreferredSize(new Dimension(S_WIDTH - 20,S_HEIGHT));
        dashboard.setBackground(Color.blue);
        add(dashboard, BorderLayout.EAST);
        try {
            // Load image from file
            File imageFile = new File("D:\\JAVA\\First_JavaProgram\\src\\JavaGames\\snake.png");
            image = ImageIO.read(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // add a KeyListener to the panel to detect Enter key presses
        dashboard.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // start the game
                    resetGame();
                    //startGame();
                }
            }
        });

    }

    public void resetGame(){
        this.bodyParts = 6;
        this.applesEaten = 0;
        this.gameCompleted = true;
        if(timer != null) {
            timer.stop();
        }
        // Reset the positions of the snake's body parts
        for (int i = 0; i < bodyParts; i++) {
            x[i] = (COLS / 2 - i) * CELL_SIZE;  // Horizontal position at the starting box
            y[i] = (ROWS / 2) * CELL_SIZE;      // Vertical position at the starting box
        }
        startGame();
    }
    public void startGame(){
        newApple();
        running = true;
        timer = new Timer(DELAY,this);
        timer.start();
    }
    public void newApple(){
        appleX = random.nextInt((int)(BOARD_WIDTH/CELL_SIZE))*CELL_SIZE;
        appleY = random.nextInt((int)(BOARD_HEIGHT/CELL_SIZE))*CELL_SIZE;
    }
    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - CELL_SIZE;
                break;
            case 'D':
                y[0] = y[0] + CELL_SIZE;
                break;
            case 'L':
                x[0] = x[0] - CELL_SIZE;
                break;
            case 'R':
                x[0] = x[0] + CELL_SIZE;
                break;
        }
    }
    public void checkApple(){
        if((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }
    public void checkCollisions(){
        //CHECK IF HEAD COLLIDES WITH BODY
        for(int i = bodyParts;i>0;i--){
            if((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }
        //CHECKING IF HEAD COLLIDES WITH LEFT BORDER
        if(x[0] < 0) {
            running = false;
        }
        //CHECKING IF HEAD COLLIDES WITH TOP BORDER
        if(x[0] > BOARD_WIDTH) {
            running = false;
        }

        //CHECKING IF HEAD COLLIDES WITH RIGHT BORDER
        if(y[0] < 0) {
            running = false;
        }
        //CHECKING IF HEAD COLLIDES WITH BOTTOM BORDER
        if(y[0] > BOARD_HEIGHT) {
            running = false;
        }
    }


    public void gameOver(Graphics g){
        gameCompleted = true;
        g.setColor(Color.red);
        g.setFont(new Font("Roboto",Font.BOLD,70));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (BOARD_WIDTH - metrics.stringWidth("Game Over"))/2,BOARD_HEIGHT/2);
        g.drawString("Press ENTER to Start ", (BOARD_WIDTH - metrics.stringWidth("Press ENTER to Start "))/2,(BOARD_HEIGHT/3)*2);
        g.drawString("Score " + applesEaten, (BOARD_WIDTH - metrics.stringWidth("Score " + applesEaten))/2,g.getFont().getSize());


    }
    public void actionPerformed(ActionEvent e){
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        gameBoard.repaint();
    }
    public void paintGameBoard(Graphics g){
        if(running) {
            // DRAWING GRID
//        g.setColor(Color.white);
//        for( int row = 0;row < ROWS; row++) {
//            for( int col = 0;col < COLS;col++) {
//                int x = col * CELL_SIZE;
//                int y = row * CELL_SIZE;
//                g.setColor(Color.white);
//                g.drawRect(x,y,CELL_SIZE,CELL_SIZE);
//            }
//        }
//            g.setColor(Color.white);
//            for (int i = 0; i < BOARD_HEIGHT / CELL_SIZE; i++) {
//                g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, BOARD_HEIGHT);
//                g.drawLine(0, i * CELL_SIZE, BOARD_WIDTH, i * CELL_SIZE);
//            }

            //DRAWING APPLES
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, CELL_SIZE, CELL_SIZE);

            //DRAWING SNAKE
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], CELL_SIZE, CELL_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], CELL_SIZE, CELL_SIZE);
                }
            }
            //DISPLAYING SCORE
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free",Font.BOLD,75));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score " + applesEaten, (BOARD_WIDTH - metrics.stringWidth("Score " + applesEaten))/2,g.getFont().getSize());
        }
        else{
            gameOver(g);
            dashboard.repaint();
        }
    }


    public static void main(String[] args){
        SnakeGame sg = new SnakeGame();
        sg.setSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        sg.setTitle("Snake Game");
        sg.setVisible(true);
        sg.setLocationRelativeTo(null);
        sg.setBackground(Color.black);

    }
}


