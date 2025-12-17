package game;

import javax.swing.*;
import java.awt.*;

public class PausePanel extends JPanel {

    private Difficulty difficulty;

    public PausePanel(JFrame frame, GamePanel gamePanel) {
        this.difficulty = gamePanel.gameDifficulty;
        this.setPreferredSize(new Dimension(GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT));
        this.setLayout(null);

        // Title
        JLabel pauseLabel = new JLabel("Paused", SwingConstants.CENTER);
        pauseLabel.setFont(new Font("Serif", Font.BOLD, 48));
        pauseLabel.setBounds(GamePanel.SCREEN_WIDTH/2 - 200, 100, 400, 80);
        pauseLabel.setForeground(Color.WHITE);
        this.add(pauseLabel);

        // Continue button
        RoundedButton continueButton = new RoundedButton("Continue");
        continueButton.setBounds(GamePanel.SCREEN_WIDTH/2 - 150, 250, 300, 60);
        continueButton.setFont(new Font("Serif", Font.BOLD, 28));
        continueButton.addActionListener(e -> {
            frame.setContentPane(gamePanel); // return to same game
            frame.revalidate();
            gamePanel.spawnTimer.start();
            gamePanel.gameTimer.start();
            gamePanel.animationTimer.start();
            gamePanel.effectTimer.start();
        });
        this.add(continueButton);

        // Restart button
        RoundedButton restartButton = new RoundedButton("Restart");
        restartButton.setBounds(GamePanel.SCREEN_WIDTH/2 - 150, 350, 300, 60);
        restartButton.setFont(new Font("Serif", Font.BOLD, 28));
        restartButton.addActionListener(e -> {
            frame.setContentPane(new GamePanel(difficulty));
            frame.revalidate();
        });
        this.add(restartButton);

        // Quit to Menu button
        RoundedButton quitButton = new RoundedButton("Quit to Menu");
        quitButton.setBounds(GamePanel.SCREEN_WIDTH/2 - 150, 450, 300, 60);
        quitButton.setFont(new Font("Serif", Font.BOLD, 28));
        quitButton.addActionListener(e -> {
            frame.setContentPane(new MenuPanel(frame));
            frame.revalidate();
        });
        this.add(quitButton);
    }
}
