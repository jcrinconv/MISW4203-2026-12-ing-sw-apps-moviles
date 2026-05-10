package com.misw.app

import android.view.ViewGroup
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
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
import org.hamcrest.Matchers
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
        // Generar un nombre aleatorio extremadamente largo con Faker
        fakerGeneratedAlbumName = faker.lorem().characters(100)

        // Configurar Dispatcher para manejar la navegación desde Home a otras pantallas
        val dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path ?: return MockResponse().setResponseCode(404)

                return when (path) {
                    "/albums" -> {
                        // Retornamos un Happy Path y un caso Faker para garantizar que la vista carga
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

        // Iniciar MockWebServer
        mockWebServer.start(0)
        RetrofitClient.setBaseUrl(mockWebServer.url("/").toString())

        // Registrar IdlingResource para Espresso
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
        val viewGroup = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.topAppBar), ViewMatchers.withParent(
                    Matchers.allOf(
                        ViewMatchers.withId(R.id.appBarLayout),
                        ViewMatchers.withParent(
                            IsInstanceOf.instanceOf(ViewGroup::class.java)
                        )
                    )
                ), ViewMatchers.isDisplayed()
            )
        )
        viewGroup.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun checkHomeAlbumsButtonTest() {
        // 1. Verificar que el contenedor principal de Álbumes sea visible
        val frameLayout = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.include_albums), ViewMatchers.isDisplayed()
            )
        )
        frameLayout.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // 2. VERIFICAR EL NOMBRE: Buscamos el TextView con el texto "Álbumes"
        // dentro de ese contenedor específico
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.tv_menu_title), // El ID del TextView en el item_menu_card
                ViewMatchers.isDescendantOfA(
                    ViewMatchers.withId(R.id.include_albums)
                ),
                ViewMatchers.isDisplayed()
            )
        ).check(ViewAssertions.matches(ViewMatchers.withText("Álbumes")))
    }

    @Test
    fun checkHomeArtistsButtonTest() {
        // 1. Verificar que el contenedor principal de Artistas sea visible
        val frameLayout = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.include_artists), ViewMatchers.isDisplayed()
            )
        )
        frameLayout.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // 2. VERIFICAR EL NOMBRE: Buscamos el TextView con el texto "Artistas"
        // dentro de ese contenedor específico
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.tv_menu_title), // El ID del TextView en el item_menu_card
                ViewMatchers.isDescendantOfA(
                    ViewMatchers.withId(R.id.include_artists)
                ), ViewMatchers.isDisplayed()
            )
        ).check(ViewAssertions.matches(ViewMatchers.withText("Artistas")))
    }

    @Test
    fun checkHomeTracksButtonTest() {
        // 1. Verificar que el contenedor principal de Tracks sea visible
        val frameLayout = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.include_tracks), ViewMatchers.isDisplayed()
            )
        )
        frameLayout.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // 2. VERIFICAR EL NOMBRE: Buscamos el TextView con el texto "Tracks"
        // dentro de ese contenedor específico
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.tv_menu_title), // El ID del TextView en el item_menu_card
                ViewMatchers.isDescendantOfA(
                    ViewMatchers.withId(R.id.include_tracks)
                ), ViewMatchers.isDisplayed()
            )
        ).check(ViewAssertions.matches(ViewMatchers.withText("Tracks")))
    }

    @Test
    fun checkHomeCollectorsButtonTest() {
        // 1. Verificar que el contenedor principal de Coleccionistas sea visible
        val frameLayout = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.include_collectors), ViewMatchers.isDisplayed()
            )
        )
        frameLayout.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // 2. VERIFICAR EL NOMBRE: Buscamos el TextView con el texto "Coleccionistas"
        // dentro de ese contenedor específico
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.tv_menu_title), // El ID del TextView en el item_menu_card
                ViewMatchers.isDescendantOfA(
                    ViewMatchers.withId(R.id.include_collectors)
                ), ViewMatchers.isDisplayed()
            )
        ).check(ViewAssertions.matches(ViewMatchers.withText("Coleccionistas")))
    }

    @Test
    fun checkNavigationToAlbumsWithFakerData() {
        // Hacemos click en el botón de Álbumes
        Espresso.onView(ViewMatchers.withId(R.id.include_albums))
            .perform(androidx.test.espresso.action.ViewActions.click())

        // Verificamos que la lista de álbumes se muestra
        Espresso.onView(ViewMatchers.withId(R.id.rvAlbumList))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Verificamos que al menos el ítem con el nombre super largo generado por Faker (Boundary)
        // se haya renderizado correctamente sin colapsar la vista
        Espresso.onView(ViewMatchers.withId(R.id.rvAlbumList))
            .perform(androidx.test.espresso.contrib.RecyclerViewActions.scrollTo<androidx.recyclerview.widget.RecyclerView.ViewHolder>(
                ViewMatchers.hasDescendant(ViewMatchers.withText(fakerGeneratedAlbumName))
            ))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(fakerGeneratedAlbumName))))
    }
}