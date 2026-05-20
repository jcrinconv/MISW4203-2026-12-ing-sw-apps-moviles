package com.misw.app.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.misw.app.model.Album
import com.misw.app.model.Track
import com.misw.app.model.TrackRequest
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
class TrackAssociateViewModelIntegrationTest {

    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockApplication: Application

    @Mock
    private lateinit var mockRepository: AlbumRepository

    private lateinit var viewModel: TrackAssociateViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun testSuccessfulTrackAssociation() {
        runTest {
            // Setup mock repository
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

            whenever(mockRepository.addTrack(any(), any())).thenReturn(mockTrack)

            viewModel = TrackAssociateViewModel(mockApplication)

            // Test successful association
            viewModel.associateTrack(1, "Test Track", "3", "45")
            advanceUntilIdle()

            // Verify success was set to true
            assert(viewModel.associationSuccess.value == true)
        }
    }

    @Test
    fun testRepositoryCalledWithCorrectData() {
        runTest {
            val mockTrack = Track(
                id = 1,
                name = "Track Name",
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

            whenever(mockRepository.addTrack(any(), any())).thenReturn(mockTrack)

            viewModel = TrackAssociateViewModel(mockApplication)
            viewModel.associateTrack(1, "Track Name", "5", "30")
            advanceUntilIdle()

            // Verify repository was called with formatted duration
            verify(mockRepository).addTrack(
                1,
                TrackRequest(name = "Track Name", duration = "05:30")
            )
        }
    }

    @Test
    fun testErrorHandlingOnRepositoryException() {
        runTest {
            val errorMessage = "Network error"
            whenever(mockRepository.addTrack(any(), any())).thenThrow(Exception(errorMessage))

            viewModel = TrackAssociateViewModel(mockApplication)
            viewModel.associateTrack(1, "Track Name", "3", "45")
            advanceUntilIdle()

            // Verify error was set
            assert(viewModel.error.value?.contains("Network error") == true)
            assert(viewModel.associationSuccess.value == false)
        }
    }

    @Test
    fun testLoadingStateOnSuccess() {
        runTest {
            val mockTrack = Track(
                id = 1,
                name = "Track",
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
            )

            whenever(mockRepository.addTrack(any(), any())).thenReturn(mockTrack)

            viewModel = TrackAssociateViewModel(mockApplication)

            // Initially loading should be false
            assert(viewModel.isLoading.value != true)

            viewModel.associateTrack(1, "Track", "3", "0")
            advanceUntilIdle()

            // After execution, loading should be false
            assert(viewModel.isLoading.value == false)
        }
    }

    @Test
    fun testErrorClearedOnNewAttempt() {
        runTest {
            // First, trigger an error
            whenever(mockRepository.addTrack(any(), any())).thenThrow(Exception("First error"))

            viewModel = TrackAssociateViewModel(mockApplication)
            viewModel.associateTrack(1, "Track", "3", "45")
            advanceUntilIdle()

            assert(viewModel.error.value != null)

            // Now clear the mock error and try again
            val mockTrack = Track(
                id = 1,
                name = "Track",
                duration = "03:45",
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
            whenever(mockRepository.addTrack(any(), any())).thenReturn(mockTrack)

            viewModel.associateTrack(1, "Track", "3", "45")
            advanceUntilIdle()

            // Error should be cleared
            assert(viewModel.error.value == null)
            assert(viewModel.associationSuccess.value == true)
        }
    }

    @Test
    fun testDurationFormattingPaddingZeros() {
        runTest {
            val mockTrack = Track(
                id = 1,
                name = "Track",
                duration = "05:09",
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

            whenever(mockRepository.addTrack(any(), any())).thenReturn(mockTrack)

            viewModel = TrackAssociateViewModel(mockApplication)
            viewModel.associateTrack(1, "Track", "5", "9")
            advanceUntilIdle()

            // Verify the duration was formatted with padding zeros
            verify(mockRepository).addTrack(
                1,
                TrackRequest(name = "Track", duration = "05:09")
            )
        }
    }
}
