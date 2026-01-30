package api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class CacheManagerTest {

    private CacheManager cacheManager;

    @BeforeEach
    public void setUp() {
        cacheManager = new CacheManager();
        cacheManager.clearCache();
    }

    @Test
    public void testSaveAndGetFromCache() throws IOException {
        String url = "https://example.com/image.jpg";
        byte[] data = "fake image data".getBytes(StandardCharsets.UTF_8);

        assertFalse(cacheManager.isCached(url));
        cacheManager.saveToCache(url, data);
        assertTrue(cacheManager.isCached(url));

        byte[] retrievedData = cacheManager.getFromCache(url);
        assertArrayEquals(data, retrievedData);
    }

    @Test
    public void testClearCache() throws IOException {
        String url = "https://example.com/image.jpg";
        byte[] data = "fake image data".getBytes(StandardCharsets.UTF_8);

        cacheManager.saveToCache(url, data);
        assertTrue(cacheManager.isCached(url));

        cacheManager.clearCache();
        assertFalse(cacheManager.isCached(url));
    }
}
