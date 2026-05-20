package com.misw.app.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.misw.app.model.Album
import com.misw.app.model.Track
import com.misw.app.repository.album.AlbumRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AlbumDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockApplication: Application

    @Mock
    private lateinit var mockRepository: AlbumRepository

    private lateinit var viewModel: AlbumDetailViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun testLoadAlbumSuccess() {
        runTest {
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

            whenever(mockRepository.getAlbumById(any())).thenReturn(testAlbum)

            viewModel = AlbumDetailViewModel(mockApplication)
            viewModel.loadAlbum(1)
            advanceUntilIdle()

            assert(viewModel.album.value?.id == 1)
            assert(viewModel.album.value?.name == "Test Album")
        }
    }

    @Test
    fun testRefreshAlbumAfterAddingTrack() {
        runTest {
            val initialAlbum = Album(
                id = 1,
                name = "Test Album",
                cover = "http://example.com/cover.jpg",
                releaseDate = "2020-01-01T00:00:00.000Z",
                description = "Test Description",
                genre = "Rock",
                recordLabel = "EMI",
                tracks = emptyList()
            )

            val updatedAlbum = Album(
                id = 1,
                name = "Test Album",
                cover = "http://example.com/cover.jpg",
                releaseDate = "2020-01-01T00:00:00.000Z",
                description = "Test Description",
                genre = "Rock",
                recordLabel = "EMI",
                tracks = listOf(
                    Track(
                        id = 1,
                        name = "New Track",
                        duration = "03:45",
                        album = initialAlbum
                    )
                )
            )

            whenever(mockRepository.getAlbumById(any())).thenReturn(initialAlbum)

            viewModel = AlbumDetailViewModel(mockApplication)
            viewModel.loadAlbum(1)
            advanceUntilIdle()

            // Verify initial load
            assert(viewModel.album.value?.tracks?.isEmpty() == true)

            // Now update the mock to return album with track
            whenever(mockRepository.getAlbumById(any())).thenReturn(updatedAlbum)

            // Call refresh
            viewModel.refreshAlbum()
            advanceUntilIdle()

            // Verify album was refreshed with new track
            assert(viewModel.album.value?.tracks?.size == 1)
            assert(viewModel.album.value?.tracks?.get(0)?.name == "New Track")
        }
    }

    @Test
    fun testLoadingStateOnLoad() {
        runTest {
            val testAlbum = Album(
                id = 1,
                name = "Album",
                cover = "http://example.com/cover.jpg",
                releaseDate = "2020-01-01T00:00:00.000Z",
                description = "Desc",
                genre = "Genre",
                recordLabel = "Label",
                tracks = emptyList()
            )

            whenever(mockRepository.getAlbumById(any())).thenReturn(testAlbum)

            viewModel = AlbumDetailViewModel(mockApplication)
            viewModel.loadAlbum(1)
            advanceUntilIdle()

            // After load, loading should be false
            assert(viewModel.isLoading.value == false)
        }
    }

    @Test
    fun testErrorHandlingOnLoadFailure() {
        runTest {
            val errorMessage = "Failed to load album"
            whenever(mockRepository.getAlbumById(any())).thenThrow(Exception(errorMessage))

            viewModel = AlbumDetailViewModel(mockApplication)
            viewModel.loadAlbum(1)
            advanceUntilIdle()

            // Verify error was set
            assert(viewModel.error.value != null)
            assert(viewModel.error.value?.contains("Failed to load album") == true)
        }
    }

    @Test
    fun testRefreshWithoutPriorLoad() {
        runTest {
            viewModel = AlbumDetailViewModel(mockApplication)

            val testAlbum = Album(
                id = 1,
                name = "Album",
                cover = "http://example.com/cover.jpg",
                releaseDate = "2020-01-01T00:00:00.000Z",
                description = "Desc",
                genre = "Genre",
                recordLabel = "Label",
                tracks = emptyList()
            )

            whenever(mockRepository.getAlbumById(any())).thenReturn(testAlbum)

            // Call refresh without prior load
            viewModel.refreshAlbum()
            advanceUntilIdle()

            // Should not crash and repository should not be called
            // (because currentAlbumId is null)
            verify(mockRepository, org.mockito.kotlin.never()).getAlbumById(any())
        }
    }

    @Test
    fun testLoadAlbumWithTracks() {
        runTest {
            val album = Album(
                id = 1,
                name = "Album",
                cover = "http://example.com/cover.jpg",
                releaseDate = "2020-01-01T00:00:00.000Z",
                description = "Desc",
                genre = "Genre",
                recordLabel = "Label",
                tracks = listOf(
                    Track(
                        id = 1,
                        name = "Track 1",
                        duration = "03:00",
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
                    ),
                    Track(
                        id = 2,
                        name = "Track 2",
                        duration = "04:00",
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
                )
            )

            whenever(mockRepository.getAlbumById(any())).thenReturn(album)

            viewModel = AlbumDetailViewModel(mockApplication)
            viewModel.loadAlbum(1)
            advanceUntilIdle()

            // Verify tracks were loaded
            assert(viewModel.album.value?.tracks?.size == 2)
            assert(viewModel.album.value?.tracks?.get(0)?.name == "Track 1")
            assert(viewModel.album.value?.tracks?.get(1)?.name == "Track 2")
        }
    }

    @Test
    fun testMultipleReloadsOfSameAlbum() {
        runTest {
            val album = Album(
                id = 1,
                name = "Album",
                cover = "http://example.com/cover.jpg",
                releaseDate = "2020-01-01T00:00:00.000Z",
                description = "Desc",
                genre = "Genre",
                recordLabel = "Label",
                tracks = emptyList()
            )

            whenever(mockRepository.getAlbumById(any())).thenReturn(album)

            viewModel = AlbumDetailViewModel(mockApplication)

            // Load same album multiple times
            viewModel.loadAlbum(1)
            advanceUntilIdle()

            viewModel.refreshAlbum()
            advanceUntilIdle()

            viewModel.refreshAlbum()
            advanceUntilIdle()

            // Should have called repository 3 times
            verify(mockRepository, org.mockito.kotlin.times(3)).getAlbumById(1)
        }
    }
}
