import api.CacheManager;
import api.MangaDexClient;
import model.Chapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;

public class ReaderPanel extends JPanel {

    private final JPanel pagesPanel;
    private final JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
    private final CacheManager cacheManager = new CacheManager();
    private SwingWorker<Void, ImageIcon> currentWorker;
    private double zoomFactor = 1.0;

    public ReaderPanel() {
        setLayout(new BorderLayout());

        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBackground(Color.DARK_GRAY);
        statusLabel.setOpaque(true);
        add(statusLabel, BorderLayout.NORTH);

        pagesPanel = new JPanel();
        pagesPanel.setLayout(new BoxLayout(pagesPanel, BoxLayout.Y_AXIS));
        pagesPanel.setBackground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(pagesPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);

        scrollPane.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isControlDown()) {
                    if (e.getWheelRotation() < 0) {
                        zoomIn();
                    } else {
                        zoomOut();
                    }
                    e.consume();
                }
            }
        });

        add(scrollPane, BorderLayout.CENTER);
    }

    public void clearPages() {
        if (currentWorker != null) currentWorker.cancel(true);

        pagesPanel.removeAll();
        pagesPanel.revalidate();
        pagesPanel.repaint();
    }

    public void loadChapter(MangaDexClient api, Chapter chapter) {
        clearPages();
        scrollToTop();
        statusLabel.setText("Loading chapter: " + chapter.title() + "...");

        currentWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                List<String> pageUrls = api.getPageUrls(chapter.id());
                int total = pageUrls.size();
                int current = 0;

                for (String url : pageUrls) {
                    if (isCancelled()) break;

                    BufferedImage image;
                    if (cacheManager.isCached(url)) {
                        byte[] data = cacheManager.getFromCache(url);
                        try (InputStream in = new ByteArrayInputStream(data)) {
                            image = ImageIO.read(in);
                        }
                    } else {
                        try (InputStream in = new URL(url).openStream()) {
                            byte[] data = in.readAllBytes();
                            cacheManager.saveToCache(url, data);
                            try (InputStream imageIn = new ByteArrayInputStream(data)) {
                                image = ImageIO.read(imageIn);
                            }
                        }
                    }

                    current++;
                    if (image != null) {
                        publish(new ImageIcon(image));
                    }
                    
                    final String progressText = String.format("Loading pages: %d / %d", current, total);
                    SwingUtilities.invokeLater(() -> statusLabel.setText(progressText));
                }
                return null;
            }

            @Override
            protected void process(List<ImageIcon> icons) {
                for (ImageIcon icon : icons) {
                    JLabel label = new JLabel(scaleIcon(icon));
                    label.putClientProperty("originalIcon", icon);
                    label.setAlignmentX(Component.CENTER_ALIGNMENT);
                    label.setBackground(Color.BLACK);
                    label.setOpaque(true);
                    pagesPanel.add(label);
                }
                pagesPanel.revalidate();
            }

            @Override
            protected void done() {
                if (!isCancelled()) {
                    statusLabel.setText("Chapter Loaded: " + chapter.title());
                }
            }
        };

        currentWorker.execute();
    }


    private ImageIcon scaleIcon(ImageIcon icon) {
        int width = Math.max(1, (int) (icon.getIconWidth() * zoomFactor));
        int height = Math.max(1, (int) (icon.getIconHeight() * zoomFactor));
        Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    public void zoomIn() {
        zoomFactor *= 1.2;
        refreshZoom();
    }

    public void zoomOut() {
        zoomFactor /= 1.2;
        refreshZoom();
    }

    public void resetZoom() {
        zoomFactor = 1.0;
        refreshZoom();
    }

    public void scrollPage(boolean down) {
        JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, pagesPanel);
        if (scrollPane != null) {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            int amount = scrollPane.getViewport().getHeight() - 50;
            if (!down) amount = -amount;
            vertical.setValue(vertical.getValue() + amount);
        }
    }

    public void scrollLine(boolean down) {
        JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, pagesPanel);
        if (scrollPane != null) {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            int amount = vertical.getUnitIncrement() * 3;
            if (!down) amount = -amount;
            vertical.setValue(vertical.getValue() + amount);
        }
    }

    public void scrollToTop() {
        SwingUtilities.invokeLater(() -> {
            JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, pagesPanel);
            if (scrollPane != null) {
                scrollPane.getVerticalScrollBar().setValue(0);
                scrollPane.getHorizontalScrollBar().setValue(0);
            }
        });
    }

    private void refreshZoom() {
        for (Component comp : pagesPanel.getComponents()) {
            if (comp instanceof JLabel label && label.getClientProperty("originalIcon") instanceof ImageIcon originalIcon) {
                label.setIcon(scaleIcon(originalIcon));
            }
        }
        pagesPanel.revalidate();
        pagesPanel.repaint();
    }

    public void clearCache() {
        cacheManager.clearCache();
        JOptionPane.showMessageDialog(this, "Cache cleared successfully.");
    }
}