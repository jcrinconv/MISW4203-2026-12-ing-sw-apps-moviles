package com.misw.app.network

import com.misw.app.model.Album
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class CacheManagerTest {

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        // Clear singleton instance before each test
        val field = CacheManager::class.java.getDeclaredField("instance")
        field.isAccessible = true
        field.set(null, null)
    }

    @Test
    fun testAlbumDetailCaching() {
        val cacheManager = CacheManager.getInstance()

        val testAlbum = Album(
            id = 1,
            name = "Test Album",
            cover = "http://example.com/cover.jpg",
            releaseDate = "2020-01-01T00:00:00.000Z",
            description = "Test Description",
            genre = "Rock",
            recordLabel = "EMI",
            tracks = emptyList()
        )

        // Add album detail to cache
        cacheManager.addAlbumDetail(1, testAlbum)

        // Retrieve and verify
        val cachedAlbum = cacheManager.getAlbumDetail(1)
        assert(cachedAlbum != null)
        assert(cachedAlbum!!.id == 1)
        assert(cachedAlbum.name == "Test Album")
    }

    @Test
    fun testClearAlbumDetail() {
        val cacheManager = CacheManager.getInstance()

        val testAlbum = Album(
            id = 1,
            name = "Test Album",
            cover = "http://example.com/cover.jpg",
            releaseDate = "2020-01-01T00:00:00.000Z",
            description = "Test Description",
            genre = "Rock",
            recordLabel = "EMI",
            tracks = emptyList()
        )

        // Add album detail
        cacheManager.addAlbumDetail(1, testAlbum)
        assert(cacheManager.getAlbumDetail(1) != null)

        // Clear the album detail cache
        cacheManager.clearAlbumDetail(1)

        // Verify it's removed
        assert(cacheManager.getAlbumDetail(1) == null)
    }

    @Test
    fun testMultipleAlbumDetailsCached() {
        val cacheManager = CacheManager.getInstance()

        val album1 = Album(
            id = 1,
            name = "Album 1",
            cover = "http://example.com/cover1.jpg",
            releaseDate = "2020-01-01T00:00:00.000Z",
            description = "Desc 1",
            genre = "Rock",
            recordLabel = "EMI",
            tracks = emptyList()
        )

        val album2 = Album(
            id = 2,
            name = "Album 2",
            cover = "http://example.com/cover2.jpg",
            releaseDate = "2020-02-01T00:00:00.000Z",
            description = "Desc 2",
            genre = "Pop",
            recordLabel = "Sony",
            tracks = emptyList()
        )

        // Add both albums
        cacheManager.addAlbumDetail(1, album1)
        cacheManager.addAlbumDetail(2, album2)

        // Verify both are cached
        assert(cacheManager.getAlbumDetail(1)?.name == "Album 1")
        assert(cacheManager.getAlbumDetail(2)?.name == "Album 2")

        // Clear only one
        cacheManager.clearAlbumDetail(1)

        // Verify only first is cleared
        assert(cacheManager.getAlbumDetail(1) == null)
        assert(cacheManager.getAlbumDetail(2) != null)
    }

    @Test
    fun testClearNonExistentAlbumDetail() {
        val cacheManager = CacheManager.getInstance()

        // Clear a detail that was never added
        cacheManager.clearAlbumDetail(999)

        // Should not throw exception
        assert(cacheManager.getAlbumDetail(999) == null)
    }

    @Test
    fun testClearCacheClears() {
        val cacheManager = CacheManager.getInstance()

        val testAlbum = Album(
            id = 1,
            name = "Test Album",
            cover = "http://example.com/cover.jpg",
            releaseDate = "2020-01-01T00:00:00.000Z",
            description = "Test Description",
            genre = "Rock",
            recordLabel = "EMI",
            tracks = emptyList()
        )

        // Add album detail
        cacheManager.addAlbumDetail(1, testAlbum)
        assert(cacheManager.getAlbumDetail(1) != null)

        // Clear entire cache
        cacheManager.clearCache()

        // Verify it's cleared
        assert(cacheManager.getAlbumDetail(1) == null)
    }

    @Test
    fun testAlbumDetailPersistedAcrossMultipleCalls() {
        val cacheManager = CacheManager.getInstance()

        val testAlbum = Album(
            id = 1,
            name = "Test Album",
            cover = "http://example.com/cover.jpg",
            releaseDate = "2020-01-01T00:00:00.000Z",
            description = "Test Description",
            genre = "Rock",
            recordLabel = "EMI",
            tracks = emptyList()
        )

        cacheManager.addAlbumDetail(1, testAlbum)

        // Multiple calls should return the same data
        val first = cacheManager.getAlbumDetail(1)
        val second = cacheManager.getAlbumDetail(1)
        val third = cacheManager.getAlbumDetail(1)

        assert(first == second)
        assert(second == third)
        assert(first?.id == 1)
    }
}
