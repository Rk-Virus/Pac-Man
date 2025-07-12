import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.HashSet;
import java.util.Random;

public class PacMan extends JPanel implements ActionListener, KeyListener {
    class Block {
        int x, y;
        int height, width;
        Image image;
        int startX, startY;
        int velocityX = 0;
        int velocityY = 0;
        char direction = ' '; // 'U', 'D', 'L', 'R' for up, down, left, right

        public Block(int x, int y, int height, int width, Image image) {
            this.x = x;
            this.y = y;
            this.height = height;
            this.width = width;
            this.image = image;
            this.startX = x;
            this.startY = y;
        }

        public void updateMotion(char direction) {
            char previousDirection = this.direction;
            this.direction = direction;
            changeDirection();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (checkCollisions(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = previousDirection; // revert to previous direction
                    changeDirection();
                    break; // Stop moving if collision occurs
                }
            }

        }

        public void changeDirection() {
            switch (this.direction) {
                case 'U':
                    this.velocityY = -speed;
                    this.velocityX = 0;
                    break;
                case 'D':
                    this.velocityY = speed;
                    this.velocityX = 0;
                    break;
                case 'L':
                    this.velocityX = -speed;
                    this.velocityY = 0;
                    break;
                case 'R':
                    this.velocityX = speed;
                    this.velocityY = 0;
                    break;
                default:
                    this.velocityX = 0;
                    this.velocityY = 0;
            }
        }

        public void resetPosition() {
            this.x = this.startX;
            this.y = this.startY;
            this.updateMotion(' '); // Stop movement
        }
    }

    // Game constants
    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int width = columnCount * tileSize;
    private int height = rowCount * tileSize;
    private Image wallImage;
    private Image blueGhost;
    private Image redGhost;
    private Image orangeGhost;
    private Image pinkGhost;
    private Image pacmanUp;
    private Image pacmanDown;
    private Image pacmanLeft;
    private Image pacmanRight;
    HashSet<Block> walls = new HashSet<Block>();
    HashSet<Block> ghosts = new HashSet<Block>();
    HashSet<Block> foods = new HashSet<Block>();
    Block pacman;
    Timer gameLoop;

    private int speed = tileSize / 4;
    private Random random = new Random();
    private char ghostDirections[] = { 'U', 'D', 'L', 'R' };
    private int score = 0;
    private int lives = 3;
    private int level = 1;

