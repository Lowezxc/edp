package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import javax.sound.sampled.*;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 1280;
    static final int SCREEN_HEIGHT = 720;

    BufferedImage background, holeImg, holeBottomImg, pookieImg, bombImg, timerImg, menuImg, heartImg, powerupHeartImg, powerupClockImg, powerupStarImg;

    Difficulty gameDifficulty;
    int score = 0;
    int lives;
    int timeLeft;
    int difficultyLevel = 1;
    int highestScore = 0; // 🏆 Track the best score
    int bombProbability; // percentage
    int speedIncreaseInterval; // seconds
    int maxDifficultyLevel;
    int maxLives;
    int scoreMultiplier = 1;
    boolean slowMotionActive = false;
    int slowMotionTimeLeft = 0;
    boolean doublePointsActive = false;
    int doublePointsTimeLeft = 0;
    int timeTick = 0;

    Rectangle[] holePositions;
    ArrayList<PoppingObject> activeObjects = new ArrayList<>();

    Timer spawnTimer, gameTimer, animationTimer, effectTimer;
    Random random = new Random();
    Clip bgMusic;

    public GamePanel(Difficulty difficulty) {
        this.gameDifficulty = difficulty;
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setFocusable(true);
        this.setOpaque(false);

        // Set parameters based on difficulty
        switch (difficulty) {
            case EASY:
                lives = 5;
                maxLives = 5;
                timeLeft = 90;
                bombProbability = 10;
                speedIncreaseInterval = 15;
                maxDifficultyLevel = 3;
                break;
            case MEDIUM:
                lives = 3;
                maxLives = 3;
                timeLeft = 60;
                bombProbability = 25;
                speedIncreaseInterval = 10;
                maxDifficultyLevel = 4;
                break;
            case HARD:
                lives = 2;
                maxLives = 2;
                timeLeft = 45;
                bombProbability = 35;
                speedIncreaseInterval = 8;
                maxDifficultyLevel = 5;
                break;
        }

        try {
            background = ImageIO.read(getClass().getClassLoader().getResource("res/bg2.png"));
            holeImg    = ImageIO.read(getClass().getClassLoader().getResource("res/hole.png"));
            holeBottomImg = ImageIO.read(getClass().getClassLoader().getResource("res/holebottom.png"));
            pookieImg  = ImageIO.read(getClass().getClassLoader().getResource("res/pookie.png"));
            bombImg    = ImageIO.read(getClass().getClassLoader().getResource("res/bomb.png")); // pookie-with-bomb
            timerImg   = ImageIO.read(getClass().getClassLoader().getResource("res/timer.png"));
            menuImg    = ImageIO.read(getClass().getClassLoader().getResource("res/menu.png"));
            heartImg   = ImageIO.read(getClass().getClassLoader().getResource("res/heart.png"));
            powerupHeartImg = ImageIO.read(getClass().getClassLoader().getResource("res/heart.png"));
            powerupClockImg = ImageIO.read(getClass().getClassLoader().getResource("res/clock.png"));
            powerupStarImg  = ImageIO.read(getClass().getClassLoader().getResource("res/star.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ✅ Start background music
        playMusic("bgmusic.wav");

        // Center donuts dynamically
        holePositions = new Rectangle[4];
        int holeWidth = 150, holeHeight = 120, spacing = 40;
        int totalWidth = (holeWidth * 4) + (spacing * 3);
        int startX = (SCREEN_WIDTH - totalWidth) / 2, y = 500;

        for (int i = 0; i < 4; i++) {
            int x = startX + i * (holeWidth + spacing);
            holePositions[i] = new Rectangle(x, y, holeWidth, holeHeight);
        }

        int spawnDelay = switch (difficulty) {
            case EASY -> 3000;
            case MEDIUM -> 2000;
            case HARD -> 1500;
        };
        spawnTimer = new Timer(spawnDelay, this);
        spawnTimer.start();

        gameTimer = new Timer(1000, e -> {
            timeTick++;
            boolean shouldDecrementTime = !slowMotionActive || (timeTick % 2 == 0);
            if (shouldDecrementTime) {
                timeLeft--;
                if (timeLeft % speedIncreaseInterval == 0 && difficultyLevel < maxDifficultyLevel) {
                    difficultyLevel++;
                    spawnTimer.setDelay(Math.max(800, spawnTimer.getDelay() - 100));
                }
                if (timeLeft <= 0) gameOver();
            }
            repaint();
        });
        gameTimer.start();

        animationTimer = new Timer(100, e -> {
            boolean needRepaint = false;
            for (PoppingObject obj : activeObjects) {
                if (obj.animationFrame < 10) {
                    obj.animationFrame++;
                    needRepaint = true;
                }
            }
            if (needRepaint) repaint();
        });
        animationTimer.start();

        effectTimer = new Timer(1000, e -> {
            if (slowMotionActive) {
                slowMotionTimeLeft--;
                if (slowMotionTimeLeft <= 0) {
                    slowMotionActive = false;
                    spawnTimer.setDelay(switch (gameDifficulty) {
                        case EASY -> 3000;
                        case MEDIUM -> 2000;
                        case HARD -> 1500;
                    });
                }
            }
            if (doublePointsActive) {
                doublePointsTimeLeft--;
                if (doublePointsTimeLeft <= 0) {
                    doublePointsActive = false;
                    scoreMultiplier = 1;
                }
            }
            repaint();
        });
        effectTimer.start();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int menuSize = 75;
                int menuX = SCREEN_WIDTH - menuSize - 10;
                int menuY = 5;

                if (e.getX() >= menuX && e.getX() <= menuX + menuSize &&
                    e.getY() >= menuY && e.getY() <= menuY + menuSize) {

                    if (spawnTimer != null) spawnTimer.stop();
                    if (gameTimer != null) gameTimer.stop();
                    if (animationTimer != null) animationTimer.stop();
                    if (effectTimer != null) effectTimer.stop();
                    if (bgMusic != null) bgMusic.stop();

                    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(GamePanel.this);
                    topFrame.setContentPane(new PausePanel(topFrame, GamePanel.this));
                    topFrame.revalidate();
                } else {
                    checkClick(e.getPoint());
                }
            }
        });
    }

    // --------------------------
