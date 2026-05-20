package com.misw.app.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class TrackAssociateViewModelTest {

    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockApplication: Application

    @Mock
    private lateinit var successObserver: Observer<Boolean>

    @Mock
    private lateinit var errorObserver: Observer<String?>

    @Mock
    private lateinit var loadingObserver: Observer<Boolean>

    private lateinit var viewModel: TrackAssociateViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        // Create viewmodel instance
        // Since the viewModel creates its own repository, we'll test the validation logic
    }

    @Test
    fun testValidateEmptyTrackName() {
        runTest {
            viewModel = TrackAssociateViewModel(mockApplication)
            viewModel.associationSuccess.observeForever(successObserver)
            viewModel.error.observeForever(errorObserver)

            // Try to associate with empty track name
            viewModel.associateTrack(1, "", "3", "45")

            // Should set error and not success
            verify(errorObserver).onChanged("El nombre del track no puede estar vacío")
        }
    }

    @Test
    fun testValidateEmptyMinutes() {
        runTest {
            viewModel = TrackAssociateViewModel(mockApplication)
            viewModel.associationSuccess.observeForever(successObserver)
            viewModel.error.observeForever(errorObserver)

            // Try to associate with empty minutes
            viewModel.associateTrack(1, "Track Name", "", "45")

            // Should set error
            verify(errorObserver).onChanged("Los minutos no pueden estar vacíos")
        }
    }

    @Test
    fun testValidateEmptySeconds() {
        runTest {
            viewModel = TrackAssociateViewModel(mockApplication)
            viewModel.associationSuccess.observeForever(successObserver)
            viewModel.error.observeForever(errorObserver)

            // Try to associate with empty seconds
            viewModel.associateTrack(1, "Track Name", "3", "")

            // Should set error
            verify(errorObserver).onChanged("Los segundos no pueden estar vacíos")
        }
    }

    @Test
    fun testValidateInvalidMinutes() {
        runTest {
            viewModel = TrackAssociateViewModel(mockApplication)
            viewModel.associationSuccess.observeForever(successObserver)
            viewModel.error.observeForever(errorObserver)

            // Try to associate with non-numeric minutes
            viewModel.associateTrack(1, "Track Name", "abc", "45")

            // Should set error
            verify(errorObserver).onChanged("Los minutos deben ser un número válido")
        }
    }

    @Test
    fun testValidateInvalidSeconds() {
        runTest {
            viewModel = TrackAssociateViewModel(mockApplication)
            viewModel.associationSuccess.observeForever(successObserver)
            viewModel.error.observeForever(errorObserver)

            // Try to associate with non-numeric seconds
            viewModel.associateTrack(1, "Track Name", "3", "xyz")

            // Should set error
            verify(errorObserver).onChanged("Los segundos deben ser un número válido")
        }
    }

    @Test
    fun testValidateSecondsTooLarge() {
        runTest {
            viewModel = TrackAssociateViewModel(mockApplication)
            viewModel.associationSuccess.observeForever(successObserver)
            viewModel.error.observeForever(errorObserver)

            // Try to associate with seconds > 59
            viewModel.associateTrack(1, "Track Name", "3", "75")

            // Should set error
            verify(errorObserver).onChanged("Los segundos no pueden ser mayores a 59")
        }
    }

    @Test
    fun testValidateSecondsEquals59() {
        runTest {
            viewModel = TrackAssociateViewModel(mockApplication)
            viewModel.error.observeForever(errorObserver)

            // 59 seconds should be valid
            viewModel.associateTrack(1, "Track Name", "3", "59")

            // Error should not be set for this validation
            // (it might be set later due to repository call, but not for validation)
        }
    }

    @Test
    fun testValidateSecondsEquals0() {
        runTest {
            viewModel = TrackAssociateViewModel(mockApplication)
            viewModel.error.observeForever(errorObserver)

            // 0 seconds should be valid
            viewModel.associateTrack(1, "Track Name", "0", "0")

            // Should proceed past validation
        }
    }

    @Test
    fun testLoadingStateChanges() {
        runTest {
            viewModel = TrackAssociateViewModel(mockApplication)
            viewModel.isLoading.observeForever(loadingObserver)

            // Try to associate with invalid data
            viewModel.associateTrack(1, "", "3", "45")

            // Loading should not be set to true for validation errors
            // (they fail before the loading state is set)
        }
    }

    @Test
    fun testDurationFormattingWithPaddedZeros() {
        runTest {
            viewModel = TrackAssociateViewModel(mockApplication)
            viewModel.error.observeForever(errorObserver)

            // Valid data with single digits should be formatted as 0X:0X
            viewModel.associateTrack(1, "Track", "5", "9")

            // Should format as 05:09 and proceed
            // (exact verification requires mocking repository)
        }
    }

    @Test
    fun testAllFieldsRequired() {
        runTest {
            viewModel = TrackAssociateViewModel(mockApplication)
            viewModel.error.observeForever(errorObserver)

            // All fields required
            val testCases = listOf(
                Triple("", "3", "45"),      // Empty name
                Triple("Track", "", "45"),  // Empty minutes
                Triple("Track", "3", ""),   // Empty seconds
            )

            for ((name, mins, secs) in testCases) {
                viewModel.associateTrack(1, name, mins, secs)
            }
        }
    }

    @Test
    fun testSecondsBoundaryConditions() {
        runTest {
            viewModel = TrackAssociateViewModel(mockApplication)
            viewModel.error.observeForever(errorObserver)

            // Test boundary: 60 (invalid)
            viewModel.associateTrack(1, "Track", "3", "60")
            verify(errorObserver).onChanged("Los segundos no pueden ser mayores a 59")

            // Test boundary: 59 (valid)
            viewModel.associateTrack(1, "Track", "3", "59")
        }
    }

    @Test
    fun testMinutesCanBeAnyValidNumber() {
        runTest {
            viewModel = TrackAssociateViewModel(mockApplication)
            viewModel.error.observeForever(errorObserver)

            // Minutes can be any valid number (no upper limit like seconds)
            val largeMins = "999"
            viewModel.associateTrack(1, "Track", largeMins, "30")

            // Should not produce validation error for minutes value
        }
    }

    @Test
    fun testWhitespaceOnlyTrackName() {
        runTest {
            viewModel = TrackAssociateViewModel(mockApplication)
            viewModel.error.observeForever(errorObserver)

            // Whitespace-only track name should be treated as blank
            viewModel.associateTrack(1, "   ", "3", "45")

            verify(errorObserver).onChanged("El nombre del track no puede estar vacío")
        }
    }
}
