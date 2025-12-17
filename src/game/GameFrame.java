package game;

import javax.swing.JFrame;

public class GameFrame extends JFrame {

    public GameFrame() {
        // Start with the menu instead of the game
        this.setContentPane(new MenuPanel(this));

        this.setTitle("Peek-A-Pookie");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
