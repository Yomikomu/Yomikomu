import api.MangaDexClient;
import model.Manga;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final MangaDexClient api = new MangaDexClient();
    private final ReaderPanel reader = new ReaderPanel();
    private final ChapterListPanel chapterList =
            new ChapterListPanel(chapter -> reader.loadChapter(api, chapter));

    public MainFrame() {
        super("Shiori");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        MangaListPanel mangaList = new MangaListPanel(manga -> chapterList.loadChapters(manga.id()));

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Manga", mangaList);
        tabs.add("Chapters", chapterList);

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                tabs,
                reader
        );
        split.setDividerLocation(350);
        add(split);

        setupZoomKeys();

        setupNavigationKeys();

        tabs.setMinimumSize(new Dimension(300, 100));
        reader.setMinimumSize(new Dimension(500, 100));

        setupMenu();
    }

    private void setupMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu helpMenu = new JMenu("Help");
        
        JMenuItem shortcutsItem = new JMenuItem("Keyboard Shortcuts");
        shortcutsItem.addActionListener(e -> showShortcuts());
        helpMenu.add(shortcutsItem);

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);

        menuBar.add(helpMenu);

        JMenu advancedMenu = new JMenu("Advanced");
        JMenuItem clearCacheItem = new JMenuItem("Clear Cache");
        clearCacheItem.addActionListener(e -> reader.clearCache());
        advancedMenu.add(clearCacheItem);
        menuBar.add(advancedMenu);

        setJMenuBar(menuBar);
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "Shiori - A Simple Manga Reader\n" +
                "Powered by MangaDex API\n\n" +
                "Version 1.0.0",
                "About Shiori",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showShortcuts() {
        String shortcuts = """
                N / Right Arrow: Next Chapter
                P / Left Arrow: Previous Chapter
                Space: Page Down
                Shift + Space: Page Up
                Down Arrow: Scroll Down
                Up Arrow: Scroll Up
                + : Zoom In
                - : Zoom Out
                0 : Reset Zoom
                Ctrl + Mouse Wheel: Zoom In/Out
                """;
        JOptionPane.showMessageDialog(this, shortcuts, "Keyboard Shortcuts", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setupNavigationKeys() {
        reader.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('n'), "nextChapter");
        reader.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("RIGHT"), "nextChapter");
        reader.getActionMap().put("nextChapter", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                chapterList.nextChapter();
            }
        });

        reader.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('p'), "previousChapter");
        reader.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("LEFT"), "previousChapter");
        reader.getActionMap().put("previousChapter", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                chapterList.previousChapter();
            }
        });

        reader.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(' '), "scrollDown");
        reader.getActionMap().put("scrollDown", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                reader.scrollPage(true);
            }
        });

        reader.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("shift SPACE"), "scrollUp");
        reader.getActionMap().put("scrollUp", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                reader.scrollPage(false);
            }
        });

        reader.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("DOWN"), "scrollLineDown");
        reader.getActionMap().put("scrollLineDown", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                reader.scrollLine(true);
            }
        });

        reader.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("UP"), "scrollLineUp");
        reader.getActionMap().put("scrollLineUp", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                reader.scrollLine(false);
            }
        });
    }

    private void setupZoomKeys() {
        reader.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('+'), "zoomIn");
        reader.getActionMap().put("zoomIn", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                reader.zoomIn();
            }
        });

        reader.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('-'), "zoomOut");
        reader.getActionMap().put("zoomOut", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                reader.zoomOut();
            }
        });

        reader.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('0'), "resetZoom");
        reader.getActionMap().put("resetZoom", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                reader.resetZoom();
            }
        });
    }
}