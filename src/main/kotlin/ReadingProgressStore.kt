package reading

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.nio.file.Files
import java.nio.file.Path
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * Stores reading progress for manga chapters.
 * Progress is keyed by mangaId + chapterId combination.
 */
class ReadingProgressStore(
    private val file: Path
) {
    private val logger: Logger = LogManager.getLogger(ReadingProgressStore::class.java)
    private val mapper = ObjectMapper()
        .registerModule(KotlinModule())

    private var progressMap: MutableMap<String, ReadingProgress> = load()

    /**
     * Save reading progress for a specific manga and chapter
     */
    fun saveProgress(mangaId: String, chapterId: String, pageIndex: Int) {
        val key = generateKey(mangaId, chapterId)
        logger.debug("Saving progress for manga {} chapter {}: page {}", mangaId, chapterId, pageIndex)
        progressMap[key] = ReadingProgress(
            mangaId = mangaId,
            chapterId = chapterId,
            pageIndex = pageIndex,
            lastReadAt = System.currentTimeMillis()
        )
        save()
    }

    /**
     * Get saved page index for a manga and chapter
     */
    fun getPageIndex(mangaId: String, chapterId: String): Int {
        val key = generateKey(mangaId, chapterId)
        return progressMap[key]?.pageIndex ?: 0
    }

    /**
     * Check if progress exists for a manga and chapter
     */
    fun hasProgress(mangaId: String, chapterId: String): Boolean {
        val key = generateKey(mangaId, chapterId)
        return progressMap.containsKey(key)
    }

    /**
     * Clear progress for a specific manga (all chapters)
     */
    fun clearMangaProgress(mangaId: String) {
        progressMap.keys.removeIf { it.startsWith(mangaId) }
        save()
    }

    /**
     * Clear all reading progress
     */
    fun clearAll() {
        progressMap.clear()
        save()
    }

    private fun generateKey(mangaId: String, chapterId: String): String {
        return "$mangaId:$chapterId"
    }

    @Suppress("UNCHECKED_CAST")
    private fun load(): MutableMap<String, ReadingProgress> {
        if (!Files.exists(file)) {
            logger.debug("Progress file {} does not exist, starting with empty progress", file)
            return mutableMapOf()
        }

        return try {
            val list: List<ReadingProgress> = mapper.readValue(
                file.toFile(),
                mapper.typeFactory.constructCollectionType(
                    List::class.java,
                    ReadingProgress::class.java
                )
            )
            val progress = list.associateTo(mutableMapOf()) { generateKey(it.mangaId, it.chapterId) to it }
            logger.info("Loaded {} reading progress entries from {}", progress.size, file)
            progress
        } catch (e: Exception) {
            logger.error("Failed to load progress from {}, starting with empty progress", file, e)
            mutableMapOf()
        }
    }

    private fun save() {
        try {
            Files.createDirectories(file.parent)
            mapper.writerWithDefaultPrettyPrinter()
                .writeValue(file.toFile(), progressMap.values.toList())
            logger.debug("Saved {} reading progress entries to {}", progressMap.size, file)
        } catch (e: Exception) {
            logger.error("Failed to save progress to {}", file, e)
        }
    }
}

