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

        // Load background image
        try {
            background = ImageIO.read(getClass().getClassLoader().getResource("res/background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Title styled as rounded oblong with ribbons
        RoundedButton titleButton = new RoundedButton("Peek-A-Pookie");
        titleButton.setBounds(GamePanel.SCREEN_WIDTH/2 - 200, 40, 400, 80); // uppermost center
        titleButton.setFont(new Font("Serif", Font.BOLD, 36));
        this.add(titleButton);

        JLabel leftRibbon = new JLabel("🎀");
        leftRibbon.setFont(new Font("Serif", Font.PLAIN, 36));
        leftRibbon.setBounds(titleButton.getX() - 50, titleButton.getY(), 50, 80);
        this.add(leftRibbon);

        JLabel rightRibbon = new JLabel("🎀");
        rightRibbon.setFont(new Font("Serif", Font.PLAIN, 36));
        rightRibbon.setBounds(titleButton.getX() + titleButton.getWidth(), titleButton.getY(), 50, 80);
        this.add(rightRibbon);

        // Start Game button styled as rounded oblong, placed lower (under tree)
        RoundedButton startButton = new RoundedButton("Start Game");
        startButton.setBounds(GamePanel.SCREEN_WIDTH/2 - 150, 580, 300, 60); // centered lower
        startButton.setFont(new Font("Serif", Font.BOLD, 28));
        this.add(startButton);

        // How to Play button
        RoundedButton howToPlayButton = new RoundedButton("How to Play");
        howToPlayButton.setBounds(GamePanel.SCREEN_WIDTH/2 - 150, 650, 300, 60);
        howToPlayButton.setFont(new Font("Serif", Font.BOLD, 28));
        this.add(howToPlayButton);

       startButton.addActionListener(e -> {
    // Stop any running timers from the old GamePanel
    if (frame.getContentPane() instanceof GamePanel gp) {
        if (gp.spawnTimer != null) gp.spawnTimer.stop();
        if (gp.gameTimer != null) gp.gameTimer.stop();
        if (gp.animationTimer != null) gp.animationTimer.stop();
    }

    // Start a fresh game
    frame.setContentPane(new GamePanel());
    frame.revalidate();
});

        howToPlayButton.addActionListener(e -> {
            UIManager.put("OptionPane.background", new Color(255, 228, 240));
            UIManager.put("Panel.background", new Color(255, 228, 240));
            UIManager.put("OptionPane.messageForeground", new Color(120, 40, 80));
            UIManager.put("Button.background", new Color(255, 182, 193));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.font", new Font("Serif", Font.BOLD, 16));
            UIManager.put("OptionPane.font", new Font("Serif", Font.BOLD, 18));

            JOptionPane.showMessageDialog(
                this,
                "How to Play:\n\n" +
                "- Click on the cute pookies to pop them and score points!\n" +
                "- Avoid clicking on the bombs, or you'll lose a life.\n" +
                "- You have 3 lives and 60 seconds to get the highest score.\n" +
                "- The game gets faster as time goes on.\n\n" +
                "Good luck! 🎀",
                "Peek-A-Pookie Instructions ✨",
                JOptionPane.INFORMATION_MESSAGE
            );
        });

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT, this);
        }
    }
}