    // Tile map representation
    private String[][] tileMaps = {
            { "XXXXXXXXXXXXXXXXXXX",
                    "X                 X",
                    "X XXXXX XXXXXXX XXX",
                    "X X             X X",
                    "X X X XXX XXXXX X X",
                    "X X X         X X X",
                    "X X X XXXXXXX X X X",
                    "X X X X     X X X X",
                    "X   X X XXX X X   X",
                    "XXX X X XrX X X XXX",
                    "X   X   b o   X   X",
                    "XXX X XXXXXXX X XXX",
                    "X   X         X   X",
                    "X XXXXX XXXXX X XXX",
                    "X                 X",
                    "X XXXXX XXXXXXX XXX",
                    "X X             X X",
                    "X X XXXXX XXXXX X X",
                    "X      P          X",
                    "X                 X",
                    "XXXXXXXXXXXXXXXXXXX" },
            { "XXXXXXXXXXXXXXXXXXX",
                    "X                 X",
                    "X XXXXX XXXXXXX XXX",
                    "X X             X X",
                    "X X X XXX XXXXX X X",
                    "X X X         X X X",
                    "X X X XXXXXXX X X X",
                    "X X X X     X X X X",
                    "X   X X XXX X X   X",
                    "XXX X    r    X XXX",
                    "X      b p o    XXX",
                    "XXX X XXXXXXX X XXX",
                    "X   X         X   X",
                    "X XXXXX XXXXX X XXX",
                    "X                 X",
                    "X XXXXX XXXXXXX XXX",
                    "X X             X X",
                    "X X XXXXX XXXXX X X",
                    "X     P           X",
                    "X                 X",
                    "XXXXXXXXXXXXXXXXXXX" },
            { "XXXXXXXXXXXXXXXXXXX",
                    "X                 X",
                    "X XXXXX XXXXXXX XXX",
                    "X X             X X",
                    "X X X XXXXXXX X X X",
                    "X X X         X X X",
                    "X X X XXXXXXX X X X",
                    "X X X XXXXXXX X X X",
                    "X   X   r b    X   X",
                    "XXX X XXXXXXX X XXX",
                    "X   X XXXXXXX X   X",
                    "XXX X XXXXXXX X XXX",
                    "X   X         X   X",
                    "X XXXXX XXXXX X XXX",
                    "X     p o r       X",
                    "X XXXXX XXXXXXX XXX",
                    "X X             X X",
                    "X X XXXXX XXXXX X X",
                    "X     P           X",
                    "X                 X",
                    "XXXXXXXXXXXXXXXXXXX" },
            {
                    "XXXXXXXXXXXXXXXXXXX",
                    "X   X     X     X X",
                    "X X X XXX X XXX X X",
                    "X X X X     X X X X",
                    "X X X X XXX X X X X",
                    "X X X X X X X X X X",
                    "X X X   X X   X X X",
                    "X X XXX X X XXX X X",
                    "X X  r  X X     X X",
                    "X XXX XXXXXXXX  XXX",
                    "X   b  b p o   r  X",
                    "X XXX XXXXXXXX  XXX",
                    "X X             X X",
                    "X X XXX X X XXX X X",
                    "X X   X X X   X X X",
                    "X XXX X X X XXX X X",
                    "X     X   X     X X",
                    "X XXXXX XXXXXXX XXX",
                    "X        P        X",
                    "X                 X",
                    "XXXXXXXXXXXXXXXXXXX"
            },
            {
                    "XXXXXXXXXXXXXXXXXXX",
                    "X       X         X",
                    "X XXXXXXXXXXXXX X X",
                    "X X     T       X X",
                    "X X X XXX XXXXX X X",
                    "X X X    o    X X X",
                    "X X X XXXXXXX X X X",
                    "X X X XXTXXXX X X X",
                    "X X X b    b  X X X",
                    "X X XXXXX XXXXX X X",
                    "X X   T p p T X   X",
                    "X X XXXXX XXXXX X X",
                    "X X X r    r  X X X",
                    "X X X XXXXXXX X X X",
                    "X X X XTTTTTX X X X",
                    "X X X         X X X",
                    "X X XXXXX XXX X X X",
                    "X X     T       X X",
                    "X X XXXXXXXXXXXXX X",
                    "X        P        X",
                    "XXXXXXXXXXXXXXXXXXX"
            }
    };

    public PacMan() {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        // load images
        wallImage = new ImageIcon(getClass().getResource("./assets/wall.png")).getImage();
        blueGhost = new ImageIcon(getClass().getResource("./assets/blueGhost.png")).getImage();
        redGhost = new ImageIcon(getClass().getResource("./assets/redGhost.png")).getImage();
        orangeGhost = new ImageIcon(getClass().getResource("./assets/orangeGhost.png")).getImage();
        pinkGhost = new ImageIcon(getClass().getResource("./assets/pinkGhost.png")).getImage();
        pacmanUp = new ImageIcon(getClass().getResource("./assets/pacmanUp.png")).getImage();
        pacmanDown = new ImageIcon(getClass().getResource("./assets/pacmanDown.png")).getImage();
        pacmanLeft = new ImageIcon(getClass().getResource("./assets/pacmanLeft.png")).getImage();
        pacmanRight = new ImageIcon(getClass().getResource("./assets/pacmanRight.png")).getImage();

        // load game map
        loadMap();
        for (Block ghost : ghosts) {
            char direction = ghostDirections[random.nextInt(ghostDirections.length)];
            ghost.direction = direction;
            ghost.changeDirection();

        }
        gameLoop = new Timer(50, this); // 1000/50 => 20 FPS
        gameLoop.start();
    }

