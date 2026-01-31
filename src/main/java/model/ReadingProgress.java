package model;

/**
 * Represents reading progress for a manga chapter.
 * Stores the last read page for each manga/chapter combination.
 */
public record ReadingProgress(
        String mangaId,
        String chapterId,
        int pageIndex,
        long lastReadAt
) {
    /**
     * Convenience constructor with default pageIndex=0 and current timestamp.
     */
    public ReadingProgress(String mangaId, String chapterId) {
        this(mangaId, chapterId, 0, System.currentTimeMillis());
    }

    /**
     * Constructor with custom pageIndex but default timestamp.
     */
    public ReadingProgress(String mangaId, String chapterId, int pageIndex) {
        this(mangaId, chapterId, pageIndex, System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "ReadingProgress{" +
                "mangaId='" + mangaId + '\'' +
                ", chapterId='" + chapterId + '\'' +
                ", pageIndex=" + pageIndex +
                ", lastReadAt=" + lastReadAt +
                '}';
    }
}

