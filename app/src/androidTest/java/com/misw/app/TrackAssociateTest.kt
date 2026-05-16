package com.misw.app

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.recyclerview.widget.RecyclerView
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

@LargeTest
@RunWith(AndroidJUnit4::class)
class TrackAssociateTest {

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
                        """[{"id":1,"name":"Test Album","cover":"https://picsum.photos/200","releaseDate":"2020-01-01T00:00:00.000Z","description":"Desc","genre":"Rock","recordLabel":"EMI"}]"""
                    )
                    path == "/albums/1" -> MockResponse().setResponseCode(200).setBody(
                        """{"id":1,"name":"Test Album","cover":"https://picsum.photos/200","releaseDate":"2020-01-01T00:00:00.000Z","description":"Desc","genre":"Rock","recordLabel":"EMI","tracks":[]}"""
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

    private fun navigateToAlbumDetail() {
        onView(withId(R.id.include_albums)).perform(click())
        onView(withId(R.id.rvAlbumList)).perform(
            RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                hasDescendant(withText("Test Album")), click()
            )
        )
    }

    @Test
    fun testAssociateTracksButtonIsVisible() {
        navigateToAlbumDetail()
        onView(withId(R.id.btnAssociateTracks)).check(matches(isDisplayed()))
    }

    @Test
    fun testAssociateTracksButtonNavigatesToAssociateScreen() {
        navigateToAlbumDetail()
        onView(withId(R.id.btnAssociateTracks)).perform(click())
        onView(withId(R.id.tvAssociateTrackTitle)).check(matches(isDisplayed()))
    }
}
