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

    BufferedImage background, holeImg, pookieImg, bombImg, timerImg, menuImg, heartImg;

    int score = 0;
    int lives = 3;
    int timeLeft = 60;
    int difficulty = 1;
    int highestScore = 0; // 🏆 Track the best score

    Rectangle[] holePositions;
    ArrayList<PoppingObject> activeObjects = new ArrayList<>();

    Timer spawnTimer, gameTimer, animationTimer;
    Random random = new Random();
    Clip bgMusic;

    public GamePanel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setFocusable(true);
        this.setOpaque(false);

        try {
            background = ImageIO.read(getClass().getClassLoader().getResource("res/background.png"));
            holeImg    = ImageIO.read(getClass().getClassLoader().getResource("res/hole.png"));
            pookieImg  = ImageIO.read(getClass().getClassLoader().getResource("res/pookie.png"));
            bombImg    = ImageIO.read(getClass().getClassLoader().getResource("res/bomb.png")); // pookie-with-bomb
            timerImg   = ImageIO.read(getClass().getClassLoader().getResource("res/timer.png"));
            menuImg    = ImageIO.read(getClass().getClassLoader().getResource("res/menu.png"));
            heartImg   = ImageIO.read(getClass().getClassLoader().getResource("res/heart.png"));
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

        spawnTimer = new Timer(2000, this);
        spawnTimer.start();

        gameTimer = new Timer(1000, e -> {
            timeLeft--;
            if (timeLeft % 10 == 0 && difficulty < 4) {
                difficulty++;
                spawnTimer.setDelay(Math.max(800, spawnTimer.getDelay() - 100));
            }
            if (timeLeft <= 0) gameOver();
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
                    score++;
                    playSoundEffect("pop.wav");
                } else if (obj.type.equals("bomb")) {
                    lives--;
                    playSoundEffect("explosion.wav");
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
        g.drawImage(background, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);

        for (Rectangle h : holePositions) {
            g.drawImage(holeImg, h.x, h.y, h.width, h.height, this);
        }

        for (PoppingObject obj : activeObjects) {
            if (obj.type.equals("pookie")) {
                int pookieWidth = 130, pookieHeight = 130;
                int pookieX = obj.rect.x + (obj.rect.width - pookieWidth) / 2;
                int offsetY = (obj.animationFrame < 5) ? -obj.animationFrame * 3 : -15;
                int pookieY = obj.rect.y - 40 + offsetY;

                g.drawImage(pookieImg, pookieX, pookieY, pookieWidth, pookieHeight, this);
            } else if (obj.type.equals("bomb")) {
                int bombWidth = 130, bombHeight = 130;
                int bombX = obj.rect.x + (obj.rect.width - bombWidth) / 2;
                int offsetY = (obj.animationFrame < 5) ? -obj.animationFrame * 3 : -15;
                int bombY = obj.rect.y - 40 + offsetY;

                g.drawImage(bombImg, bombX, bombY, bombWidth, bombHeight, this);
            }
        }

        for (int i = 0; i < lives; i++) {
            g.drawImage(heartImg, 20 + (i * 40), 20, 30, 30, this);
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 150, 45);

        int topRightX = SCREEN_WIDTH - 200;
        int iconSize = 40;
        g.drawImage(timerImg, topRightX, 20, iconSize, iconSize, this);
        g.drawString(timeLeft + "s", topRightX + 50, 50);

        int menuSize = 75;
        int menuX = SCREEN_WIDTH - menuSize - 10;
        int menuY = 5;
        g.drawImage(menuImg, menuX, menuY, menuSize, menuSize, this);
    }

    // --------------------------
    // SPAWN OBJECTS
    // --------------------------
    @Override
    public void actionPerformed(ActionEvent e) {
        activeObjects.clear();
        HashSet<Integer> usedHoles = new HashSet<>();
        for (int i = 0; i < difficulty; i++) {
            int holeIndex;
            do {
                holeIndex = random.nextInt(holePositions.length);
            } while (usedHoles.contains(holeIndex));
            usedHoles.add(holeIndex);

            Rectangle h = holePositions[holeIndex];
            Rectangle pRect = new Rectangle(h.x + 35, h.y - 100, 100, 100);
                       String type = (random.nextInt(100) < 75) ? "pookie" : "bomb";
            activeObjects.add(new PoppingObject(pRect, type));
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
            lives = 3;
            timeLeft = 60;
            difficulty = 1;
            activeObjects.clear();

            spawnTimer.setDelay(2000); // reset spawn speed
            spawnTimer.start();
            gameTimer.start();
            animationTimer.start();

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
        int animationFrame = 0;
        boolean clicked = false; // ✅ prevents mass scoring

        public PoppingObject(Rectangle r, String t) {
            rect = r;
            type = t;
        }
    }
}
