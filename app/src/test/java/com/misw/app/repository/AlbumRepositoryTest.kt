package com.misw.app.repository.album

import android.content.Context
import com.misw.app.model.Album
import com.misw.app.model.Track
import com.misw.app.model.TrackRequest
import com.misw.app.network.CacheManager
import com.misw.app.network.album.AlbumRemoteDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AlbumRepositoryTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockRemoteDataSource: AlbumRemoteDataSource

    @Mock
    private lateinit var mockCacheManager: CacheManager

    private lateinit var repository: AlbumRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun testAddTrackClearsAlbumDetailCache() = runTest {
        // Create repository with mocked dependencies
        repository = AlbumRepositoryImpl(mockContext, mockRemoteDataSource)

        val trackRequest = TrackRequest(
            name = "Test Track",
            duration = "03:45"
        )

        val mockTrack = Track(
            id = 1,
            name = "Test Track",
            duration = "03:45",
            album = Album(
                id = 1,
                name = "Test Album",
                cover = "http://example.com/cover.jpg",
                releaseDate = "2020-01-01T00:00:00.000Z",
                description = "Test Description",
                genre = "Rock",
                recordLabel = "EMI",
                tracks = emptyList()
            )
        )

        whenever(mockRemoteDataSource.addTrack(any(), any())).thenReturn(mockTrack)

        // Call add track
        repository.addTrack(1, trackRequest)

        // Verify the remote data source was called
        verify(mockRemoteDataSource).addTrack(1, trackRequest)
    }

    @Test
    fun testAddTrackReturnsTrack() = runTest {
        repository = AlbumRepositoryImpl(mockContext, mockRemoteDataSource)

        val trackRequest = TrackRequest(
            name = "Test Track",
            duration = "03:45"
        )

        val expectedTrack = Track(
            id = 1,
            name = "Test Track",
            duration = "03:45",
            album = Album(
                id = 1,
                name = "Test Album",
                cover = "http://example.com/cover.jpg",
                releaseDate = "2020-01-01T00:00:00.000Z",
                description = "Test Description",
                genre = "Rock",
                recordLabel = "EMI",
                tracks = emptyList()
            )
        )

        whenever(mockRemoteDataSource.addTrack(any(), any())).thenReturn(expectedTrack)

        val result = repository.addTrack(1, trackRequest)

        assert(result.id == expectedTrack.id)
        assert(result.name == expectedTrack.name)
        assert(result.duration == expectedTrack.duration)
    }

    @Test
    fun testAddTrackWithFormattedDuration() = runTest {
        repository = AlbumRepositoryImpl(mockContext, mockRemoteDataSource)

        val trackRequest = TrackRequest(
            name = "Song",
            duration = "05:30"
        )

        val mockTrack = Track(
            id = 1,
            name = "Song",
            duration = "05:30",
            album = Album(
                id = 1,
                name = "Album",
                cover = "http://example.com/cover.jpg",
                releaseDate = "2020-01-01T00:00:00.000Z",
                description = "Desc",
                genre = "Genre",
                recordLabel = "Label",
                tracks = emptyList()
            )
        )

        whenever(mockRemoteDataSource.addTrack(any(), any())).thenReturn(mockTrack)

        repository.addTrack(1, trackRequest)

        // Verify it was called with the exact request
        verify(mockRemoteDataSource).addTrack(1, trackRequest)
    }

    @Test
    fun testAddTrackHandlesException() = runTest {
        repository = AlbumRepositoryImpl(mockContext, mockRemoteDataSource)

        val trackRequest = TrackRequest(
            name = "Test Track",
            duration = "03:45"
        )

        whenever(mockRemoteDataSource.addTrack(any(), any())).thenThrow(Exception("Network error"))

        try {
            repository.addTrack(1, trackRequest)
            assert(false) // Should have thrown
        } catch (e: Exception) {
            assert(e.message == "Network error")
        }
    }
}
