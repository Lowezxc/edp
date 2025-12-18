package game;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class MenuPanel extends JPanel {

    private BufferedImage background;

    public MenuPanel(JFrame frame) {
        this.setPreferredSize(new Dimension(GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT));
        this.setLayout(null);

        // Load background images
        try {
            background = ImageIO.read(getClass().getClassLoader().getResource("res/bg.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Start Game button
        RoundedButton startButton = new RoundedButton("Start Game");
        startButton.setFont(new Font("Serif", Font.PLAIN, 36));
        startButton.setBounds(GamePanel.SCREEN_WIDTH/2 - 150, 480, 300, 60);
        startButton.addActionListener(e -> {
            // Stop any running timers from the old GamePanel
            if (frame.getContentPane() instanceof GamePanel gp) {
                if (gp.spawnTimer != null) gp.spawnTimer.stop();
                if (gp.gameTimer != null) gp.gameTimer.stop();
                if (gp.animationTimer != null) gp.animationTimer.stop();
            }
            // Go to difficulty selection
            frame.setContentPane(new DifficultyPanel(frame));
            frame.revalidate();
        });
        this.add(startButton);

        // How to Play button
        RoundedButton howToPlayButton = new RoundedButton("How to Play");
        howToPlayButton.setFont(new Font("Serif", Font.PLAIN, 36));
        howToPlayButton.setBounds(GamePanel.SCREEN_WIDTH/2 - 150, 550, 300, 60);
        howToPlayButton.addActionListener(e -> {
            frame.setContentPane(new InstructionsPanel(frame));
            frame.revalidate();
        });
        this.add(howToPlayButton);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT, this);
        }

        // Draw title with shadow
        g.setFont(new Font("SansSerif", Font.BOLD, 36));
        FontMetrics fm = g.getFontMetrics();
        int titleWidth = fm.stringWidth("Peek-A-Pookie");
        int titleX = (GamePanel.SCREEN_WIDTH - titleWidth) / 2;
        int titleY = 80;

        // Shadow
        g.setColor(new Color(80, 20, 60));
        g.drawString("Peek-A-Pookie", titleX + 1, titleY + 1);

        // Main text
        g.setColor(new Color(120, 40, 80));
        g.drawString("Peek-A-Pookie", titleX, titleY);

        // Ribbons
        g.setFont(new Font("Serif", Font.PLAIN, 36));
        g.drawString("🎀", titleX - 50, titleY);
        g.drawString("🎀", titleX + titleWidth + 10, titleY);
    }
}
