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
import com.misw.app.network.CacheManager
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
class CollectorListTest {

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

        // Limpiar cache para asegurar independencia de tests
        CacheManager.getInstance().clearCache()

        val initialCollectorsJson = """
            [
                {"id":1, "name":"Manolo Bellon", "telephone":"3502457896", "email":"manolo@bellon.com"},
                {"id":2, "name":"Jaime Monsalve", "telephone":"3012357936", "email":"j.monsalve@radio.com"},
                {"id":3, "name":"Juan Villoro", "telephone":"3412569874", "email":"j.villoro@unam.mx"}
            ]
        """.trimIndent()
        
        navigateToCollectors(200, initialCollectorsJson)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        RetrofitClient.setBaseUrl(BuildConfig.BASE_URL)
    }

    private fun navigateToCollectors(responseCode: Int = 200, responseBody: String = "[]") {
        mockWebServer.enqueue(MockResponse().setResponseCode(responseCode).setBody(responseBody))
        onView(withId(R.id.include_collectors)).perform(click())
    }

    @Test
    fun testVisibilityOfAllComponents() {
        onView(withId(R.id.collectors_search_bar)).check(matches(isDisplayed()))
        onView(withId(R.id.rvCollectorList)).check(matches(isDisplayed()))
    }

    @Test
    fun testSearchFiltering() {
        val collectorToSearch = "Manolo Bellon"

        onView(allOf(withId(R.id.etSearchAlbum), isDescendantOfA(withId(R.id.collectors_search_bar))))
            .perform(replaceText(collectorToSearch), closeSoftKeyboard())

        onView(withId(R.id.rvCollectorList))
            .check(matches(hasDescendant(withText(containsString(collectorToSearch)))))
    }

    @Test
    fun testSearchNoResults() {
        val nonExistentCollector = "Fake-" + faker.lorem().characters(10)

        onView(allOf(withId(R.id.etSearchAlbum), isDescendantOfA(withId(R.id.collectors_search_bar))))
            .perform(replaceText(nonExistentCollector), closeSoftKeyboard())

        onView(withId(R.id.rvCollectorList)).check(matches(not(isDisplayed())))
        onView(withId(R.id.llEmptyState)).check(matches(isDisplayed()))
        onView(withId(R.id.tvEmptyState)).check(matches(withText(R.string.no_collectors_found)))
    }

    @Test
    fun testEmptyListFromServer() {
        androidx.test.espresso.Espresso.pressBack()
        // Limpiar cache para que el repositorio haga la peticion de red
        CacheManager.getInstance().clearCache()
        navigateToCollectors(200, "[]")

        onView(withId(R.id.llEmptyState)).check(matches(isDisplayed()))
        onView(withId(R.id.tvEmptyState)).check(matches(withText(R.string.no_collectors_found)))
        onView(withId(R.id.rvCollectorList)).check(matches(not(isDisplayed())))
    }

    @Test
    fun testServerError() {
        androidx.test.espresso.Espresso.pressBack()
        // Limpiar cache para que el repositorio haga la peticion de red
        CacheManager.getInstance().clearCache()
        navigateToCollectors(500, "Internal Server Error")
        
        onView(withId(R.id.llEmptyState)).check(matches(isDisplayed()))
        onView(withId(R.id.tvEmptyState)).check(matches(withText(R.string.error_loading_content)))
        onView(withId(R.id.rvCollectorList)).check(matches(not(isDisplayed())))
    }

    @Test
    fun testSearchAndClearRestoresList() {
        onView(allOf(withId(R.id.etSearchAlbum), isDescendantOfA(withId(R.id.collectors_search_bar))))
            .perform(replaceText("Manolo"), closeSoftKeyboard())

        onView(allOf(withId(R.id.etSearchAlbum), isDescendantOfA(withId(R.id.collectors_search_bar))))
            .perform(replaceText(""), closeSoftKeyboard())

        onView(withId(R.id.rvCollectorList))
            .check(matches(hasMinimumChildCount(1)))
    }

    @Test
    fun testRecyclerViewContent() {
        val expectedCollectors = listOf("Manolo Bellon", "Jaime Monsalve", "Juan Villoro")
        
        expectedCollectors.forEach { collectorName ->
            onView(withId(R.id.rvCollectorList))
                .perform(RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText(containsString(collectorName)))
                ))
                .check(matches(hasDescendant(withText(containsString(collectorName)))))
        }
    }
}
