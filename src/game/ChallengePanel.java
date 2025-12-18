package game;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class ChallengePanel extends JPanel {

    private BufferedImage background;

    public ChallengePanel(JFrame frame) {
        this.setPreferredSize(new Dimension(GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT));
        this.setLayout(null);

        // Load background images
        try {
            background = ImageIO.read(getClass().getClassLoader().getResource("res/bg.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Title
        RoundedButton titleButton = new RoundedButton("Select Challenge");
        titleButton.setBounds(GamePanel.SCREEN_WIDTH/2 - 150, 40, 300, 60);
        titleButton.setFont(new Font("Serif", Font.BOLD, 36));
        this.add(titleButton);

        // Mission buttons
        int y = 150;
        for (Mission mission : Mission.values()) {
            RoundedButton missionButton = new RoundedButton(mission.description);
            missionButton.setBounds(GamePanel.SCREEN_WIDTH/2 - 250, y, 500, 80);
            missionButton.setFont(new Font("Serif", Font.BOLD, 18));
            missionButton.addActionListener(e -> startChallenge(frame, mission));
            this.add(missionButton);
            y += 100;
        }

        // Back to Menu button
        RoundedButton backButton = new RoundedButton("Back to Menu");
        backButton.setBounds(GamePanel.SCREEN_WIDTH/2 - 150, 600, 300, 60);
        backButton.setFont(new Font("Serif", Font.BOLD, 28));
        backButton.addActionListener(e -> {
            frame.setContentPane(new MenuPanel(frame));
            frame.revalidate();
        });
        this.add(backButton);
    }

    private void startChallenge(JFrame frame, Mission mission) {
        // Start with HARD difficulty for challenges
        frame.setContentPane(new GamePanel(Difficulty.HARD, mission));
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