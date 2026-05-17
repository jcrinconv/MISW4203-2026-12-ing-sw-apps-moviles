package com.misw.app

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.misw.app.network.CacheManager
import com.misw.app.network.EspressoIdlingResource
import com.misw.app.network.RetrofitClient
import com.misw.app.ui.MainActivity
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.matcher.ViewMatchers.isRoot

@LargeTest
@RunWith(AndroidJUnit4::class)
class TrackAssociateViewTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    private val mockWebServer = MockWebServer()

    @Before
    fun setup() {
        CacheManager.getInstance(InstrumentationRegistry.getInstrumentation().targetContext).clearCache()

        val dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path ?: return MockResponse().setResponseCode(404)
                return when {
                    path == "/albums" -> MockResponse().setResponseCode(200).setBody(
                        """[{"id":1,"name":"Test Album","cover":"https://picsum.photos/200","releaseDate":"2020-01-01T00:00:00.000Z","description":"Desc","genre":"Rock","recordLabel":"EMI","tracks":[]}]"""
                    )
                    path == "/albums/1" -> MockResponse().setResponseCode(200).setBody(
                        """{"id":1,"name":"Test Album","cover":"https://picsum.photos/200","releaseDate":"2020-01-01T00:00:00.000Z","description":"Desc","genre":"Rock","recordLabel":"EMI","tracks":[]}"""
                    )
                    path.startsWith("/albums/1/tracks") && request.method == "POST" -> MockResponse().setResponseCode(200).setBody(
                        """{"id":1,"name":"New Track","duration":"3:45","album":{"id":1,"name":"Test Album","cover":"https://picsum.photos/200","releaseDate":"2020-01-01T00:00:00.000Z","description":"Desc","genre":"Rock","recordLabel":"EMI"}}"""
                    )
                    else -> MockResponse().setResponseCode(404)
                }
            }
        }
        mockWebServer.dispatcher = dispatcher
        mockWebServer.start(0)
        RetrofitClient.setBaseUrl(mockWebServer.url("/").toString())
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        RetrofitClient.setBaseUrl(BuildConfig.BASE_URL)
    }

    private fun navigateToTrackAssociateScreen() {
        // Navigate to albums
        onView(withId(R.id.include_albums)).perform(click())
        // Click on album to go to detail
        onView(withId(R.id.rvAlbumList)).perform(
            androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition<androidx.recyclerview.widget.RecyclerView.ViewHolder>(
                0, click()
            )
        )
        // Click associate button
        onView(withId(R.id.btnAssociateTracks)).perform(click())
    }

    @Test
    fun testTrackAssociateScreenDisplaysInputFields() {
        navigateToTrackAssociateScreen()
        onView(withId(R.id.etTrackName)).check(matches(isDisplayed()))
        onView(withId(R.id.etMinutos)).check(matches(isDisplayed()))
        onView(withId(R.id.etSegundos)).check(matches(isDisplayed()))
    }

    @Test
    fun testTrackAssociateScreenDisplaysButtons() {
        navigateToTrackAssociateScreen()
        onView(withId(R.id.btnCancel)).check(matches(isDisplayed()))
        onView(withId(R.id.btnAssociate)).check(matches(isDisplayed()))
    }

    @Test
    fun testCancelButtonNavigatesBack() {
        navigateToTrackAssociateScreen()
        onView(withId(R.id.btnCancel)).perform(click())
        onView(withId(R.id.tvTrackCount)).check(matches(isDisplayed()))
    }

    @Test
    fun testCanEnterTrackName() {
        navigateToTrackAssociateScreen()
        onView(withId(R.id.etTrackName)).perform(typeText("My Track"))
        onView(withId(R.id.etTrackName)).check(matches(withText("My Track")))
    }

    @Test
    fun testCanEnterDuration() {
        navigateToTrackAssociateScreen()
        onView(withId(R.id.etMinutos)).perform(typeText("3"))
        onView(withId(R.id.etSegundos)).perform(typeText("45"))
        onView(withId(R.id.etMinutos)).check(matches(withText("3")))
        onView(withId(R.id.etSegundos)).check(matches(withText("45")))
    }

    @Test
    fun testSuccessfulTrackAssociation() {
        navigateToTrackAssociateScreen()

        // Fill in track details
        onView(withId(R.id.etTrackName)).perform(typeText("Test Track"))
        onView(withId(R.id.etMinutos)).perform(typeText("3"))
        onView(withId(R.id.etSegundos)).perform(typeText("45"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Click associate button
        onView(withId(R.id.btnAssociate)).perform(click())

        // Click 'Sí' on modal
        onView(withText("Sí")).perform(click())

        // Should navigate back to album detail
        onView(withId(R.id.tvTrackCount)).check(matches(isDisplayed()))
    }

    @Test
    fun testTrackNameFieldHasCorrectHint() {
        navigateToTrackAssociateScreen()
        onView(withId(R.id.etTrackName)).check(matches(withHint("Nombre de la canción")))
    }

    @Test
    fun testMinutesFieldHasCorrectHint() {
        navigateToTrackAssociateScreen()
        onView(withId(R.id.etMinutos)).check(matches(withHint("mm")))
    }

    @Test
    fun testSecondsFieldHasCorrectHint() {
        navigateToTrackAssociateScreen()
        onView(withId(R.id.etSegundos)).check(matches(withHint("ss")))
    }

    @Test
    fun testAssociateButtonIsEnabledByDefault() {
        navigateToTrackAssociateScreen()
        onView(withId(R.id.btnAssociate)).check(matches(isDisplayed()))
    }

    @Test
    fun testCancelButtonIsEnabledByDefault() {
        navigateToTrackAssociateScreen()
        onView(withId(R.id.btnCancel)).check(matches(isDisplayed()))
    }

    @Test
    fun testCompleteTrackAssociationFlow() {
        navigateToTrackAssociateScreen()

        // Verify we're on the associate track screen
        onView(withText("Asociar canción")).check(matches(isDisplayed()))

        // Fill in all track details
        onView(withId(R.id.etTrackName)).perform(typeText("Bohemian Rhapsody"))
        onView(withId(R.id.etMinutos)).perform(typeText("5"))
        onView(withId(R.id.etSegundos)).perform(typeText("55"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Verify data was entered
        onView(withId(R.id.etTrackName)).check(matches(withText("Bohemian Rhapsody")))
        onView(withId(R.id.etMinutos)).check(matches(withText("5")))
        onView(withId(R.id.etSegundos)).check(matches(withText("55")))

        // Click associate button
        onView(withId(R.id.btnAssociate)).perform(click())

        // Click 'Sí' on modal
        onView(withText("Sí")).perform(click())

        // Should return to album detail view with track count displayed
        onView(withId(R.id.tvTrackCount)).check(matches(isDisplayed()))
    }

    @Test
    fun testEmptyTrackNameValidation() {
        navigateToTrackAssociateScreen()

        // Leave track name empty and fill other fields
        onView(withId(R.id.etMinutos)).perform(typeText("3"))
        onView(withId(R.id.etSegundos)).perform(typeText("45"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Click associate button
        onView(withId(R.id.btnAssociate)).perform(click())

        // Should stay on the same screen (validation error shown in toast)
        onView(withText("Asociar canción")).check(matches(isDisplayed()))
    }

    @Test
    fun testEmptyMinutesValidation() {
        navigateToTrackAssociateScreen()

        // Fill track name but leave minutes empty
        onView(withId(R.id.etTrackName)).perform(typeText("My Track"))
        onView(withId(R.id.etSegundos)).perform(typeText("45"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Click associate button
        onView(withId(R.id.btnAssociate)).perform(click())

        // Should stay on the same screen
        onView(withText("Asociar canción")).check(matches(isDisplayed()))
    }

    @Test
    fun testEmptySecondsValidation() {
        navigateToTrackAssociateScreen()

        // Fill track name and minutes but leave seconds empty
        onView(withId(R.id.etTrackName)).perform(typeText("My Track"))
        onView(withId(R.id.etMinutos)).perform(typeText("3"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Click associate button
        onView(withId(R.id.btnAssociate)).perform(click())

        // Should stay on the same screen
        onView(withText("Asociar canción")).check(matches(isDisplayed()))
    }

    @Test
    fun testInvalidSecondsNumberValidation() {
        navigateToTrackAssociateScreen()

        // Fill valid data except seconds > 59
        onView(withId(R.id.etTrackName)).perform(typeText("My Track"))
        onView(withId(R.id.etMinutos)).perform(typeText("3"))
        onView(withId(R.id.etSegundos)).perform(typeText("75"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Click associate button
        onView(withId(R.id.btnAssociate)).perform(click())

        // Should stay on the same screen
        onView(withText("Asociar canción")).check(matches(isDisplayed()))
    }

    @Test
    fun testInvalidMinutesNumberValidation() {
        navigateToTrackAssociateScreen()

        // Fill valid data except invalid minutes (letters)
        onView(withId(R.id.etTrackName)).perform(typeText("My Track"))
        onView(withId(R.id.etMinutos)).perform(typeText("abc"))
        onView(withId(R.id.etSegundos)).perform(typeText("45"))


        // Click associate button
        onView(withId(R.id.btnAssociate)).perform(click())

        // Should stay on the same screen
        onView(withText("Asociar canción")).check(matches(isDisplayed()))
    }

    @Test
    fun testZeroDurationIsValid() {
        navigateToTrackAssociateScreen()

        // Fill with zero duration (valid)
        onView(withId(R.id.etTrackName)).perform(typeText("Silent Track"))
        onView(withId(R.id.etMinutos)).perform(typeText("0"))
        onView(withId(R.id.etSegundos)).perform(typeText("0"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Click associate button
        onView(withId(R.id.btnAssociate)).perform(click())

        // Click 'Sí' on modal
        onView(withText("Sí")).perform(click())

        // Should return to album detail
        onView(withId(R.id.tvTrackCount)).check(matches(isDisplayed()))
    }

    @Test
    fun testMaxValidSeconds() {
        navigateToTrackAssociateScreen()

        // Fill with max valid seconds (59)
        onView(withId(R.id.etTrackName)).perform(typeText("Track"))
        onView(withId(R.id.etMinutos)).perform(typeText("10"))
        onView(withId(R.id.etSegundos)).perform(typeText("59"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Click associate button
        onView(withId(R.id.btnAssociate)).perform(click())

        // Click 'Sí' on modal
        onView(withText("Sí")).perform(click())

        // Should return to album detail
        onView(withId(R.id.tvTrackCount)).check(matches(isDisplayed()))
    }

    @Test
    fun testFormattingWithPaddedZeros() {
        navigateToTrackAssociateScreen()

        // Fill with single digit numbers
        onView(withId(R.id.etTrackName)).perform(typeText("Track"))
        onView(withId(R.id.etMinutos)).perform(typeText("5"))
        onView(withId(R.id.etSegundos)).perform(typeText("9"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Click associate button
        onView(withId(R.id.btnAssociate)).perform(click())

        // Click 'Sí' on modal
        onView(withText("Sí")).perform(click())

        // Should return to album detail (duration will be formatted as 05:09)
        onView(withId(R.id.tvTrackCount)).check(matches(isDisplayed()))
    }

    @Test
    fun testLongTrackNameIsAccepted() {
        navigateToTrackAssociateScreen()

        val longTrackName = "This Is A Very Long Track Name That Should Still Be Accepted By The System"

        // Fill with long track name
        onView(withId(R.id.etTrackName)).perform(typeText(longTrackName))
        onView(withId(R.id.etMinutos)).perform(typeText("8"))
        onView(withId(R.id.etSegundos)).perform(typeText("30"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Click associate button
        onView(withId(R.id.btnAssociate)).perform(click())

        // Click 'Sí' on modal
        onView(withText("Sí")).perform(click())

        // Should return to album detail
        onView(withId(R.id.tvTrackCount)).check(matches(isDisplayed()))
    }

    @Test
    fun testClearingFieldsAndReenteringData() {
        navigateToTrackAssociateScreen()

        // Enter data
        onView(withId(R.id.etTrackName)).perform(typeText("First Track"))
        onView(withId(R.id.etMinutos)).perform(typeText("3"))
        onView(withId(R.id.etSegundos)).perform(typeText("30"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Clear and re-enter different data
        onView(withId(R.id.etTrackName)).perform(clearText(), typeText("Second Track"))
        onView(withId(R.id.etMinutos)).perform(clearText(), typeText("4"))
        onView(withId(R.id.etSegundos)).perform(clearText(), typeText("45"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Verify new data
        onView(withId(R.id.etTrackName)).check(matches(withText("Second Track")))
        onView(withId(R.id.etMinutos)).check(matches(withText("4")))
        onView(withId(R.id.etSegundos)).check(matches(withText("45")))

        // Click associate
        onView(withId(R.id.btnAssociate)).perform(click())

        // Click 'Sí' on modal
        onView(withText("Sí")).perform(click())

        // Should return to album detail
        onView(withId(R.id.tvTrackCount)).check(matches(isDisplayed()))
    }

    @Test
    fun testTrackNameWithSpecialCharacters() {
        navigateToTrackAssociateScreen()

        // Enter track name with special characters
        onView(withId(R.id.etTrackName)).perform(typeText("Track #1 - Rock's Song"))
        onView(withId(R.id.etMinutos)).perform(typeText("3"))
        onView(withId(R.id.etSegundos)).perform(typeText("45"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Click associate button
        onView(withId(R.id.btnAssociate)).perform(click())

        // Click 'Sí' on modal
        onView(withText("Sí")).perform(click())

        // Should return to album detail
        onView(withId(R.id.tvTrackCount)).check(matches(isDisplayed()))
    }

    @Test
    fun testCancelModalIfNameNotEmpty() {
        navigateToTrackAssociateScreen()

        // Enter track name
        onView(withId(R.id.etTrackName)).perform(typeText("Song"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Click cancel button
        onView(withId(R.id.btnCancel)).perform(click())

        // Should show cancel modal
        onView(withText("¿Desea cancelar la asociación de la canción?")).check(matches(isDisplayed()))
    }

    @Test
    fun testCancelModalIfMinutesNotEmpty() {
        navigateToTrackAssociateScreen()

        // Enter track name
        onView(withId(R.id.etMinutos)).perform(typeText("3"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Click cancel button
        onView(withId(R.id.btnCancel)).perform(click())

        // Should show cancel modal
        onView(withText("¿Desea cancelar la asociación de la canción?")).check(matches(isDisplayed()))
    }

    @Test
    fun testCancelModalIfSecondsNotEmpty() {
        navigateToTrackAssociateScreen()

        // Enter track name
        onView(withId(R.id.etSegundos)).perform(typeText("45"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Click cancel button
        onView(withId(R.id.btnCancel)).perform(click())

        // Should show cancel modal
        onView(withText("¿Desea cancelar la asociación de la canción?")).check(matches(isDisplayed()))
    }

    @Test
    fun testCancelModalNoSelected() {
        navigateToTrackAssociateScreen()

        // Enter track name with special characters
        onView(withId(R.id.etTrackName)).perform(typeText("Song"))
        onView(withId(R.id.etMinutos)).perform(typeText("3"))
        onView(withId(R.id.etSegundos)).perform(typeText("45"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Click cancel button
        onView(withId(R.id.btnCancel)).perform(click())

        // Click 'No' on modal
        onView(withText("No")).perform(click())

        // Should stay on the same screen
        onView(withText("Asociar canción")).check(matches(isDisplayed()))
    }

    @Test
    fun testCancelModalYesSelected() {
        navigateToTrackAssociateScreen()

        // Enter track name with special characters
        onView(withId(R.id.etTrackName)).perform(typeText("Song"))
        onView(withId(R.id.etMinutos)).perform(typeText("3"))
        onView(withId(R.id.etSegundos)).perform(typeText("45"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Click cancel button
        onView(withId(R.id.btnCancel)).perform(click())

        // Click 'No' on modal
        onView(withText("Sí")).perform(click())

        // Should return to album detail
        onView(withId(R.id.tvTrackCount)).check(matches(isDisplayed()))
    }

    @Test
    fun testAlbumDetailModalIfNameNotEmpty() {
        navigateToTrackAssociateScreen()

        // Enter track name
        onView(withId(R.id.etTrackName)).perform(typeText("Song"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Go back
        onView(withContentDescription(
            androidx.appcompat.R.string.abc_action_bar_up_description
        )).perform(click())

        // Should show album detail modal
        onView(withText("¿Desea volver al detalle del álbum?")).check(matches(isDisplayed()))
    }

    @Test
    fun testAlbumDetailModalIfMinutesNotEmpty() {
        navigateToTrackAssociateScreen()

        // Enter track name
        onView(withId(R.id.etMinutos)).perform(typeText("3"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Go back
        onView(withContentDescription(
            androidx.appcompat.R.string.abc_action_bar_up_description
        )).perform(click())

        // Should show album detail modal
        onView(withText("¿Desea volver al detalle del álbum?")).check(matches(isDisplayed()))
    }

    @Test
    fun testAlbumDetailModalIfSecondsNotEmpty() {
        navigateToTrackAssociateScreen()

        // Enter track name
        onView(withId(R.id.etSegundos)).perform(typeText("45"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Go back
        onView(withContentDescription(
            androidx.appcompat.R.string.abc_action_bar_up_description
        )).perform(click())

        // Should show album detail modal
        onView(withText("¿Desea volver al detalle del álbum?")).check(matches(isDisplayed()))
    }

    @Test
    fun testAlbumDetailModalNoSelected() {
        navigateToTrackAssociateScreen()

        // Enter track name with special characters
        onView(withId(R.id.etTrackName)).perform(typeText("Song"))
        onView(withId(R.id.etMinutos)).perform(typeText("3"))
        onView(withId(R.id.etSegundos)).perform(typeText("45"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Go back
        onView(withContentDescription(
            androidx.appcompat.R.string.abc_action_bar_up_description
        )).perform(click())

        // Click 'No' on modal
        onView(withText("No")).perform(click())

        // Should stay on the same screen
        onView(withText("Asociar canción")).check(matches(isDisplayed()))
    }

    @Test
    fun testAlbumDetailModalYesSelected() {
        navigateToTrackAssociateScreen()

        // Enter track name with special characters
        onView(withId(R.id.etTrackName)).perform(typeText("Song"))
        onView(withId(R.id.etMinutos)).perform(typeText("3"))
        onView(withId(R.id.etSegundos)).perform(typeText("45"))
        onView(isRoot()).perform(closeSoftKeyboard())

        // Go back
        onView(withContentDescription(
            androidx.appcompat.R.string.abc_action_bar_up_description
        )).perform(click())

        // Click 'No' on modal
        onView(withText("Sí")).perform(click())

        // Should return to album detail
        onView(withId(R.id.tvTrackCount)).check(matches(isDisplayed()))
    }
}
