import javax.swing.*;
import java.awt.*;

public class showOptions {
    public static void showOptions() {
        initOptionsUI();

    }

    public static void initOptionsUI() {
        JFrame frame = new JFrame("Options");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        JLabel temptext = new JLabel("Nothing to see here!");
        frame.pack();
        frame.setLocationRelativeTo(null); // this centers the window on spawn
        frame.setVisible(true);
    }
}
