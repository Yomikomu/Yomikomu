package recent

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.nio.file.Files
import java.nio.file.Path
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * Stores recently opened manga for quick access.
 * Mangas are ordered by last read timestamp, most recent first.
 * Maximum of 10 entries are stored.
 */
class RecentMangasStore(
    private val file: Path
) {
    private val logger: Logger = LogManager.getLogger(RecentMangasStore::class.java)
    private val mapper = ObjectMapper()
        .registerModule(KotlinModule())

    private val recentMangas: MutableList<RecentManga> = load()

    /**
     * Add a manga to the recent list.
     * If manga already exists, it's moved to the top with updated timestamp.
     * If list exceeds MAX_COUNT, oldest entries are removed.
     */
    fun add(mangaId: String, title: String) {
        logger.debug("Adding recent manga: {} ({})", title, mangaId)

        // Remove existing entry if present
        recentMangas.removeAll { it.mangaId == mangaId }

        // Add new entry at the beginning (most recent)
        recentMangas.add(0, RecentManga(mangaId, title, System.currentTimeMillis()))

        // Trim to max count
        while (recentMangas.size > RecentManga.MAX_COUNT) {
            recentMangas.removeAt(recentMangas.lastIndex)
        }

        save()
    }

    /**
     * Get all recent mangas, ordered by last read time (most recent first)
     */
    fun getAll(): List<RecentManga> {
        return recentMangas.toList()
    }

    /**
     * Update the title of a manga in the recent list
     */
    fun updateTitle(mangaId: String, newTitle: String) {
        val index = recentMangas.indexOfFirst { it.mangaId == mangaId }
        if (index >= 0) {
            val old = recentMangas[index]
            recentMangas[index] = old.copy(title = newTitle)
            save()
        }
    }

    /**
     * Remove a manga from the recent list
     */
    fun remove(mangaId: String) {
        recentMangas.removeAll { it.mangaId == mangaId }
        save()
    }

    /**
     * Clear all recent mangas
     */
    fun clear() {
        recentMangas.clear()
        save()
    }

    /**
     * Check if a manga is in the recent list
     */
    fun contains(mangaId: String): Boolean {
        return recentMangas.any { it.mangaId == mangaId }
    }

    private fun load(): MutableList<RecentManga> {
        if (!Files.exists(file)) {
            logger.debug("Recent mangas file {} does not exist, starting with empty list", file)
            return mutableListOf()
        }

        return try {
            val list: List<RecentManga> = mapper.readValue(
                file.toFile(),
                mapper.typeFactory.constructCollectionType(
                    List::class.java,
                    RecentManga::class.java
                )
            )
            logger.info("Loaded {} recent mangas from {}", list.size, file)
            list.toMutableList()
        } catch (e: Exception) {
            logger.error("Failed to load recent mangas from {}, starting with empty list", file, e)
            mutableListOf()
        }
    }

    private fun save() {
        try {
            Files.createDirectories(file.parent)
            mapper.writerWithDefaultPrettyPrinter()
                .writeValue(file.toFile(), recentMangas)
            logger.debug("Saved {} recent mangas to {}", recentMangas.size, file)
        } catch (e: Exception) {
            logger.error("Failed to save recent mangas to {}", file, e)
        }
    }
}

