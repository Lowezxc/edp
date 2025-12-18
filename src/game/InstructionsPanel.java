package game;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class InstructionsPanel extends JPanel {

    private BufferedImage background;

    public InstructionsPanel(JFrame frame) {
        this.setPreferredSize(new Dimension(GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT));
        this.setLayout(null);

        // Load background images
        try {
            background = ImageIO.read(getClass().getClassLoader().getResource("res/bg.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Instructions panel
        JPanel instructionsPanel = new JPanel();
        instructionsPanel.setLayout(new BoxLayout(instructionsPanel, BoxLayout.Y_AXIS));
        instructionsPanel.setBackground(new Color(255, 228, 240, 200));
        instructionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(instructionsPanel);
        scrollPane.setBounds(200, 100, 880, 450);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(255, 228, 240, 200));

        // Title
        JLabel title = new JLabel("Welcome to Peek-A-Pookie! 🎀");
        title.setFont(new Font("Serif", Font.BOLD, 28));
        title.setForeground(new Color(120, 40, 80));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionsPanel.add(title);
        instructionsPanel.add(Box.createVerticalStrut(20));

        // Goal
        JLabel goal = new JLabel("Your goal is to pop as many cute pookies as possible!");
        goal.setFont(new Font("Serif", Font.PLAIN, 20));
        goal.setForeground(new Color(120, 40, 80));
        goal.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionsPanel.add(goal);
        instructionsPanel.add(Box.createVerticalStrut(20));

        // Pookie
        JPanel pookiePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pookiePanel.setBackground(new Color(0,0,0,0));
        ImageIcon pookieImg = new ImageIcon(getClass().getClassLoader().getResource("res/pookie.png"));
        Image scaledPookie = pookieImg.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel pookieIcon = new JLabel(new ImageIcon(scaledPookie));
        pookiePanel.add(pookieIcon);
        JLabel pookieText = new JLabel("Click on the pookies that pop up from the holes to score points.");
        pookieText.setFont(new Font("Serif", Font.PLAIN, 18));
        pookieText.setForeground(new Color(120, 40, 80));
        pookiePanel.add(pookieText);
        instructionsPanel.add(pookiePanel);
        instructionsPanel.add(Box.createVerticalStrut(10));

        // Bomb
        JPanel bombPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bombPanel.setBackground(new Color(0,0,0,0));
        ImageIcon bombImg = new ImageIcon(getClass().getClassLoader().getResource("res/bomb.png"));
        Image scaledBomb = bombImg.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel bombIcon = new JLabel(new ImageIcon(scaledBomb));
        bombPanel.add(bombIcon);
        JLabel bombText = new JLabel("Avoid clicking on the bombs, or you'll lose a life.");
        bombText.setFont(new Font("Serif", Font.PLAIN, 18));
        bombText.setForeground(new Color(120, 40, 80));
        bombPanel.add(bombText);
        instructionsPanel.add(bombPanel);
        instructionsPanel.add(Box.createVerticalStrut(10));

        // Powerups
        JLabel powerupsTitle = new JLabel("Powerups:");
        powerupsTitle.setFont(new Font("Serif", Font.BOLD, 20));
        powerupsTitle.setForeground(new Color(120, 40, 80));
        powerupsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionsPanel.add(powerupsTitle);
        instructionsPanel.add(Box.createVerticalStrut(10));

        // Extra Life
        JPanel lifePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lifePanel.setBackground(new Color(0,0,0,0));
        ImageIcon heartImg = new ImageIcon(getClass().getClassLoader().getResource("res/heart.png"));
        Image scaledHeart = heartImg.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        JLabel lifeIcon = new JLabel(new ImageIcon(scaledHeart));
        lifePanel.add(lifeIcon);
        JLabel lifeText = new JLabel("Extra Life - Grants +1 life instantly.");
        lifeText.setFont(new Font("Serif", Font.PLAIN, 16));
        lifeText.setForeground(new Color(120, 40, 80));
        lifePanel.add(lifeText);
        instructionsPanel.add(lifePanel);
        instructionsPanel.add(Box.createVerticalStrut(5));

        // Slow Motion
        JPanel slowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        slowPanel.setBackground(new Color(0,0,0,0));
        ImageIcon clockImg = new ImageIcon(getClass().getClassLoader().getResource("res/clock.png"));
        Image scaledClock = clockImg.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        JLabel slowIcon = new JLabel(new ImageIcon(scaledClock));
        slowPanel.add(slowIcon);
        JLabel slowText = new JLabel("Slow Motion - Slows spawning and time for 10 seconds.");
        slowText.setFont(new Font("Serif", Font.PLAIN, 16));
        slowText.setForeground(new Color(120, 40, 80));
        slowPanel.add(slowText);
        instructionsPanel.add(slowPanel);
        instructionsPanel.add(Box.createVerticalStrut(5));

        // Double Points
        JPanel doublePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        doublePanel.setBackground(new Color(0,0,0,0));
        ImageIcon starImg = new ImageIcon(getClass().getClassLoader().getResource("res/star.png"));
        Image scaledStar = starImg.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        JLabel doubleIcon = new JLabel(new ImageIcon(scaledStar));
        doublePanel.add(doubleIcon);
        JLabel doubleText = new JLabel("Double Points - Doubles score for 15 seconds.");
        doubleText.setFont(new Font("Serif", Font.PLAIN, 16));
        doubleText.setForeground(new Color(120, 40, 80));
        doublePanel.add(doubleText);
        instructionsPanel.add(doublePanel);
        instructionsPanel.add(Box.createVerticalStrut(20));

        // General info
        JLabel general = new JLabel("You have 3 lives and 60 seconds. The game gets faster over time.");
        general.setFont(new Font("Serif", Font.PLAIN, 18));
        general.setForeground(new Color(120, 40, 80));
        general.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionsPanel.add(general);
        instructionsPanel.add(Box.createVerticalStrut(10));

        // Difficulties
        JLabel diffTitle = new JLabel("Difficulties:");
        diffTitle.setFont(new Font("Serif", Font.BOLD, 20));
        diffTitle.setForeground(new Color(120, 40, 80));
        diffTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionsPanel.add(diffTitle);
        instructionsPanel.add(Box.createVerticalStrut(10));

        JLabel easy = new JLabel("Easy: 5 lives, 90 seconds, slower pace");
        easy.setFont(new Font("Serif", Font.PLAIN, 16));
        easy.setForeground(new Color(120, 40, 80));
        easy.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionsPanel.add(easy);
        instructionsPanel.add(Box.createVerticalStrut(5));

        JLabel medium = new JLabel("Medium: 3 lives, 60 seconds, balanced");
        medium.setFont(new Font("Serif", Font.PLAIN, 16));
        medium.setForeground(new Color(120, 40, 80));
        medium.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionsPanel.add(medium);
        instructionsPanel.add(Box.createVerticalStrut(5));

        JLabel hard = new JLabel("Hard: 2 lives, 45 seconds, intense challenge");
        hard.setFont(new Font("Serif", Font.PLAIN, 16));
        hard.setForeground(new Color(120, 40, 80));
        hard.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionsPanel.add(hard);
        instructionsPanel.add(Box.createVerticalStrut(20));

        JLabel fun = new JLabel("Have fun! ✨");
        fun.setFont(new Font("Serif", Font.PLAIN, 18));
        fun.setForeground(new Color(120, 40, 80));
        fun.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionsPanel.add(fun);
        instructionsPanel.add(Box.createVerticalStrut(50));

        this.add(scrollPane);

        // Back to Menu button
        RoundedButton backButton = new RoundedButton("Back to Menu");
        backButton.setBounds(GamePanel.SCREEN_WIDTH/2 - 150, 600, 300, 60);
        backButton.setFont(new Font("Serif", Font.BOLD, 28));
        this.add(backButton);

        backButton.addActionListener(e -> {
            frame.setContentPane(new MenuPanel(frame));
            frame.revalidate();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT, this);
        }

        // Draw title
        g.setFont(new Font("SansSerif", Font.BOLD, 36));
        FontMetrics fm = g.getFontMetrics();
        int titleWidth = fm.stringWidth("How to Play");
        int titleX = (GamePanel.SCREEN_WIDTH - titleWidth) / 2;
        int titleY = 70;

        // Shadow
        g.setColor(new Color(80, 20, 60));
        g.drawString("How to Play", titleX + 1, titleY + 1);

        // Main text
        g.setColor(new Color(120, 40, 80));
        g.drawString("How to Play", titleX, titleY);
    }
}