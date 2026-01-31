package model;

/**
 * Represents a recently opened manga.
 * Stores manga ID, title, and last read timestamp.
 */
public record RecentManga(
        String mangaId,
        String title,
        long lastReadAt
) {
    /** Maximum number of recent mangas to store. */
    public static final int MAX_COUNT = 10;

    /**
     * Convenience constructor with default timestamp.
     */
    public RecentManga(String mangaId, String title) {
        this(mangaId, title, System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "RecentManga{" +
                "mangaId='" + mangaId + '\'' +
                ", title='" + title + '\'' +
                ", lastReadAt=" + lastReadAt +
                '}';
    }
}