    public void loadMap() {
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                char tile = tileMaps[level - 1][row].charAt(col);
                int x = col * tileSize;
                int y = row * tileSize;
                System.out.println(row + " " + col + " " + tile);

                switch (tile) {
                    case 'X':
                        walls.add(new Block(x, y, tileSize, tileSize, wallImage));
                        break;
                    case 'b':
                        ghosts.add(new Block(x, y, tileSize, tileSize, blueGhost));
                        break;
                    case 'r':
                        ghosts.add(new Block(x, y, tileSize, tileSize, redGhost));
                        break;
                    case 'o':
                        ghosts.add(new Block(x, y, tileSize, tileSize, orangeGhost));
                        break;
                    case 'p':
                        ghosts.add(new Block(x, y, tileSize, tileSize, pinkGhost));
                        break;
                    case 'P':
                        pacman = new Block(x, y, tileSize, tileSize, pacmanRight);
                        break;
                    default:
                        foods.add(new Block(x + 14, y + 14, 4, 4, null));
                        break;
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, this);
        }
        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, this);
        }
        if (pacman != null) {
            g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, this);
        }
        g.setColor(Color.YELLOW);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }
        // Draw a light rectangle to show stats clearly
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, 150, 100);
        g.setColor(Color.WHITE);
        g.setFont(getFont().deriveFont(16f));
        g.drawString("lives: " + lives, 10, 20);
        g.drawString("Level: " + level, 10, 40);
        g.drawString("Foods Left: " + foods.size(), 10, 60);
        g.drawString("Current Score: " + score, 10, 80);
    }

    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;
        for (Block wall : walls) {
            if (checkCollisions(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                pacman.updateMotion(' ');
                break; // Stop moving if collision occurs
            }
        }

        for (Block ghost : ghosts) {
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for (Block wall : walls) {
                if (checkCollisions(ghost, wall)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = ghostDirections[random.nextInt(ghostDirections.length)];
                    ghost.updateMotion(newDirection);
                    break; // Stop moving if collision occurs
                }
            }

            // Check for collision with Pacman
            if (checkCollisions(pacman, ghost)) {
                lives--;
                if (lives <= 0) {
                    gameLoop.stop();
                    int result = JOptionPane.showConfirmDialog(
                            null,
                            "Score: " + score + ". Want to play again?",
                            "Game Over",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);

                    if (result == JOptionPane.YES_OPTION) {
                        // Reset everything
                        resetGame();
                    } else {
                        System.exit(0); // Exit the game
                    }
                } else {
                    pacman.resetPosition();
                }
            }
        }

        Block foodEaten = null;
        for (Block food : foods) {
            if (checkCollisions(pacman, food)) {
                foodEaten = food;
                score += 10;
                break;
            }
        }
        foods.remove(foodEaten);

        // Check if all food is eaten
        if (foods.isEmpty()) {
            gameLoop.stop();
            if (level == 5) {
                JOptionPane.showMessageDialog(this, "Wow. Just...wow.\r\n" + //
                        "\r\n" + //
                        "Not possible! Did you seriously complete *every* level? You must have way too much free time—or some kind of neural implant for perfect reflexes.\r\n"
                        + //
                        "\r\n" + //
                        "I know you're probably lying. I won't believe it until you send me a screenshot of your glorious, impossible victory. Until then, I'm convinced you're making it up.\r\n"
                        + //
                        "\r\n" + //
                        "Anyway, congratulations (I guess). You're officially the Pac-Man Overlord.");
                System.exit(0);
            }
            JOptionPane.showMessageDialog(this, "You won! Let's move to the next level.");
            level++;
            resetGame();
        }
    }

    public boolean checkCollisions(Block a, Block b) {
        return (a.x < b.x + b.width && a.x + a.width > b.x &&
                a.y < b.y + b.height && a.y + a.height > b.y);
    }

    public void resetGame() {
        // Reset everything
        score = 0;
        lives = 3;
        walls.clear();
        ghosts.clear();
        foods.clear();
        loadMap();
        for (Block gh : ghosts) {
            char direction = ghostDirections[random.nextInt(ghostDirections.length)];
            gh.direction = direction;
            gh.changeDirection();
        }
        gameLoop.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
        move();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                pacman.updateMotion('U');
                break;
            case KeyEvent.VK_DOWN:
                pacman.updateMotion('D');
                break;
            case KeyEvent.VK_LEFT:
                pacman.updateMotion('L');
                break;
            case KeyEvent.VK_RIGHT:
                pacman.updateMotion('R');
                break;
        }
        switch (pacman.direction) {
            case 'U':
                pacman.image = pacmanUp;
                break;
            case 'D':
                pacman.image = pacmanDown;
                break;
            case 'L':
                pacman.image = pacmanLeft;
                break;
            case 'R':
                pacman.image = pacmanRight;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
