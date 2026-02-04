package ui;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class showOptions {

    private static final String KEY_CACHE = "cachingEnabled";
    private static final String NSFW_CACHE = "nsfwEnabled";

    private final Preferences prefs =
            Preferences.userNodeForPackage(showOptions.class);

    private boolean iWantCaching;
    private boolean iAmAGooner;

    public showOptions() {
        // Load persisted values
        iWantCaching = prefs.getBoolean(KEY_CACHE, true);
        iAmAGooner = prefs.getBoolean(NSFW_CACHE, false);
    }

    public void showOptions() {
        initOptionsUI();
    }

    private void initOptionsUI() {
        JFrame frame = new JFrame("Options");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JCheckBox c1 = new JCheckBox("Enable Caching", iWantCaching);
        JCheckBox c2 = new JCheckBox("Show NSFW/Hentai Content", iAmAGooner);


        c1.addActionListener(e -> {
            iWantCaching = c1.isSelected();
            prefs.putBoolean(KEY_CACHE, iWantCaching);
        });

        c2.addActionListener(e -> {
            iAmAGooner = c2.isSelected();
            prefs.putBoolean(NSFW_CACHE, iAmAGooner);
        });

        frame.add(c1);
        frame.add(c2);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public boolean isCachingEnabled() {
        return iWantCaching;
    }

    public boolean isNsfwEnabled() {
        return iAmAGooner;
    }
}
