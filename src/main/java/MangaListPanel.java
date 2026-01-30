import api.MangaDexClient;
import model.Manga;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class MangaListPanel extends JPanel {

    private final DefaultListModel<Manga> model = new DefaultListModel<>();
    private final JList<Manga> list = new JList<>(model);
    private final MangaDexClient api = new MangaDexClient();
    private final Consumer<Manga> onSelect;

    public MangaListPanel(Consumer<Manga> onSelect) {
        this.onSelect = onSelect;

        setLayout(new BorderLayout());

        JTextField searchField = new JTextField();
        add(searchField, BorderLayout.NORTH);

        add(new JScrollPane(list), BorderLayout.CENTER);

        searchField.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (query.isEmpty()) return;

            model.clear();

            new SwingWorker<List<Manga>, Void>() {
                @Override
                protected List<Manga> doInBackground() throws Exception {
                    return api.searchManga(query);
                }

                @Override
                protected void done() {
                    try {
                        for (Manga m : get()) model.addElement(m);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }.execute();
        });

        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && list.getSelectedValue() != null) {
                onSelect.accept(list.getSelectedValue());
            }
        });
    }

    public MangaListPanel() {
        this(manga -> {});
    }
}