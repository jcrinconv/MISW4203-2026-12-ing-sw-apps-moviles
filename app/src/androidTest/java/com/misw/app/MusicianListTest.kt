package com.misw.app

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.misw.app.network.CacheManager
import com.misw.app.network.EspressoIdlingResource
import com.misw.app.network.RetrofitClient
import com.misw.app.ui.MainActivity
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MusicianListTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    private val mockWebServer = MockWebServer()

    @Before
    fun setup() {
        mockWebServer.start(0)
        RetrofitClient.setBaseUrl(mockWebServer.url("/").toString())
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        
        // Limpiar cache antes de cada test
        CacheManager.getInstance().clearCache()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        RetrofitClient.setBaseUrl(BuildConfig.BASE_URL)
    }

    private fun navigateToMusicians(responseCode: Int = 200, responseBody: String = "[]") {
        mockWebServer.enqueue(MockResponse().setResponseCode(responseCode).setBody(responseBody))
        onView(withId(R.id.include_artists)).perform(click())
    }

    @Test
    fun testVisibilityOfAllComponents() {
        val json = """[{"id":1, "name":"Axl Rose", "image":"https://picsum.photos/200", "description":"D1", "birthDate":"1962-02-06T00:00:00.000Z"}]"""
        navigateToMusicians(200, json)

        onView(withId(R.id.searchBar)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSwapOrder)).check(matches(isDisplayed()))
        onView(withId(R.id.rvMusicians)).check(matches(isDisplayed()))
    }

    @Test
    fun testEmptyResponseShowsEmptyState() {
        // Escenario: El API retorna una lista vacía []
        navigateToMusicians(200, "[]")

        onView(withId(R.id.llEmptyState)).check(matches(isDisplayed()))
        onView(withId(R.id.tvEmptyState)).check(matches(withText(R.string.no_artists_found)))
        onView(withId(R.id.rvMusicians)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun testError500ShowsErrorMessage() {
        // Escenario: El API retorna un error 500
        navigateToMusicians(500, "Internal Server Error")

        onView(withId(R.id.llEmptyState)).check(matches(isDisplayed()))
        // Verificamos que se muestre el mensaje de error definido en el ViewModel/Strings
        onView(withId(R.id.tvEmptyState)).check(matches(withText(R.string.error_loading_content)))
    }

    @Test
    fun testSearchFiltering() {
        val json = """
            [
                {"id":1, "name":"Axl Rose", "image":"...", "description":"D1", "birthDate":"..."},
                {"id":2, "name":"Freddie Mercury", "image":"...", "description":"D2", "birthDate":"..."}
            ]
        """.trimIndent()
        navigateToMusicians(200, json)

        onView(allOf(isAssignableFrom(android.widget.EditText::class.java), isDescendantOfA(withId(R.id.searchBar))))
            .perform(replaceText("Freddie"), closeSoftKeyboard())

        onView(withId(R.id.rvMusicians))
            .check(matches(hasDescendant(withText(containsString("Freddie")))))
    }

    @Test
    fun testToggleSortOrder() {
        val json = """
            [
                {"id":1, "name":"Axl Rose", "image":"...", "description":"D1", "birthDate":"..."},
                {"id":2, "name":"Freddie Mercury", "image":"...", "description":"D2", "birthDate":"..."}
            ]
        """.trimIndent()
        navigateToMusicians(200, json)

        // Inicial: Axl Rose primero
        onView(withId(R.id.rvMusicians)).check(matches(atPosition(0, hasDescendant(withText("Axl Rose")))))

        onView(withId(R.id.btnSwapOrder)).perform(click())

        // Después de toggle: Freddie Mercury primero
        onView(withId(R.id.rvMusicians)).check(matches(atPosition(0, hasDescendant(withText("Freddie Mercury")))))
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
}