// POWERUP METHODS
// --------------------------
    void applyPowerup(PowerupType type) {
        switch (type) {
            case EXTRA_LIFE:
                lives++;
                playSoundEffect("chime.wav");
                break;
            case SLOW_MOTION:
                slowMotionActive = true;
                slowMotionTimeLeft = 10;
                timeTick = 0; // align the slow time
                spawnTimer.setDelay(spawnTimer.getDelay() * 2); // double the delay
                playSoundEffect("clock.wav");
                break;
            case DOUBLE_POINTS:
                doublePointsActive = true;
                doublePointsTimeLeft = 15;
                scoreMultiplier = 2;
                playSoundEffect("2x.wav");
                break;
        }
    }

    // --------------------------
// SOUND METHODS
// --------------------------
    public void playMusic(String filename) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                getClass().getClassLoader().getResource("res/" + filename));
            bgMusic = AudioSystem.getClip();
            bgMusic.open(audioStream);
            bgMusic.loop(Clip.LOOP_CONTINUOUSLY);
            bgMusic.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSoundEffect(String filename) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                getClass().getClassLoader().getResource("res/" + filename));
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --------------------------
    // CLICK DETECTION
    // --------------------------
    void checkClick(Point p) {
        for (int i = 0; i < activeObjects.size(); i++) {
            PoppingObject obj = activeObjects.get(i);
            if (obj.rect.contains(p) && !obj.clicked) {
                obj.clicked = true;

                if (obj.type.equals("pookie")) {
                    score += scoreMultiplier;
                    playSoundEffect("pop.wav");
                } else if (obj.type.equals("bomb")) {
                    lives--;
                    playSoundEffect("explosion.wav");
                    if (lives <= 0) {
                        gameOver();
                        return;
                    }
                } else if (obj.type.equals("powerup")) {
                    applyPowerup(obj.powerupType);
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
        g.drawImage(background, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);

        for (Rectangle h : holePositions) {
            g.drawImage(holeImg, h.x, h.y, h.width, h.height, this);
        }

        for (PoppingObject obj : activeObjects) {
            if (obj.type.equals("pookie")) {
                int pookieWidth = 120, pookieHeight = 120;
                int pookieX = obj.rect.x + (obj.rect.width - pookieWidth) / 2;
                int offsetY = (obj.animationFrame < 5) ? -obj.animationFrame * 5 : -25;
                int pookieY = obj.rect.y + (obj.rect.height - pookieHeight) / 2 + offsetY;

                g.drawImage(pookieImg, pookieX, pookieY, pookieWidth, pookieHeight, this);
            } else if (obj.type.equals("bomb")) {
                int bombWidth = 120, bombHeight = 120;
                int bombX = obj.rect.x + (obj.rect.width - bombWidth) / 2;
                int offsetY = (obj.animationFrame < 5) ? -obj.animationFrame * 5 : -25;
                int bombY = obj.rect.y + (obj.rect.height - bombHeight) / 2 + offsetY;

                g.drawImage(bombImg, bombX, bombY, bombWidth, bombHeight, this);
            } else if (obj.type.equals("powerup")) {
                BufferedImage img = switch (obj.powerupType) {
                    case EXTRA_LIFE -> powerupHeartImg;
                    case SLOW_MOTION -> powerupClockImg;
                    case DOUBLE_POINTS -> powerupStarImg;
                };
                int pw = 60, ph = 60;
                int px = obj.rect.x + (obj.rect.width - pw) / 2;
                int offsetY = (obj.animationFrame < 5) ? -obj.animationFrame * 5 : -25;
                int py = obj.rect.y + (obj.rect.height - ph) / 2 + offsetY;

                g.drawImage(img, px, py, pw, ph, this);
            }

            // Draw bottom of hole on top of the object, sized like the full hole
            g.drawImage(holeBottomImg, obj.rect.x - 25, obj.rect.y - 10, 150, 120, this);
        }

        for (int i = 0; i < lives; i++) {
            g.drawImage(heartImg, 20 + (i * 40), 20, 30, 30, this);
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 20, 70);

        int topRightX = SCREEN_WIDTH - 200;
        int iconSize = 40;
        g.drawImage(timerImg, topRightX, 20, iconSize, iconSize, this);
        g.drawString(timeLeft + "s", topRightX + 50, 50);

        int menuSize = 75;
        int menuX = SCREEN_WIDTH - menuSize - 10;
        int menuY = 5;
        g.drawImage(menuImg, menuX, menuY, menuSize, menuSize, this);

        // Effect indicators
        int effectY = 80;
        if (slowMotionActive) {
            g.drawString("Slow Motion: " + slowMotionTimeLeft + "s", 150, effectY);
            effectY += 30;
        }
        if (doublePointsActive) {
            g.drawString("Double Points: " + doublePointsTimeLeft + "s", 150, effectY);
        }
    }

    // --------------------------
    // SPAWN OBJECTS
    // --------------------------
    @Override
    public void actionPerformed(ActionEvent e) {
        activeObjects.clear();
        HashSet<Integer> usedHoles = new HashSet<>();
        for (int i = 0; i < difficultyLevel; i++) {
            int holeIndex;
            do {
                holeIndex = random.nextInt(holePositions.length);
            } while (usedHoles.contains(holeIndex));
            usedHoles.add(holeIndex);

            Rectangle h = holePositions[holeIndex];
            Rectangle pRect = new Rectangle(h.x + 25, h.y + 10, 100, 100);
            int rand = random.nextInt(100);
            PoppingObject obj;
            if (rand < bombProbability) {
                obj = new PoppingObject(pRect, "bomb");
            } else if (rand < bombProbability + 4 && lives < maxLives) { // extra life 4%, only if not max
                obj = new PoppingObject(pRect, PowerupType.EXTRA_LIFE);
            } else if (rand < bombProbability + 4 + 3) { // slow motion 3%
                obj = new PoppingObject(pRect, PowerupType.SLOW_MOTION);
            } else if (rand < bombProbability + 4 + 3 + 3) { // double points 3%
                obj = new PoppingObject(pRect, PowerupType.DOUBLE_POINTS);
            } else {
                obj = new PoppingObject(pRect, "pookie");
            }
            activeObjects.add(obj);
        }

        repaint();
    }

    // --------------------------
    // GAME OVER
    // --------------------------
    void gameOver() {

        playSoundEffect("gameover.wav");

        if (spawnTimer != null) spawnTimer.stop();
        if (gameTimer != null) gameTimer.stop();
        if (animationTimer != null) animationTimer.stop();
        if (effectTimer != null) effectTimer.stop();
        if (bgMusic != null) bgMusic.stop();

        // 🏆 Check for new high score
        if (score > highestScore) {
            highestScore = score;

            // Celebration sound + message
            playSoundEffect("yay.wav");
            showCelebrationDialog("Score: " + score);
        }

        UIManager.put("OptionPane.background", new Color(255, 228, 240));
        UIManager.put("Panel.background", new Color(255, 228, 240));
        UIManager.put("OptionPane.messageForeground", new Color(120, 40, 80));
        UIManager.put("Button.background", new Color(255, 182, 193));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", new Font("Serif", Font.BOLD, 16));
        UIManager.put("OptionPane.font", new Font("Serif", Font.BOLD, 18));

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Game Over!\nScore: " + score + "\nWould you like to play again? 🎀",
            "Peek-A-Pookie ✨",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        if (choice == JOptionPane.YES_OPTION) {
            // Reset game state
            score = 0;
            lives = maxLives;
            switch (gameDifficulty) {
                case EASY:
                    timeLeft = 90;
                    break;
                case MEDIUM:
                    timeLeft = 60;
                    break;
                case HARD:
                    timeLeft = 45;
                    break;
            }
            difficultyLevel = 1;
            timeTick = 0;
            activeObjects.clear();

            int spawnDelay = switch (gameDifficulty) {
                case EASY -> 3000;
                case MEDIUM -> 2000;
                case HARD -> 1500;
            };
            spawnTimer.setDelay(spawnDelay); // reset spawn speed
            spawnTimer.start();
            gameTimer.start();
            animationTimer.start();
            effectTimer.start();
            // reset effects
            slowMotionActive = false;
            doublePointsActive = false;
            scoreMultiplier = 1;

            // restart background music
            if (bgMusic != null) {
                bgMusic.setFramePosition(0);
                bgMusic.start();
            }

            repaint();
        } else {
            // 🎀 Return to main menu instead of exiting
            topFrame.setContentPane(new MenuPanel(topFrame));
            topFrame.revalidate();
        }
    }

    // --------------------------
    // CUSTOM CELEBRATION DIALOG
    // --------------------------
    private void showCelebrationDialog(String message) {
        JDialog dialog = new JDialog((Frame) null, "Peek-A-Pookie ✨", true);
        dialog.setSize(400, 250);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 228, 240)); // pastel pink
        panel.setLayout(new BorderLayout());

        JLabel title = new JLabel("🎉 New High Score! 🎉", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setForeground(new Color(120, 40, 80)); // deep rose
        panel.add(title, BorderLayout.NORTH);

        JLabel msg = new JLabel(message, SwingConstants.CENTER);
        msg.setFont(new Font("Serif", Font.PLAIN, 18));
        msg.setForeground(new Color(80, 20, 60));
        panel.add(msg, BorderLayout.CENTER);

        JButton okButton = new JButton("Yay!");
        okButton.setBackground(new Color(255, 182, 193));
        okButton.setForeground(Color.WHITE);
        okButton.setFont(new Font("Serif", Font.BOLD, 16));
        okButton.addActionListener(e -> dialog.dispose());
        panel.add(okButton, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    // --------------------------
// OBJECT CLASS
// --------------------------
    class PoppingObject {
        Rectangle rect;
        String type;
        PowerupType powerupType;
        int animationFrame = 0;
        boolean clicked = false; // ✅ prevents mass scoring

        public PoppingObject(Rectangle r, String t) {
            rect = r;
            type = t;
            powerupType = null;
        }

        public PoppingObject(Rectangle r, PowerupType pt) {
            rect = r;
            type = "powerup";
            powerupType = pt;
        }
    }
}
