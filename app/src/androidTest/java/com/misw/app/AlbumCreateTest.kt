package com.misw.app

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.github.javafaker.Faker
import com.misw.app.network.CacheManager
import com.misw.app.network.EspressoIdlingResource
import com.misw.app.network.RetrofitClient
import com.misw.app.ui.MainActivity
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AlbumCreateTest {

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
        
        CacheManager.getInstance(InstrumentationRegistry.getInstrumentation().targetContext).clearCache()

        val dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when {
                    request.method == "GET" && request.path?.contains("albums") == true -> {
                        MockResponse().setResponseCode(200).setBody("[]")
                    }
                    request.method == "POST" && request.path?.contains("albums") == true -> {
                        val albumResponse = """{"id": 1, "name": "New Album", "cover": "https://picsum.photos/200", "releaseDate": "2023-05-15T00:00:00-05:00", "description": "desc", "genre": "Rock", "recordLabel": "EMI"}"""
                        MockResponse().setResponseCode(201).setBody(albumResponse)
                    }
                    else -> MockResponse().setResponseCode(404)
                }
            }
        }
        mockWebServer.dispatcher = dispatcher

        // Navigate to Albums List
        onView(withId(R.id.include_albums)).perform(click())

        // Navigate to Create Album (button in toolbar)
        onView(withId(R.id.buttonCreate)).perform(click())
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        RetrofitClient.setBaseUrl(BuildConfig.BASE_URL)
    }

    @Test
    fun testVisibilityOfComponents() {
        onView(withId(R.id.et_album_name)).check(matches(isDisplayed()))
        onView(withId(R.id.et_cover_url)).check(matches(isDisplayed()))
        onView(withId(R.id.et_day)).check(matches(isDisplayed()))
        onView(withId(R.id.spinner_month)).check(matches(isDisplayed()))
        onView(withId(R.id.et_year)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_create)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_cancel)).check(matches(isDisplayed()))
    }

    @Test
    fun testEmptyFieldsValidation() {
        onView(withId(R.id.btn_create)).perform(click())

        onView(withId(R.id.tv_error_name)).check(matches(withText("El nombre es obligatorio")))
        onView(withId(R.id.tv_error_cover)).check(matches(withText("La URL de la portada es obligatoria")))
        onView(withId(R.id.tv_error_description)).check(matches(withText("La descripción es obligatoria")))
    }

    @Test
    fun testSuccessfulAlbumCreation() {
        val albumName = faker.commerce().productName()
        
        onView(withId(R.id.et_album_name)).perform(replaceText(albumName), closeSoftKeyboard())
        onView(withId(R.id.et_cover_url)).perform(replaceText("https://picsum.photos/200"), closeSoftKeyboard())
        onView(withId(R.id.et_day)).perform(replaceText("15"), closeSoftKeyboard())
        
        onView(withId(R.id.spinner_month)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Mayo"))).perform(click())
        
        onView(withId(R.id.et_year)).perform(replaceText("2023"), closeSoftKeyboard())
        onView(withId(R.id.et_description)).perform(scrollTo(), replaceText(faker.lorem().paragraph()), closeSoftKeyboard())

        onView(withId(R.id.btn_create)).perform(click())

        // Check for searchBar to confirm we are back on the list screen
        onView(withId(R.id.searchBar)).check(matches(isDisplayed()))
    }

    @Test
    fun testCancelButton() {
        onView(withId(R.id.btn_cancel)).perform(click())
        // Check for searchBar to confirm we are back on the list screen
        onView(withId(R.id.searchBar)).check(matches(isDisplayed()))
    }
}
