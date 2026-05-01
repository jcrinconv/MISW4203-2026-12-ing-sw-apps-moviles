package com.misw.app

import android.view.View
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
import org.hamcrest.CoreMatchers.containsString
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
        // 1. Iniciar servidor de mock en puerto aleatorio
        mockWebServer.start(0)
        RetrofitClient.setBaseUrl(mockWebServer.url("/").toString())

        // 2. Registrar sincronización
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

        // 3. Encolar respuesta inicial para que la lista cargue al navegar
        val initialAlbumsJson = """
            [
                {"id":1, "name":"A Day at the Races", "cover":"https://picsum.photos/200", "releaseDate":"1976-12-10T00:00:00.000Z", "description":"D1", "genre":"Rock", "recordLabel":"EMI"},
                {"id":2, "name":"A Night at the Opera", "cover":"https://picsum.photos/200", "releaseDate":"1975-11-21T00:00:00.000Z", "description":"D2", "genre":"Rock", "recordLabel":"EMI"},
                {"id":3, "name":"Buscando América", "cover":"https://picsum.photos/200", "releaseDate":"1984-04-01T00:00:00.000Z", "description":"D3", "genre":"Salsa", "recordLabel":"Elektra"}
            ]
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(initialAlbumsJson))

        // 4. Navegar a la sección de álbumes
        onView(withId(R.id.include_albums)).perform(click())
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        // Restaurar URL original (opcional si se usa BuildConfig en producción)
        RetrofitClient.setBaseUrl(BuildConfig.BASE_URL)
    }

    @Test
    fun testVisibilityOfAllComponents() {
        onView(withId(R.id.etSearchAlbum)).check(matches(isDisplayed()))
        onView(withId(R.id.rvAlbumList)).check(matches(isDisplayed()))
    }

    @Test
    fun testSearchFiltering() {
        val albumToSearch = "Buscando América"

        onView(withId(R.id.etSearchAlbum))
            .perform(replaceText(albumToSearch), closeSoftKeyboard())

        onView(withId(R.id.rvAlbumList))
            .check(matches(hasDescendant(withText(containsString(albumToSearch)))))
    }

    @Test
    fun testSearchNoResults() {
        // Escenario negativo usando un valor generado por Faker que no existe en nuestro JSON inicial
        val nonExistentAlbum = "Fake-" + faker.lorem().characters(10)

        onView(withId(R.id.etSearchAlbum))
            .perform(replaceText(nonExistentAlbum), closeSoftKeyboard())

        onView(withId(R.id.rvAlbumList))
            .check(matches(hasChildCount(0)))
    }

    @Test
    fun testSearchAndClearRestoresList() {
        onView(withId(R.id.etSearchAlbum))
            .perform(replaceText("Buscando"), closeSoftKeyboard())

        onView(withId(R.id.etSearchAlbum))
            .perform(replaceText(""), closeSoftKeyboard())

        onView(withId(R.id.rvAlbumList))
            .check(matches(hasMinimumChildCount(1)))
    }

    @Test
    fun testRecyclerViewContent() {
        // Verificamos que los datos mockeados en el setup() están presentes
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

/**
 * Matcher auxiliar para validar posiciones específicas si fuera necesario
 */
fun atPosition(position: Int, itemMatcher: org.hamcrest.Matcher<View>): org.hamcrest.Matcher<View> {
    return object :
        androidx.test.espresso.matcher.BoundedMatcher<View, RecyclerView>(
            RecyclerView::class.java
        ) {
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
