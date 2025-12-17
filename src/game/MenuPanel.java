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

       startButton.addActionListener(e -> {
    // Stop any running timers from the old GamePanel
    if (frame.getContentPane() instanceof GamePanel gp) {
        if (gp.spawnTimer != null) gp.spawnTimer.stop();
        if (gp.gameTimer != null) gp.gameTimer.stop();
    }

    // Start a fresh game
    frame.setContentPane(new GamePanel());
    frame.revalidate();
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
