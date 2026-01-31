package bookmark;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import model.Bookmark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Stores bookmarks for manga chapters.
 * Bookmarks are keyed by mangaId, with only one bookmark per manga.
 */
public class BookmarkStore {
    private final Path file;
    private final ObjectMapper mapper;
    private final List<Bookmark> bookmarks = new ArrayList<>();

    public BookmarkStore(Path file) {
        this.file = file;
        this.mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        load();
    }

    /**
     * Add or update a bookmark for a manga.
     * If a bookmark for this manga already exists, it will be replaced.
     */
    public void add(Bookmark bookmark) {
        bookmarks.removeIf(b -> Objects.equals(b.mangaId(), bookmark.mangaId()));
        bookmarks.add(bookmark);
        save();
    }

/**
     * Remove a bookmark by manga ID.
     */
    public void remove(String mangaId) {
        bookmarks.removeIf(b -> Objects.equals(b.mangaId(), mangaId));
        save();
    }

    /**
     * Find a bookmark by manga ID.
     *
     * @param mangaId the manga ID to search for
     * @return the bookmark if found, null otherwise
     */
    public Bookmark find(String mangaId) {
        return bookmarks.stream()
                .filter(b -> Objects.equals(b.mangaId(), mangaId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get all bookmarks.
     *
     * @return an unmodifiable list of all bookmarks
     */
    public List<Bookmark> all() {
        return List.copyOf(bookmarks);
    }

    private void load() {
        if (!Files.exists(file)) {
            return;
        }

        try {
            Bookmark[] loaded = mapper.readValue(file.toFile(), Bookmark[].class);
            bookmarks.clear();
            bookmarks.addAll(List.of(loaded));
        } catch (IOException e) {
            System.err.println("Failed to load bookmarks from " + file + ": " + e.getMessage());
        }
    }

    private void save() {
        try {
            Files.createDirectories(file.getParent());
            mapper.writeValue(file.toFile(), bookmarks);
        } catch (IOException e) {
            System.err.println("Failed to save bookmarks to " + file + ": " + e.getMessage());
        }
    }
}

