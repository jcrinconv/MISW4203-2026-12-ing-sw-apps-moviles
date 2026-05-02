package com.misw.app

import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
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
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AlbumDetailFragmentTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    private val mockWebServer = MockWebServer()
    private val faker = Faker()

    // Variable global para almacenar el nombre largo generado y poder asertarlo en el test
    private lateinit var fakerGeneratedAlbumName: String
    private lateinit var fakerGeneratedAlbumDescription: String

    @Before
    fun setup() {
        // Generar datos aleatorios extremos (border case) para el álbum de id 99
        fakerGeneratedAlbumName = faker.lorem().characters(100) // Nombre muy largo para probar UI
        fakerGeneratedAlbumDescription = faker.lorem().paragraph(10) // Descripción muy larga

        // Configurar el Dispatcher para manejar múltiples rutas
        val dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path ?: return MockResponse().setResponseCode(404)

                return when {
                    path == "/albums" -> {
                        // Devuelve el Happy Path + un item para el border case (id: 99)
                        val albumsJson = """
                            [
                                {"id":1, "name":"A Day at the Races", "cover":"https://picsum.photos/200", "releaseDate":"1976-12-10T00:00:00.000Z", "description":"D1", "genre":"Rock", "recordLabel":"EMI"},
                                {"id":99, "name":"$fakerGeneratedAlbumName", "cover":"https://picsum.photos/200", "releaseDate":"2023-01-01T00:00:00.000Z", "description":"D99", "genre":"Pop", "recordLabel":"Sony"}
                            ]
                        """.trimIndent()
                        MockResponse().setResponseCode(200).setBody(albumsJson)
                    }
                    path == "/albums/1" -> {
                        // Happy path detail
                        val albumDetailJson = """
                            {
                                "id":1, "name":"A Day at the Races", "cover":"https://picsum.photos/200", 
                                "releaseDate":"1976-12-10T00:00:00.000Z", "description":"D1", "genre":"Rock", 
                                "recordLabel":"EMI",
                                "tracks": [
                                    {"id":1, "name":"Tie Your Mother Down", "duration":"4:48"}
                                ],
                                "performers": [],
                                "comments": []
                            }
                        """.trimIndent()
                        MockResponse().setResponseCode(200).setBody(albumDetailJson)
                    }
                    path == "/albums/99" -> {
                        // Border case detail usando Faker
                        val albumDetailJson = """
                            {
                                "id":99, "name":"$fakerGeneratedAlbumName", "cover":"https://picsum.photos/200", 
                                "releaseDate":"2023-01-01T00:00:00.000Z", "description":"$fakerGeneratedAlbumDescription", "genre":"Pop", 
                                "recordLabel":"Sony",
                                "tracks": [],
                                "performers": [],
                                "comments": []
                            }
                        """.trimIndent()
                        MockResponse().setResponseCode(200).setBody(albumDetailJson)
                    }
                    else -> MockResponse().setResponseCode(404)
                }
            }
        }
        mockWebServer.dispatcher = dispatcher

        // 1. Iniciar servidor de mock
        mockWebServer.start(0)
        RetrofitClient.setBaseUrl(mockWebServer.url("/").toString())

        // 2. Registrar sincronización
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        RetrofitClient.setBaseUrl(BuildConfig.BASE_URL)
    }

    /**
     * betterScrollTo es una alternativa robusta a la acción scrollTo() estándar de Espresso.
     *
     * ¿Por qué es necesaria?
     * 1. La acción estándar scrollTo() solo funciona con ScrollView tradicional y falla con NestedScrollView.
     * 2. Esta implementación detecta dinámicamente si el padre es ScrollView o NestedScrollView.
     * 3. Realiza el desplazamiento programático directamente sobre el contenedor padre.
     * 4. Utiliza loopMainThreadUntilIdle() para asegurar que la animación de scroll termine antes
     *    de realizar la siguiente validación.
     */
    private fun betterScrollTo(): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return allOf(
                    withEffectiveVisibility(Visibility.VISIBLE),
                    isDescendantOfA(anyOf(isAssignableFrom(ScrollView::class.java), isAssignableFrom(NestedScrollView::class.java)))
                )
            }

            override fun getDescription(): String = "scroll to view"

            override fun perform(uiController: UiController, view: View) {
                if (isDisplayingAtLeast(90).matches(view)) {
                    return
                }
                var parent = view.parent
                while (parent != null && parent !is NestedScrollView && parent !is ScrollView) {
                    parent = parent.parent
                }

                when (parent) {
                    is NestedScrollView -> {
                        parent.scrollTo(0, view.top)
                    }
                    is ScrollView -> {
                        parent.scrollTo(0, view.top)
                    }
                }
                uiController.loopMainThreadForAtLeast(500)
                uiController.loopMainThreadUntilIdle()
            }
        }
    }

    private fun navigateToFirstAlbum() {
        onView(withId(R.id.include_albums)).perform(click())
        onView(withId(R.id.rvAlbumList))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
    }

    @Test
    fun checkAlbumCoverTest() {
        navigateToFirstAlbum()
        onView(withId(R.id.ivAlbumCover)).check(matches(isDisplayed()))
    }

    @Test
    fun checkAlbumNameTest() {
        navigateToFirstAlbum()
        onView(withId(R.id.tvAlbumName)).check(matches(allOf(isDisplayed(), not(withText("")))))
    }

    @Test
    fun checkReleaseDateTest() {
        navigateToFirstAlbum()
        onView(withId(R.id.tvReleaseDate)).check(matches(allOf(
            isDisplayed(),
            withText(startsWith("Lanzado en "))
        )))
    }

    @Test
    fun checkRecordLabelTest() {
        navigateToFirstAlbum()
        onView(withId(R.id.tvRecordLabel)).perform(betterScrollTo()).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.tvRecordLabel)).check(matches(not(withText(""))))
    }

    @Test
    fun checkGenreTest() {
        navigateToFirstAlbum()
        onView(withId(R.id.tvGenre)).perform(betterScrollTo()).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun checkDescriptionTest() {
        navigateToFirstAlbum()
        onView(withId(R.id.tvDescription)).perform(betterScrollTo()).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.tvDescription)).check(matches(not(withText(""))))
    }

    @Test
    fun checkTracklistTest() {
        navigateToFirstAlbum()

        onView(withText(R.string.tracklist)).perform(betterScrollTo()).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withId(R.id.llTracksContainer)).perform(betterScrollTo()).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withId(R.id.llTracksContainer)).check { view, noViewFoundException ->
            noViewFoundException?.let { throw it }
            val container = view as ViewGroup

            if (container.childCount > 0) {
                assert(view.visibility == View.VISIBLE)

                for (i in 0 until container.childCount) {
                    val trackRow = container.getChildAt(i) as ViewGroup

                    val tvNumber = trackRow.findViewById<View>(R.id.tvTrackNumber)
                    val tvName = trackRow.findViewById<View>(R.id.tvTrackName)
                    val tvDuration = trackRow.findViewById<View>(R.id.tvTrackDuration)

                    assert(tvNumber != null && tvNumber.visibility == View.VISIBLE)
                    assert(tvName != null && tvName.visibility == View.VISIBLE)
                    assert(tvDuration != null && tvDuration.visibility == View.VISIBLE)
                }
            }
        }
    }

    @Test
    fun checkBorderCaseAlbumTest() {
        // Navegar a la lista
        onView(withId(R.id.include_albums)).perform(click())
        
        // Hacer scroll hasta el item con nombre generado (id 99) y hacer click
        onView(withId(R.id.rvAlbumList))
            .perform(RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                hasDescendant(withText(fakerGeneratedAlbumName)), click()
            ))

        // Validar que el nombre dinámico y largo carga correctamente
        onView(withId(R.id.tvAlbumName)).check(matches(withText(fakerGeneratedAlbumName)))

        // Hacer scroll a la descripción y validar que carga el texto dinámico y muy largo
        onView(withId(R.id.tvDescription)).perform(betterScrollTo())
            .check(matches(withText(fakerGeneratedAlbumDescription)))
    }
}