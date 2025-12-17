package game;

import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {

    public RoundedButton(String text) {
        super(text);
        setFocusPainted(false);           // removes focus ring
        setContentAreaFilled(false);      // disables default fill
        setBorderPainted(false);          // disables default border
        setOpaque(false);                 // ensures transparency
        setBorder(null);                  // removes any leftover border
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Pastel pink background
        g2.setColor(new Color(255, 182, 193));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

        // White centered text
        FontMetrics fm = g2.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(getText())) / 2;
        int textY = (getHeight() + fm.getAscent()) / 2 - 4;
        g2.setColor(Color.WHITE);
        g2.setFont(getFont());
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }
}
