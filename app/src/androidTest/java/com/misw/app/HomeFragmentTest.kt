package com.misw.app

import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.javafaker.Faker
import com.misw.app.network.CacheManager
import com.misw.app.network.EspressoIdlingResource
import com.misw.app.network.RetrofitClient
import com.misw.app.ui.MainActivity
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Matchers.allOf
import org.hamcrest.core.IsInstanceOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    private val mockWebServer = MockWebServer()
    private val faker = Faker()

    private lateinit var fakerGeneratedAlbumName: String

    @Before
    fun setup() {
        // Limpiar caché para asegurar que se obtengan datos frescos del MockWebServer
        CacheManager.getInstance().clearCache()

        // Generar un nombre aleatorio razonable con Faker para evitar truncamiento en UI
        fakerGeneratedAlbumName = faker.lorem().characters(20)

        val dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path ?: return MockResponse().setResponseCode(404)

                return when (path) {
                    "/albums" -> {
                        val albumsJson = """
                            [
                                {"id":1, "name":"A Day at the Races", "cover":"https://picsum.photos/200", "releaseDate":"1976-12-10T00:00:00.000Z", "description":"D1", "genre":"Rock", "recordLabel":"EMI"},
                                {"id":99, "name":"$fakerGeneratedAlbumName", "cover":"https://picsum.photos/200", "releaseDate":"2023-01-01T00:00:00.000Z", "description":"D99", "genre":"Pop", "recordLabel":"Sony"}
                            ]
                        """.trimIndent()
                        MockResponse().setResponseCode(200).setBody(albumsJson)
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

    @Test
    fun checkTopAppBarTest() {
        onView(
            allOf(
                withId(R.id.topAppBar), withParent(
                    allOf(
                        withId(R.id.appBarLayout),
                        withParent(IsInstanceOf.instanceOf(ViewGroup::class.java))
                    )
                ), isDisplayed()
            )
        ).check(matches(isDisplayed()))
    }

    @Test
    fun checkHomeAlbumsButtonTest() {
        onView(allOf(withId(R.id.include_albums), isDisplayed())).check(matches(isDisplayed()))
        onView(
            allOf(
                withId(R.id.tv_menu_title),
                isDescendantOfA(withId(R.id.include_albums)),
                isDisplayed()
            )
        ).check(matches(withText("Álbumes")))
    }

    @Test
    fun checkHomeArtistsButtonTest() {
        onView(allOf(withId(R.id.include_artists), isDisplayed())).check(matches(isDisplayed()))
        onView(
            allOf(
                withId(R.id.tv_menu_title),
                isDescendantOfA(withId(R.id.include_artists)),
                isDisplayed()
            )
        ).check(matches(withText("Artistas")))
    }

    @Test
    fun checkHomeTracksButtonTest() {
        onView(allOf(withId(R.id.include_tracks), isDisplayed())).check(matches(isDisplayed()))
        onView(
            allOf(
                withId(R.id.tv_menu_title),
                isDescendantOfA(withId(R.id.include_tracks)),
                isDisplayed()
            )
        ).check(matches(withText("Tracks")))
    }

    @Test
    fun checkHomeCollectorsButtonTest() {
        onView(allOf(withId(R.id.include_collectors), isDisplayed())).check(matches(isDisplayed()))
        onView(
            allOf(
                withId(R.id.tv_menu_title),
                isDescendantOfA(withId(R.id.include_collectors)),
                isDisplayed()
            )
        ).check(matches(withText("Coleccionistas")))
    }

    @Test
    fun checkNavigationToAlbumsWithFakerData() {
        onView(withId(R.id.include_albums)).perform(click())
        onView(withId(R.id.rvAlbumList)).check(matches(isDisplayed()))

        onView(withId(R.id.rvAlbumList))
            .perform(RecyclerViewActions.scrollTo<androidx.recyclerview.widget.RecyclerView.ViewHolder>(
                hasDescendant(withText(fakerGeneratedAlbumName))
            ))
            .check(matches(hasDescendant(withText(fakerGeneratedAlbumName))))
    }
}
