import javax.swing.*;
import java.awt.*;
/**
 * i know this file has a stupid name.
 * */
public class showOptions {
    public static void showOptions() {
        initOptionsUI();

    }


    public static void initOptionsUI() {
        JFrame frame = new JFrame("Options");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        JCheckBox c1 = new JCheckBox("Enable Caching", true);
        c1.addActionListener(e -> {
            if(c1.isSelected()) {
                boolean iWantCaching = true;
            } else {
                boolean iWantCaching = false;
            }
        });
        frame.add(c1);
        frame.pack();
        frame.setLocationRelativeTo(null); // this centers the window on spawn
        frame.setVisible(true);
    }


}
