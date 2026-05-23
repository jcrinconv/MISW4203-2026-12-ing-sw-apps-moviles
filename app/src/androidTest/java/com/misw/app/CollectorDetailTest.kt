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
import com.misw.app.network.CacheManager
import com.misw.app.network.EspressoIdlingResource
import com.misw.app.network.RetrofitClient
import com.misw.app.ui.MainActivity
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.not
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

    private val collectorsJson =
        """[{"id":1, "name":"Manolo Bellon", "telephone":"3502457896", "email":"manolo@bellon.com"}]"""

    private val detailJson = """
        {
            "id":1, "name":"Manolo Bellon", "telephone":"3502457896", "email":"manolo@bellon.com",
            "comments": [{"id":1, "description":"Excelente", "rating":5}],
            "favoritePerformers": [{"id":1, "name":"Axl Rose", "image":""}],
            "collectorAlbums": []
        }
    """.trimIndent()

    private val albumsJson = """
        [
            {
                "id": 1, "price": 35000, "status": "Active",
                "album": {"id": 1, "name": "Discovery", "cover": "", "releaseDate": "", "description": "", "genre": "Electronic", "recordLabel": "EMI"}
            }
        ]
    """.trimIndent()

    private fun happyPathDispatcher() = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val path = request.path ?: return MockResponse().setResponseCode(404)
            return when {
                path == "/collectors"        -> MockResponse().setResponseCode(200).setBody(collectorsJson)
                path == "/collectors/1"      -> MockResponse().setResponseCode(200).setBody(detailJson)
                path == "/collectors/1/albums" -> MockResponse().setResponseCode(200).setBody(albumsJson)
                else -> MockResponse().setResponseCode(404)
            }
        }
    }

    @Before
    fun setup() {
        mockWebServer.dispatcher = happyPathDispatcher()
        mockWebServer.start(0)
        RetrofitClient.setBaseUrl(mockWebServer.url("/").toString())
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        CacheManager.getInstance().clearCache()

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
        onView(withId(R.id.llEmptyState)).check(matches(not(isDisplayed())))
        onView(withId(R.id.llErrorState)).check(matches(not(isDisplayed())))
    }

    @Test
    fun testCollectorInformationIsCorrect() {
        onView(withId(R.id.tvCollectorName)).check(matches(withText(containsString("Manolo"))))
        onView(withId(R.id.tvCollectorEmail)).check(matches(withText("manolo@bellon.com")))
    }

    @Test
    fun testTabSwitchingContent() {
        // Default tab (Albums) shows "Discovery"
        onView(withId(R.id.rvCollectorDetail))
            .check(matches(hasDescendant(withText("Discovery"))))

        // Switch to Artists tab
        onView(withText(R.string.favorite_artists)).perform(click())

        // Artists tab shows "Axl Rose"
        onView(withId(R.id.rvCollectorDetail))
            .check(matches(hasDescendant(withText("Axl Rose"))))
    }

    @Test
    fun testSearchFiltering() {
        // Search for existing album → results shown, no empty state
        onView(withId(R.id.etSearch)).perform(replaceText("Discovery"), closeSoftKeyboard())
        onView(withId(R.id.rvCollectorDetail)).check(matches(isDisplayed()))
        onView(withId(R.id.rvCollectorDetail)).check(matches(hasDescendant(withText("Discovery"))))
        onView(withId(R.id.llEmptyState)).check(matches(not(isDisplayed())))

        // Search for non-existent text → empty state shown with the search term
        onView(withId(R.id.etSearch)).perform(replaceText("NonExistentText"), closeSoftKeyboard())
        onView(withId(R.id.llEmptyState)).check(matches(isDisplayed()))
        onView(withId(R.id.tvEmptyState)).check(matches(withText(containsString("NonExistentText"))))
        onView(withId(R.id.rvCollectorDetail)).check(matches(not(isDisplayed())))
        onView(withId(R.id.llErrorState)).check(matches(not(isDisplayed())))

        // Clear search → results return, empty state hidden
        onView(withId(R.id.etSearch)).perform(replaceText(""), closeSoftKeyboard())
        onView(withId(R.id.llEmptyState)).check(matches(not(isDisplayed())))
        onView(withId(R.id.rvCollectorDetail)).check(matches(isDisplayed()))
    }

    @Test
    fun testErrorState() {
        // Clear cache and switch to a dispatcher that fails detail requests
        CacheManager.getInstance().clearCache()
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path ?: return MockResponse().setResponseCode(404)
                return when {
                    path == "/collectors" -> MockResponse().setResponseCode(200).setBody(collectorsJson)
                    else -> MockResponse().setResponseCode(500)
                }
            }
        }

        // Go back to the collectors list (onViewCreated re-fetches with /collectors → 200)
        androidx.test.espresso.Espresso.pressBack()

        // Navigate to detail again → /collectors/1 returns 500 → error state
        onView(withId(R.id.rvCollectorList))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        onView(withId(R.id.llErrorState)).check(matches(isDisplayed()))
        onView(withId(R.id.rvCollectorDetail)).check(matches(not(isDisplayed())))
        onView(withId(R.id.llEmptyState)).check(matches(not(isDisplayed())))
    }
}
