import javax.swing.*;
import java.awt.*;

public class MangaListPanel extends JPanel {
    public MangaListPanel() {
        setLayout(new BorderLayout());
        JTextField searchField = new JTextField();
        add(searchField, BorderLayout.NORTH);
        DefaultListModel<String> model = new DefaultListModel<>();
        model.addElement("Bocchi the Rock!");
        model.addElement("Lucky Star");
        JList<String> list = new JList<>(model);
        add(new JScrollPane(list), BorderLayout.CENTER);
    }
}