package com.misw.app

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
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
import org.hamcrest.Matchers.not

@LargeTest
@RunWith(AndroidJUnit4::class)
class MusicianDetailFragmentTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    private val mockWebServer = MockWebServer()

    @Before
    fun setup() {
        val dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path ?: return MockResponse().setResponseCode(404)
                return when {
                    path == "/musicians" -> {
                        val musiciansJson = """
                            [
                                {"id":1, "name":"Axl Rose", "image":"https://picsum.photos/200", "description":"Description 1", "birthDate":"1962-02-06T00:00:00.000Z"},
                                {"id":2, "name":"Empty Albums Artist", "image":"https://picsum.photos/200", "description":"Description 2", "birthDate":"1962-02-06T00:00:00.000Z"},
                                {"id":3, "name":"Error Artist", "image":"https://picsum.photos/200", "description":"Description 3", "birthDate":"1962-02-06T00:00:00.000Z"},
                                {"id":4, "name":"Ruben Blades", "image":"https://picsum.photos/200", "description":"Description 4", "birthDate":"1948-07-16T00:00:00.000Z"}
                            ]
                        """.trimIndent()
                        MockResponse().setResponseCode(200).setBody(musiciansJson)
                    }
                    path == "/musicians/1" -> {
                        val detailJson = """
                            {
                                "id":1, "name":"Axl Rose", "image":"https://picsum.photos/200", 
                                "description":"Description 1", "birthDate":"1962-02-06T00:00:00.000Z",
                                "albums": [
                                    {"id":1, "name":"Appetite for Destruction", "cover":"https://picsum.photos/200", "releaseDate":"1987-07-21T00:00:00.000Z", "description":"", "genre":"Rock", "recordLabel":"Geffen", "tracks": []},
                                    {"id":2, "name":"Use Your Illusion I", "cover":"https://picsum.photos/200", "releaseDate":"1991-09-17T00:00:00.000Z", "description":"", "genre":"Rock", "recordLabel":"Geffen", "tracks": []}
                                ],
                                "performerPrizes": []
                            }
                        """.trimIndent()
                        MockResponse().setResponseCode(200).setBody(detailJson)
                    }
                    path == "/musicians/2" -> {
                        val detailJson = """
                            {
                                "id":2, "name":"Empty Albums Artist", "image":"https://picsum.photos/200", 
                                "description":"Description 2", "birthDate":"1962-02-06T00:00:00.000Z",
                                "albums": [],
                                "performerPrizes": []
                            }
                        """.trimIndent()
                        MockResponse().setResponseCode(200).setBody(detailJson)
                    }
                    path == "/musicians/3" -> {
                        MockResponse().setResponseCode(500)
                    }
                    path == "/musicians/4" -> {
                        val detailJson = """
                            {
                                "id":4, "name":"Ruben Blades", "image":"https://picsum.photos/200",
                                "description":"Description 4", "birthDate":"1948-07-16T00:00:00.000Z",
                                "albums":[],
                                "performerPrizes":[{"id":1,"premiationDate":"1978-12-10T00:00:00.000Z"}]
                            }
                        """.trimIndent()
                        MockResponse().setResponseCode(200).setBody(detailJson)
                    }
                    path == "/musicians/1/performerPrizes" -> {
                        MockResponse().setResponseCode(200).setBody("[]")
                    }
                    path == "/musicians/2/performerPrizes" -> {
                        MockResponse().setResponseCode(200).setBody("[]")
                    }
                    path == "/musicians/4/performerPrizes" -> {
                        val prizesJson = """
                            [
                                {
                                    "id":1,
                                    "premiationDate":"1978-12-10T00:00:00.000Z",
                                    "prize":
                                        {
                                            "id":1,
                                            "organization":"National Academy of Recording Arts & Sciences",
                                            "name":"Grammy Award",
                                            "description":"Grammy description"
                                        }
                                
                                }
                            ]
                        """.trimIndent()
                        MockResponse().setResponseCode(200).setBody(prizesJson)
                    }
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

    private fun atPosition(position: Int, itemMatcher: org.hamcrest.Matcher<View>): org.hamcrest.Matcher<View> {
        return object : androidx.test.espresso.matcher.BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: org.hamcrest.Description) {
                description.appendText("has item at position $position: ")
                itemMatcher.describeTo(description)
            }
            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position) ?: return false
                return itemMatcher.matches(viewHolder.itemView)
            }
        }
    }

    @Test
    fun testMusicianNameVisualization() {
        onView(withId(R.id.include_artists)).perform(click())

        onView(withId(R.id.rvMusicians)).perform(
            RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText("Axl Rose")), click())
        )

        onView(withId(R.id.tvMusicianName)).check(matches(isDisplayed()))
        onView(withId(R.id.tvMusicianName)).check(matches(withText("Axl Rose")))
    }

    @Test
    fun testMusicianDescriptionVisualization() {
        onView(withId(R.id.include_artists)).perform(click())

        onView(withId(R.id.rvMusicians)).perform(
            RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText("Axl Rose")), click())
        )

        onView(withId(R.id.tvDescription)).check(matches(isDisplayed()))
        onView(withId(R.id.tvDescription)).check(matches(not(withText(""))))
    }

    @Test
    fun testMusicianBirthdateVisualization() {
        onView(withId(R.id.include_artists)).perform(click())

        onView(withId(R.id.rvMusicians)).perform(
            RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText("Axl Rose")), click())
        )

        onView(withId(R.id.tvMusicianBirthDate)).check(matches(isDisplayed()))
        onView(withId(R.id.tvMusicianBirthDate)).check(matches(not(withText(""))))
    }

    @Test
    fun testMusicianNoPrizesVisualization() {
        onView(withId(R.id.include_artists)).perform(click())

        onView(withId(R.id.rvMusicians)).perform(
            RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText("Axl Rose")), click())
        )

        onView(withId(R.id.flexPrizes)).check(matches(isDisplayed()))
        onView(withText("Sin premios")).check(matches(isDisplayed()))
    }

    @Test
    fun testMusicianPrizesVisualization() {
        onView(withId(R.id.include_artists)).perform(click())

        onView(withId(R.id.rvMusicians)).perform(
            RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText("Ruben Blades")), click())
        )

        onView(withId(R.id.flexPrizes)).check(matches(isDisplayed()))
        onView(withText("Grammy Award")).check(matches(isDisplayed()))
    }

    @Test
    fun testAlbumListVisualization() {
        // 1. Navegar al listado de músicos
        onView(withId(R.id.include_artists)).perform(click())

        // 2. Click en Axl Rose
        onView(withId(R.id.rvMusicians)).perform(
            RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText("Axl Rose")), click())
        )

        // 3. Scroll and verify elements
        onView(isAssignableFrom(CoordinatorLayout::class.java)).perform(swipeUp())
        
        onView(withId(R.id.searchBar)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSortName)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSortDate)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSwapOrder)).check(matches(isDisplayed()))
        onView(withId(R.id.rvAlbums)).check(matches(isDisplayed()))

        // Verify albums are loaded
        onView(withId(R.id.rvAlbums)).check(matches(atPosition(0, hasDescendant(withText("Appetite for Destruction")))))
        onView(withId(R.id.rvAlbums)).check(matches(atPosition(1, hasDescendant(withText("Use Your Illusion I")))))
    }

    @Test
    fun testEmptyAlbumListEdgeCase() {
        onView(withId(R.id.include_artists)).perform(click())

        onView(withId(R.id.rvMusicians)).perform(
            RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText("Empty Albums Artist")), click())
        )

        onView(isAssignableFrom(CoordinatorLayout::class.java)).perform(swipeUp())

        // The recycler view will not be visible but no albums layout will
        onView(withId(R.id.llNoAlbumsState)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.rvAlbums)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun testErrorLoadingMusicianDetail() {
        onView(withId(R.id.include_artists)).perform(click())

        onView(withId(R.id.rvMusicians)).perform(
            RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText("Error Artist")), click())
        )

        onView(withId(R.id.llErrorState)).check(matches(isDisplayed()))
        onView(withId(R.id.tvErrorState)).check(matches(withText(R.string.error_loading_content)))
        onView(withId(R.id.nestedScrollView)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }
}
