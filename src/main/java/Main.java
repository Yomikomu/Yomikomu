import javax.swing.SwingUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting Shiori manga reader application");
        SwingUtilities.invokeLater(() -> {
            try {
                new MainFrame().setVisible(true);
                logger.info("MainFrame initialized and displayed");
            } catch (Exception e) {
                logger.error("Failed to initialize MainFrame", e);
            }
        });
    }
}
