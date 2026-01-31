package recent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import model.RecentManga;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Stores recently opened manga for quick access.
 * Mangas are ordered by last read timestamp, most recent first.
 * Maximum of 10 entries are stored.
 */
public class RecentMangasStore {
    private static final Logger logger = LogManager.getLogger(RecentMangasStore.class);

    private final Path file;
    private final ObjectMapper mapper;
    private final List<RecentManga> recentMangas = new ArrayList<>();

    public RecentMangasStore(Path file) {
        this.file = file;
        this.mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        load();
    }

    /**
     * Add a manga to the recent list.
     * If manga already exists, it's moved to the top with updated timestamp.
     * If list exceeds MAX_COUNT, oldest entries are removed.
     */
    public void add(String mangaId, String title) {
        logger.debug("Adding recent manga: {} ({})", title, mangaId);

        // Remove existing entry if present
        recentMangas.removeIf(m -> Objects.equals(m.mangaId(), mangaId));

        // Add new entry at the beginning (most recent)
        recentMangas.add(0, new RecentManga(mangaId, title, System.currentTimeMillis()));

        // Trim to max count
        while (recentMangas.size() > RecentManga.MAX_COUNT) {
            recentMangas.remove(recentMangas.size() - 1);
        }

        save();
    }

    /**
     * Get all recent mangas, ordered by last read time (most recent first).
     *
     * @return an unmodifiable list of recent mangas
     */
    public List<RecentManga> getAll() {
        return List.copyOf(recentMangas);
    }

    /**
     * Update the title of a manga in the recent list.
     */
    public void updateTitle(String mangaId, String newTitle) {
        for (int i = 0; i < recentMangas.size(); i++) {
            if (Objects.equals(recentMangas.get(i).mangaId(), mangaId)) {
                RecentManga old = recentMangas.get(i);
                recentMangas.set(i, new RecentManga(mangaId, newTitle, old.lastReadAt()));
                save();
                return;
            }
        }
    }

    /**
     * Remove a manga from the recent list.
     */
    public void remove(String mangaId) {
        recentMangas.removeIf(m -> Objects.equals(m.mangaId(), mangaId));
        save();
    }

    /**
     * Clear all recent mangas.
     */
    public void clear() {
        recentMangas.clear();
        save();
    }

    /**
     * Check if a manga is in the recent list.
     */
    public boolean contains(String mangaId) {
        return recentMangas.stream()
                .anyMatch(m -> Objects.equals(m.mangaId(), mangaId));
    }

    @SuppressWarnings("unchecked")
    private void load() {
        if (!Files.exists(file)) {
            logger.debug("Recent mangas file {} does not exist, starting with empty list", file);
            return;
        }

        try {
            List<Map<String, Object>> list = mapper.readValue(
                    file.toFile(),
                    List.class
            );

            for (Map<String, Object> item : list) {
                String mangaId = (String) item.get("mangaId");
                String title = (String) item.get("title");

                long lastReadAt;
                if (item.get("lastReadAt") instanceof Long) {
                    lastReadAt = (Long) item.get("lastReadAt");
                } else if (item.get("lastReadAt") instanceof Integer) {
                    lastReadAt = ((Integer) item.get("lastReadAt")).longValue();
                } else {
                    lastReadAt = System.currentTimeMillis();
                }

                recentMangas.add(new RecentManga(mangaId, title, lastReadAt));
            }

            logger.info("Loaded {} recent mangas from {}", recentMangas.size(), file);
        } catch (IOException e) {
            logger.error("Failed to load recent mangas from {}, starting with empty list", file, e);
        }
    }

    private void save() {
        try {
            Files.createDirectories(file.getParent());
            mapper.writeValue(file.toFile(), recentMangas);
            logger.debug("Saved {} recent mangas to {}", recentMangas.size(), file);
        } catch (IOException e) {
            logger.error("Failed to save recent mangas to {}", file, e);
        }
    }
}

