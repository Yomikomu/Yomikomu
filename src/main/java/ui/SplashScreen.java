package ui;

import javax.swing.*;
import java.awt.*;

public class SplashScreen {
    private final JWindow window;
    private final JLabel imageLabel;
    private final JLabel versionLabel;
    
    private static final String VERSION = "v0.5a";
    private static final int SPLASH_WIDTH = 400;
    private static final int SPLASH_HEIGHT = 300;
    
    public SplashScreen() {
        window = new JWindow();
        window.setLayout(new BorderLayout());
        
        // Create main panel with dark background
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(40, 40, 50));
        mainPanel.setLayout(new BorderLayout());
        
        // Load and display logo
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        // Try to load the logo image with error handling
        ImageIcon logoIcon = loadLogoImage();
        if (logoIcon != null) {
            imageLabel.setIcon(logoIcon);
            // Scale image to fit nicely
            Image scaledImage = logoIcon.getImage().getScaledInstance(
                SPLASH_WIDTH - 40, SPLASH_HEIGHT - 100, Image.SCALE_SMOOTH
            );
            imageLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            // Fallback to text if image fails to load
            imageLabel.setText("<html><center><h1>Shiori</h1><p>Manga Reader</p></center></html>");
            imageLabel.setFont(new Font("Serif", Font.BOLD, 24));
            imageLabel.setForeground(Color.WHITE);
        }
        
        mainPanel.add(imageLabel, BorderLayout.CENTER);
        
        // Add version label at bottom
        versionLabel = new JLabel("Shiori " + VERSION, SwingConstants.CENTER);
        versionLabel.setForeground(Color.WHITE);
        versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        versionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(versionLabel, BorderLayout.SOUTH);
        
        window.add(mainPanel);
        
        // Set size and position
        window.setSize(SPLASH_WIDTH, SPLASH_HEIGHT);
        centerOnScreen();
    }
    
    private ImageIcon loadLogoImage() {
        try {
            // Try multiple paths for the logo
            String[] paths = {
                "/logo-trans.png",
                "/resources/logo-trans.png",
                "logo-trans.png"
            };
            
            for (String path : paths) {
                java.net.URL imgURL = getClass().getResource(path);
                if (imgURL != null) {
                    ImageIcon icon = new ImageIcon(imgURL);
                    if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                        System.out.println("Successfully loaded logo from: " + path);
                        return icon;
                    }
                }
            }
            
            // If resource loading fails, try to load from file system
            java.io.File logoFile = new java.io.File("src/main/resources/logo-trans.png");
            if (logoFile.exists()) {
                ImageIcon icon = new ImageIcon(logoFile.getAbsolutePath());
                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    System.out.println("Successfully loaded logo from filesystem");
                    return icon;
                }
            }
            
            System.err.println("Warning: Could not load logo image, using fallback");
            return null;
            
        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
            return null;
        }
    }
    
    private void centerOnScreen() {
        // Get screen dimensions
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();
        
        if (screens.length > 0) {
            // Center on primary screen
            GraphicsDevice primaryScreen = screens[0];
            GraphicsConfiguration gc = primaryScreen.getDefaultConfiguration();
            Rectangle screenBounds = gc.getBounds();
            
            int x = screenBounds.x + (screenBounds.width - window.getWidth()) / 2;
            int y = screenBounds.y + (screenBounds.height - window.getHeight()) / 2;
            
            window.setLocation(x, y);
        } else {
            // Fallback to old method
            window.setLocationRelativeTo(null);
        }
    }
    
    public void show() {
        window.setVisible(true);
        // Force immediate repaint
        window.paintAll(window.getGraphics());
    }
    
    public void hide() {
        window.setVisible(false);
        window.dispose();
    }
    
    public void setStatus(String status) {
        versionLabel.setText("Shiori " + VERSION + " - " + status);
        versionLabel.repaint();
    }
    
    // Method to show splash for a minimum duration (useful during initialization)
    public void showForDuration(int milliseconds) {
        show();
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
