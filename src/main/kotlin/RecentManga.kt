package recent

/**
 * Represents a recently opened manga.
 * Stores manga ID, title, and last read timestamp.
 */
data class RecentManga(
    val mangaId: String,
    val title: String,
    val lastReadAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val MAX_COUNT = 10
    }
}

