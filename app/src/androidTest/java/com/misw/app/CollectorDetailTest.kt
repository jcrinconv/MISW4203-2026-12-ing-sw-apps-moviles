package com.misw.app

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
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
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CollectorDetailTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    private val mockWebServer = MockWebServer()

    @Before
    fun setup() {
        val collectorsJson = """[{"id":1, "name":"Manolo Bellon", "telephone":"3502457896", "email":"manolo@bellon.com"}]"""
        val detailJson = """
            {
                "id":1, "name":"Manolo Bellon", "telephone":"3502457896", "email":"manolo@bellon.com",
                "comments": [{"id":1, "description":"Excelente", "rating":5}],
                "favoritePerformers": [{"id":1, "name":"Axl Rose", "image":""}],
                "collectorAlbums": []
            }
        """.trimIndent()
        val albumsJson = """
            [
                {
                    "id": 1, "price": 35000, "status": "Active",
                    "album": {"id": 1, "name": "Discovery", "cover": "", "releaseDate": "", "description": "", "genre": "Electronic", "recordLabel": "EMI"}
                }
            ]
        """.trimIndent()

        val dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path ?: return MockResponse().setResponseCode(404)
                return when {
                    path == "/collectors" -> MockResponse().setResponseCode(200).setBody(collectorsJson)
                    path == "/collectors/1" -> MockResponse().setResponseCode(200).setBody(detailJson)
                    path == "/collectors/1/albums" -> MockResponse().setResponseCode(200).setBody(albumsJson)
                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

        mockWebServer.dispatcher = dispatcher
        mockWebServer.start(0)
        RetrofitClient.setBaseUrl(mockWebServer.url("/").toString())
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        CacheManager.getInstance(InstrumentationRegistry.getInstrumentation().targetContext).clearCache()

        onView(withId(R.id.include_collectors)).perform(click())
        onView(withId(R.id.rvCollectorList))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        RetrofitClient.setBaseUrl(BuildConfig.BASE_URL)
    }

    @Test
    fun testVisibilityOfAllComponents() {
        onView(withId(R.id.tvCollectorName)).check(matches(isDisplayed()))
        onView(withId(R.id.tvCollectorEmail)).check(matches(isDisplayed()))
        onView(withId(R.id.etSearch)).check(matches(isDisplayed()))
        onView(withId(R.id.tabLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.rvCollectorDetail)).check(matches(isDisplayed()))
    }

    @Test
    fun testCollectorInformationIsCorrect() {
        // Use containsString because the fragment inserts newlines in the name
        onView(withId(R.id.tvCollectorName)).check(matches(withText(containsString("Manolo"))))
        onView(withId(R.id.tvCollectorEmail)).check(matches(withText("manolo@bellon.com")))
    }

    @Test
    fun testTabSwitchingContent() {
        // Verify default tab (Albums) shows "Discovery"
        onView(withId(R.id.rvCollectorDetail))
            .check(matches(hasDescendant(withText("Discovery"))))

        // Switch to Artists tab
        onView(withText(R.string.favorite_artists)).perform(click())

        // Verify it now shows the favorite artist
        onView(withId(R.id.rvCollectorDetail))
            .check(matches(hasDescendant(withText("Axl Rose"))))
    }

    @Test
    fun testSearchFiltering() {
        // Search for existing album
        onView(withId(R.id.etSearch)).perform(replaceText("Discovery"), closeSoftKeyboard())
        onView(withId(R.id.rvCollectorDetail)).check(matches(hasDescendant(withText("Discovery"))))

        // Search for non-existent text
        onView(withId(R.id.etSearch)).perform(replaceText("NonExistentText"), closeSoftKeyboard())
        onView(withId(R.id.rvCollectorDetail)).check(matches(hasChildCount(0)))
    }
}