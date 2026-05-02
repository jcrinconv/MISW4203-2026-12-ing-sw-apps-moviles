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
import com.github.javafaker.Faker
import com.misw.app.network.EspressoIdlingResource
import com.misw.app.network.RetrofitClient
import com.misw.app.ui.MainActivity
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AlbumsListTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    private val mockWebServer = MockWebServer()
    private val faker = Faker()

    @Before
    fun setup() {
        mockWebServer.start(0)
        RetrofitClient.setBaseUrl(mockWebServer.url("/").toString())
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

        val initialAlbumsJson = """
            [
                {"id":1, "name":"A Day at the Races", "cover":"https://picsum.photos/200", "releaseDate":"1976-12-10T00:00:00.000Z", "description":"D1", "genre":"Rock", "recordLabel":"EMI"},
                {"id":2, "name":"A Night at the Opera", "cover":"https://picsum.photos/200", "releaseDate":"1975-11-21T00:00:00.000Z", "description":"D2", "genre":"Rock", "recordLabel":"EMI"},
                {"id":3, "name":"Buscando América", "cover":"https://picsum.photos/200", "releaseDate":"1984-04-01T00:00:00.000Z", "description":"D3", "genre":"Salsa", "recordLabel":"Elektra"}
            ]
        """.trimIndent()
        
        navigateToAlbums(200, initialAlbumsJson)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        RetrofitClient.setBaseUrl(BuildConfig.BASE_URL)
    }

    private fun navigateToAlbums(responseCode: Int = 200, responseBody: String = "[]") {
        mockWebServer.enqueue(MockResponse().setResponseCode(responseCode).setBody(responseBody))
        onView(withId(R.id.include_albums)).perform(click())
    }

    @Test
    fun testVisibilityOfAllComponents() {
        onView(withId(R.id.searchBar)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSortName)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSortDate)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSwapOrder)).check(matches(isDisplayed()))
        onView(withId(R.id.rvAlbumList)).check(matches(isDisplayed()))
    }

    @Test
    fun testSearchFiltering() {
        val albumToSearch = "Buscando América"

        onView(allOf(isAssignableFrom(android.widget.EditText::class.java), isDescendantOfA(withId(R.id.searchBar))))
            .perform(replaceText(albumToSearch), closeSoftKeyboard())

        onView(withId(R.id.rvAlbumList))
            .check(matches(hasDescendant(withText(containsString(albumToSearch)))))
    }

    @Test
    fun testSearchNoResults() {
        val nonExistentAlbum = "Fake-" + faker.lorem().characters(10)

        onView(allOf(isAssignableFrom(android.widget.EditText::class.java), isDescendantOfA(withId(R.id.searchBar))))
            .perform(replaceText(nonExistentAlbum), closeSoftKeyboard())

        onView(withId(R.id.rvAlbumList)).check(matches(not(isDisplayed())))
        onView(withId(R.id.llEmptyState)).check(matches(isDisplayed()))
        onView(withId(R.id.tvEmptyState)).check(matches(withText(R.string.no_albums_found)))
    }

    @Test
    fun testEmptyListFromServer() {
        androidx.test.espresso.Espresso.pressBack()
        navigateToAlbums(200, "[]")

        onView(withId(R.id.llEmptyState)).check(matches(isDisplayed()))
        onView(withId(R.id.tvEmptyState)).check(matches(withText(R.string.no_albums_found)))
        onView(withId(R.id.rvAlbumList)).check(matches(not(isDisplayed())))
    }

    @Test
    fun testServerError() {
        androidx.test.espresso.Espresso.pressBack()
        navigateToAlbums(500, "Internal Server Error")
        
        onView(withId(R.id.llEmptyState)).check(matches(isDisplayed()))
        onView(withId(R.id.tvEmptyState)).check(matches(withText(R.string.error_loading_content)))
        onView(withId(R.id.rvAlbumList)).check(matches(not(isDisplayed())))
    }

    @Test
    fun testSearchAndClearRestoresList() {
        onView(allOf(isAssignableFrom(android.widget.EditText::class.java), isDescendantOfA(withId(R.id.searchBar))))
            .perform(replaceText("Buscando"), closeSoftKeyboard())

        onView(allOf(isAssignableFrom(android.widget.EditText::class.java), isDescendantOfA(withId(R.id.searchBar))))
            .perform(replaceText(""), closeSoftKeyboard())

        onView(withId(R.id.rvAlbumList))
            .check(matches(hasMinimumChildCount(1)))
    }

    @Test
    fun testRecyclerViewContent() {
        val expectedAlbums = listOf("A Day at the Races", "A Night at the Opera", "Buscando América")
        
        expectedAlbums.forEach { albumName ->
            onView(withId(R.id.rvAlbumList))
                .perform(RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText(containsString(albumName)))
                ))
                .check(matches(hasDescendant(withText(containsString(albumName)))))
        }
    }
}
