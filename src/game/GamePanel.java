package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 1280;
    static final int SCREEN_HEIGHT = 720;

    // Images
    Image background;
    Image holeImg;
    Image pookieImg;
    Image bombImg;
    Image timerImg;
    Image menuImg;
    Image heartImg;

    // Game data
    int score = 0;
    int lives = 3;      // still needed for bombs
    int timeLeft = 60;

    int difficulty = 1;

    // Holes (donuts)
    Rectangle[] holePositions;

    // Active objects (Pookie or Bomb)
    ArrayList<PoppingObject> activeObjects = new ArrayList<>();

    // Timers
    Timer spawnTimer;
    Timer gameTimer;

    Random random = new Random();


    public GamePanel() {

        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setFocusable(true);

        // Load images from classpath (in bin/ directory)
        background = new ImageIcon(getClass().getClassLoader().getResource("background.png")).getImage();
        holeImg    = new ImageIcon(getClass().getClassLoader().getResource("hole.png")).getImage();
        pookieImg  = new ImageIcon(getClass().getClassLoader().getResource("pookie.png")).getImage();
        bombImg    = new ImageIcon(getClass().getClassLoader().getResource("bomb.png")).getImage();
        timerImg   = new ImageIcon(getClass().getClassLoader().getResource("timer.png")).getImage();
        menuImg    = new ImageIcon(getClass().getClassLoader().getResource("menu.png")).getImage();
        heartImg   = new ImageIcon(getClass().getClassLoader().getResource("heart.png")).getImage();

        // Candy world donut placement (matches your screenshot)
        holePositions = new Rectangle[] {
                new Rectangle(380, 500, 150, 120),
                new Rectangle(560, 500, 150, 120),
                new Rectangle(740, 500, 150, 120),
                new Rectangle(920, 500, 150, 120)
        };

        // Spawn timer
        spawnTimer = new Timer(1200, this);
        spawnTimer.start();

        // Countdown timer
        gameTimer = new Timer(1000, e -> {
            timeLeft--;

            if (timeLeft % 10 == 0 && difficulty < 4) {
                difficulty++;
                spawnTimer.setDelay(Math.max(400, spawnTimer.getDelay() - 200));
            }

            if (timeLeft <= 0) {
                gameOver();
            }
            repaint();
        });
        gameTimer.start();

        // Player clicks
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Check if menu button was clicked (top-right area)
                if (e.getX() >= SCREEN_WIDTH - 50 && e.getX() <= SCREEN_WIDTH - 20 &&
                    e.getY() >= 20 && e.getY() <= 50) {
                    // Toggle pause state
                    if (spawnTimer.isRunning()) {
                        spawnTimer.stop();
                        gameTimer.stop();
                    } else {
                        spawnTimer.start();
                        gameTimer.start();
                    }
                } else {
                    // Handle game clicks
                    checkClick(e.getPoint());
                }
            }
        });
    }


    // --------------------------
    // CLICK DETECTION
    // --------------------------
    void checkClick(Point p) {

        for (int i = 0; i < activeObjects.size(); i++) {

            PoppingObject obj = activeObjects.get(i);

            if (obj.rect.contains(p)) {

                if (obj.type.equals("pookie")) {
                    score++;
                }
                else if (obj.type.equals("bomb")) {
                    lives--;
                    if (lives <= 0) {
                        gameOver();
                        return;
                    }
                }

                activeObjects.remove(i);
                repaint();
                return;
            }
        }
    }



    // --------------------------
    // DRAW EVERYTHING
    // --------------------------
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background
        g.drawImage(background, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);

        // Draw donut holes
        for (Rectangle h : holePositions) {
            g.drawImage(holeImg, h.x, h.y, h.width, h.height, this);
        }

        // Draw active objects (pookies and bombs)
        for (PoppingObject obj : activeObjects) {
            if (obj.type.equals("pookie")) {
                // Draw pookie emerging from hole
                int pookieWidth = 100;  // Fixed width for pookie
                int pookieHeight = 100; // Fixed height for pookie
                int pookieX = obj.rect.x + (obj.rect.width - pookieWidth) / 2;
                int pookieY = obj.rect.y - 30; // Slightly above hole
                
                g.drawImage(pookieImg, 
                    pookieX, pookieY, 
                    pookieWidth, pookieHeight, 
                    this);
                    
                // Draw bomb above pookie if it's a bomb type
                if (obj.hasBomb) {
                    g.drawImage(bombImg, 
                        pookieX + 30, pookieY - 40, 
                        40, 40, 
                        this);
                }
            }
        }

        // Draw lives (hearts) - top left
        for (int i = 0; i < lives; i++) {
            g.drawImage(heartImg, 20 + (i * 40), 20, 30, 30, this);
        }
        
        // Draw score - next to lives
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 150, 45);

        // Draw timer (hourglass) - top right
        g.drawImage(timerImg, SCREEN_WIDTH - 100, 20, 30, 30, this);
        g.drawString(timeLeft + "s", SCREEN_WIDTH - 60, 45);

        // Menu button - top right (hamburger menu)
        g.drawImage(menuImg, SCREEN_WIDTH - 50, 20, 30, 30, this);
    }



    // --------------------------
    // SPAWN OBJECTS
    // --------------------------
    @Override
    public void actionPerformed(ActionEvent e) {

        activeObjects.clear();

        for (int i = 0; i < difficulty; i++) {

            int holeIndex = random.nextInt(holePositions.length);
            Rectangle h = holePositions[holeIndex];

            // Objects rise from donut hole (just like screenshot)
            Rectangle pRect = new Rectangle(
                    h.x + 35,
                    h.y - 100,
                    100,
                    100
            );

            // 75% Pookie, 25% Bomb
            String type = (random.nextInt(100) < 75) ? "pookie" : "bomb";

            activeObjects.add(new PoppingObject(pRect, type));
        }

        repaint();
    }



    // --------------------------
    // GAME OVER
    // --------------------------
    void gameOver() {

        spawnTimer.stop();
        gameTimer.stop();

        JOptionPane.showMessageDialog(this,
                "Game Over!\nScore: " + score,
                "Peek-A-Pookie",
                JOptionPane.INFORMATION_MESSAGE);
    }



    // --------------------------
    // OBJECT CLASS
    // --------------------------
    class PoppingObject {
        Rectangle rect;
        String type;
        boolean hasBomb;

        public PoppingObject(Rectangle r, String t) {
            rect = r;
            type = t;
            hasBomb = (Math.random() < 0.3); // 30% chance for a bomb
        }
    }
}
