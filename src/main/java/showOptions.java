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
        JCheckBox c1 = new JCheckBox("Tick this box?"); // TODO: MAKE THIS DO SOMETHING
        frame.add(c1);
        frame.pack();
        frame.setLocationRelativeTo(null); // this centers the window on spawn
        frame.setVisible(true);
    } 


}
