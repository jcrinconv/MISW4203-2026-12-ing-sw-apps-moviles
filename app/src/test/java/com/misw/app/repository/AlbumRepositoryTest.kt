package com.misw.app.repository.album

import com.misw.app.model.Album
import com.misw.app.model.Track
import com.misw.app.model.TrackRequest
import com.misw.app.network.CacheManager
import com.misw.app.network.album.AlbumRemoteDataSource
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class AlbumRepositoryTest {

    private lateinit var fakeRemoteDataSource: FakeAlbumRemoteDataSource
    private lateinit var repository: AlbumRepository

    @Before
    fun setUp() {
        fakeRemoteDataSource = FakeAlbumRemoteDataSource()
        repository = AlbumRepositoryImpl(fakeRemoteDataSource)
        // Reset singleton cache between tests
        CacheManager.getInstance().clearCache()
    }

    @Test
    fun testAddTrackClearsAlbumDetailCache() { runBlocking {
        val trackRequest = TrackRequest(name = "Test Track", duration = "03:45")
        repository.addTrack(1, trackRequest)
        assert(fakeRemoteDataSource.addTrackCalled)
    } }

    @Test
    fun testAddTrackReturnsTrack() { runBlocking {
        val trackRequest = TrackRequest(name = "Test Track", duration = "03:45")
        val result = repository.addTrack(1, trackRequest)
        assert(result.name == "Test Track")
        assert(result.duration == "03:45")
    } }

    @Test
    fun testAddTrackWithFormattedDuration() { runBlocking {
        val trackRequest = TrackRequest(name = "Song", duration = "05:30")
        repository.addTrack(1, trackRequest)
        assert(fakeRemoteDataSource.lastTrackRequest?.duration == "05:30")
    } }

    @Test
    fun testAddTrackHandlesException() { runBlocking {
        fakeRemoteDataSource.shouldThrow = true
        val trackRequest = TrackRequest(name = "Test Track", duration = "03:45")
        try {
            repository.addTrack(1, trackRequest)
            assert(false)
        } catch (e: Exception) {
            assert(e.message == "Network error")
        }
    } }
}

private class FakeAlbumRemoteDataSource : AlbumRemoteDataSource() {
    var addTrackCalled = false
    var lastTrackRequest: TrackRequest? = null
    var shouldThrow = false

    private val stubAlbum = Album(
        id = 1, name = "Test Album", cover = "", releaseDate = "2020-01-01T00:00:00.000Z",
        description = "", genre = "Rock", recordLabel = "EMI", tracks = emptyList()
    )

    override suspend fun addTrack(albumId: Int, track: TrackRequest): Track {
        if (shouldThrow) throw Exception("Network error")
        addTrackCalled = true
        lastTrackRequest = track
        return Track(id = 1, name = track.name, duration = track.duration, album = stubAlbum)
    }
}
