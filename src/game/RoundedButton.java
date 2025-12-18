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

        // White centered multi-line text
        g2.setColor(Color.WHITE);
        Font baseFont = getFont();
        Font boldFont = baseFont.deriveFont(Font.BOLD, baseFont.getSize() + 4);
        String[] lines = getText().split("\n");
        int lineHeight = baseFont.getSize() + 8; // approximate line height
        int totalHeight = lines.length * lineHeight;
        int startY = (getHeight() - totalHeight) / 2 + baseFont.getSize();

        for (int i = 0; i < lines.length; i++) {
            Font font = (i == 0) ? boldFont : baseFont;
            g2.setFont(font);
            FontMetrics fm = g2.getFontMetrics(font);
            int textX = (getWidth() - fm.stringWidth(lines[i])) / 2;
            int textY = startY + i * lineHeight;
            g2.drawString(lines[i], textX, textY);
        }

        // Darker outline
        g2.setStroke(new BasicStroke(2));
        g2.setColor(new Color(120, 40, 80));
        g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 40, 40);

        g2.dispose();
    }
}
