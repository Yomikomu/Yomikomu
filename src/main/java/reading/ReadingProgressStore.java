package reading;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import model.ReadingProgress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores reading progress for manga chapters.
 * Progress is keyed by mangaId + ":" + chapterId combination.
 */
public class ReadingProgressStore {
    private static final Logger logger = LogManager.getLogger(ReadingProgressStore.class);

    private final Path file;
    private final ObjectMapper mapper;
    private final Map<String, ReadingProgress> progressMap = new HashMap<>();

    public ReadingProgressStore(Path file) {
        this.file = file;
        this.mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        load();
    }

    /**
     * Save reading progress for a specific manga and chapter.
     */
    public void saveProgress(String mangaId, String chapterId, int pageIndex) {
        String key = generateKey(mangaId, chapterId);
        logger.debug("Saving progress for manga {} chapter {}: page {}", mangaId, chapterId, pageIndex);
        progressMap.put(key, new ReadingProgress(mangaId, chapterId, pageIndex, System.currentTimeMillis()));
        save();
    }

    /**
     * Get saved page index for a manga and chapter.
     *
     * @return the page index, or 0 if no progress exists
     */
    public int getPageIndex(String mangaId, String chapterId) {
        String key = generateKey(mangaId, chapterId);
        ReadingProgress progress = progressMap.get(key);
        return progress != null ? progress.pageIndex() : 0;
    }

    /**
     * Check if progress exists for a manga and chapter.
     */
    public boolean hasProgress(String mangaId, String chapterId) {
        String key = generateKey(mangaId, chapterId);
        return progressMap.containsKey(key);
    }

    /**
     * Clear progress for a specific manga (all chapters).
     */
    public void clearMangaProgress(String mangaId) {
        progressMap.keySet().removeIf(key -> key.startsWith(mangaId + ":"));
        save();
    }

    /**
     * Clear all reading progress.
     */
    public void clearAll() {
        progressMap.clear();
        save();
    }

    private String generateKey(String mangaId, String chapterId) {
        return mangaId + ":" + chapterId;
    }

    @SuppressWarnings("unchecked")
    private void load() {
        if (!Files.exists(file)) {
            logger.debug("Progress file {} does not exist, starting with empty progress", file);
            return;
        }

        try {
            List<Map<String, Object>> list = mapper.readValue(
                    file.toFile(),
                    List.class
            );

            for (Map<String, Object> item : list) {
                String mangaId = (String) item.get("mangaId");
                String chapterId = (String) item.get("chapterId");
                int pageIndex;
                if (item.get("pageIndex") instanceof Integer) {
                    pageIndex = (Integer) item.get("pageIndex");
                } else if (item.get("pageIndex") instanceof Long) {
                    pageIndex = ((Long) item.get("pageIndex")).intValue();
                } else {
                    pageIndex = 0;
                }

                long lastReadAt;
                if (item.get("lastReadAt") instanceof Long) {
                    lastReadAt = (Long) item.get("lastReadAt");
                } else if (item.get("lastReadAt") instanceof Integer) {
                    lastReadAt = ((Integer) item.get("lastReadAt")).longValue();
                } else {
                    lastReadAt = System.currentTimeMillis();
                }

                String key = generateKey(mangaId, chapterId);
                progressMap.put(key, new ReadingProgress(mangaId, chapterId, pageIndex, lastReadAt));
            }

            logger.info("Loaded {} reading progress entries from {}", progressMap.size(), file);
        } catch (IOException e) {
            logger.error("Failed to load progress from {}, starting with empty progress", file, e);
        }
    }

    private void save() {
        try {
            Files.createDirectories(file.getParent());
            mapper.writeValue(file.toFile(), progressMap.values());
            logger.debug("Saved {} reading progress entries to {}", progressMap.size(), file);
        } catch (IOException e) {
            logger.error("Failed to save progress to {}", file, e);
        }
    }
}

