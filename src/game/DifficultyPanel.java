package game;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class DifficultyPanel extends JPanel {

    private BufferedImage background;

    public DifficultyPanel(JFrame frame) {
        this.setPreferredSize(new Dimension(GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT));
        this.setLayout(null);

        // Load background images
        try {
            background = ImageIO.read(getClass().getClassLoader().getResource("res/bg.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Title
        RoundedButton titleButton = new RoundedButton("Select Difficulty");
        titleButton.setBounds(GamePanel.SCREEN_WIDTH/2 - 200, 40, 400, 80);
        titleButton.setFont(new Font("Serif", Font.BOLD, 36));
        this.add(titleButton);

        // Easy button
        RoundedButton easyButton = new RoundedButton("Easy\n5 lives, 90s, Relaxed");
        easyButton.setBounds(GamePanel.SCREEN_WIDTH/2 - 200, 200, 400, 100);
        easyButton.setFont(new Font("Serif", Font.BOLD, 20));
        this.add(easyButton);

        // Medium button
        RoundedButton mediumButton = new RoundedButton("Medium\n3 lives, 60s, Balanced");
        mediumButton.setBounds(GamePanel.SCREEN_WIDTH/2 - 200, 320, 400, 100);
        mediumButton.setFont(new Font("Serif", Font.BOLD, 20));
        this.add(mediumButton);

        // Hard button
        RoundedButton hardButton = new RoundedButton("Hard\n2 lives, 45s,Intense");
        hardButton.setBounds(GamePanel.SCREEN_WIDTH/2 - 200, 440, 400, 100);
        hardButton.setFont(new Font("Serif", Font.BOLD, 20));
        this.add(hardButton);

        // Back to Menu button
        RoundedButton backButton = new RoundedButton("Back to Menu");
        backButton.setBounds(GamePanel.SCREEN_WIDTH/2 - 150, 580, 300, 60);
        backButton.setFont(new Font("Serif", Font.BOLD, 28));
        this.add(backButton);

        // Action listeners
        easyButton.addActionListener(e -> startGame(frame, Difficulty.EASY));
        mediumButton.addActionListener(e -> startGame(frame, Difficulty.MEDIUM));
        hardButton.addActionListener(e -> startGame(frame, Difficulty.HARD));

        backButton.addActionListener(e -> {
            frame.setContentPane(new MenuPanel(frame));
            frame.revalidate();
        });
    }

    private void startGame(JFrame frame, Difficulty difficulty) {
        frame.setContentPane(new GamePanel(difficulty));
        frame.revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT, this);
        }
    }
}