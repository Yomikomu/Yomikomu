package model;

/**
 * Represents a bookmark for a manga chapter.
 * Stores the last read position for each manga.
 */
public record Bookmark(
        String mangaId,
        String mangaTitle,
        String chapterId,
        String chapterTitle,
        int page,
        long createdAt
) {
    /**
     * Convenience constructor with default page=0 and current timestamp.
     */
    public Bookmark(String mangaId, String mangaTitle, String chapterId, String chapterTitle) {
        this(mangaId, mangaTitle, chapterId, chapterTitle, 0, System.currentTimeMillis());
    }

    /**
     * Constructor with custom page but default timestamp.
     */
    public Bookmark(String mangaId, String mangaTitle, String chapterId, String chapterTitle, int page) {
        this(mangaId, mangaTitle, chapterId, chapterTitle, page, System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "Bookmark{" +
                "mangaId='" + mangaId + '\'' +
                ", mangaTitle='" + mangaTitle + '\'' +
                ", chapterId='" + chapterId + '\'' +
                ", chapterTitle='" + chapterTitle + '\'' +
                ", page=" + page +
                ", createdAt=" + createdAt +
                '}';
    }
}

