package com.misw.app

import android.graphics.Rect
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
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.coordinatorlayout.widget.CoordinatorLayout
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

    private lateinit var fakerGeneratedAlbumName: String
    private lateinit var fakerGeneratedAlbumDescription: String
    private lateinit var fakerGeneratedTrackName: String

    @Before
    fun setup() {
        fakerGeneratedAlbumName = "Album " + faker.lorem().characters(20)
        // Descripción muy larga para forzar el "Ver más"
        fakerGeneratedAlbumDescription = faker.lorem().fixedString(800)
        fakerGeneratedTrackName = faker.music().instrument()

        // Limpiar cache antes de cada test para asegurar independencia
        CacheManager.getInstance().clearCache()

        val dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path ?: return MockResponse().setResponseCode(404)
                return when {
                    path == "/albums" -> {
                        val albumsJson = """
                            [
                                {"id":1, "name":"$fakerGeneratedAlbumName", "cover":"https://picsum.photos/200", "releaseDate":"1976-12-10T00:00:00.000Z", "description":"D1", "genre":"Rock", "recordLabel":"EMI"},
                                {"id":98, "name":"Empty Album", "cover":"https://picsum.photos/200", "releaseDate":"2023-01-01T00:00:00.000Z", "description":"", "genre":"Pop", "recordLabel":"Indie"},
                                {"id":404, "name":"Error Album", "cover":"https://picsum.photos/200", "releaseDate":"2023-01-01T00:00:00.000Z", "description":"", "genre":"Pop", "recordLabel":"Indie"}
                            ]
                        """.trimIndent()
                        MockResponse().setResponseCode(200).setBody(albumsJson)
                    }
                    path == "/albums/1" -> {
                        val detailJson = """
                            {
                                "id":1, "name":"$fakerGeneratedAlbumName", "cover":"https://picsum.photos/200", 
                                "releaseDate":"1976-12-10T00:00:00.000Z", "description":"$fakerGeneratedAlbumDescription", "genre":"Rock", 
                                "recordLabel":"EMI",
                                "tracks": [{"id":1, "name":"$fakerGeneratedTrackName", "duration":"4:48"}]
                            }
                        """.trimIndent()
                        MockResponse().setResponseCode(200).setBody(detailJson)
                    }
                    path == "/albums/98" -> {
                        val detailJson = """
                            {
                                "id":98, "name":"Empty Album", "cover":"https://picsum.photos/200", 
                                "releaseDate":"2023-01-01T00:00:00.000Z", "description":"", "genre":"Pop", 
                                "recordLabel":"Indie", "tracks": []
                            }
                        """.trimIndent()
                        MockResponse().setResponseCode(200).setBody(detailJson)
                    }

                    path == "/albums/404" -> {
                        MockResponse().setResponseCode(404)
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

    /**
     * betterScrollTo uses Android's native requestRectangleOnScreen to guarantee the view
     * becomes visible, then falls back to manual scroll if still not displayed.
     */
    private fun betterScrollTo(): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = allOf(
                withEffectiveVisibility(Visibility.VISIBLE),
                isDescendantOfA(anyOf(isAssignableFrom(ScrollView::class.java), isAssignableFrom(NestedScrollView::class.java)))
            )
            override fun getDescription(): String = "better scroll to"
            override fun perform(uiController: UiController, view: View) {
                if (isDisplayingAtLeast(90).matches(view)) return

                // First attempt: use Android's native mechanism
                val rect = Rect(0, 0, view.width, view.height)
                view.requestRectangleOnScreen(rect, true)
                uiController.loopMainThreadForAtLeast(500)
                uiController.loopMainThreadUntilIdle()

                if (isDisplayingAtLeast(90).matches(view)) return

                // Fallback: manual scroll using absolute coordinates
                var scrollParent: View? = view.parent as? View
                while (scrollParent != null && scrollParent !is NestedScrollView && scrollParent !is ScrollView) {
                    scrollParent = scrollParent.parent as? View
                }

                if (scrollParent != null) {
                    val parentRect = Rect()
                    view.getDrawingRect(parentRect)
                    (scrollParent as ViewGroup).offsetDescendantRectToMyCoords(view, parentRect)
                    val offset = parentRect.top - 200

                    if (scrollParent is NestedScrollView) {
                        scrollParent.scrollTo(0, offset.coerceAtLeast(0))
                    } else if (scrollParent is ScrollView) {
                        scrollParent.scrollTo(0, offset.coerceAtLeast(0))
                    }
                }
                uiController.loopMainThreadForAtLeast(500)
                uiController.loopMainThreadUntilIdle()
            }
        }
    }

    @Test
    fun testFullAlbumDetailFlow() {
        // 1. Navegar al álbum generado por Faker
        onView(withId(R.id.include_albums)).perform(click())
        onView(withId(R.id.rvAlbumList)).perform(
            RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText(fakerGeneratedAlbumName)), click())
        )

        // 2. Verificar datos básicos
        onView(withId(R.id.tvAlbumName)).check(matches(withText(fakerGeneratedAlbumName)))
        onView(withId(R.id.tvGenre)).check(matches(isDisplayed()))

        // 3. Probar lógica de "Ver más" en descripción larga
        // Colapsar la cabecera para que el NestedScrollView ocupe toda la pantalla
        onView(isAssignableFrom(CoordinatorLayout::class.java)).perform(swipeUp())

        // Clic para expandir
        onView(withId(R.id.tvReadMore)).perform(betterScrollTo(), click())
        onView(withId(R.id.tvReadMore)).check(matches(withText("Ver menos")))

        // Esperamos a que Android termine de expandir el TextView y recalcule el layout
        Thread.sleep(1500)

        // Forzamos múltiples swipes para asegurar que el botón quede en pantalla
        onView(isAssignableFrom(CoordinatorLayout::class.java)).perform(swipeUp())
        onView(isAssignableFrom(CoordinatorLayout::class.java)).perform(swipeUp())
        Thread.sleep(1000) // Esperamos a que termine el scroll (fling)

        // Clic para colapsar
        onView(withId(R.id.tvReadMore)).perform(betterScrollTo(), click())
        onView(withId(R.id.tvReadMore)).check(matches(withText("Ver más")))

        // 4. Verificar tracklist renderizado dinámicamente
        onView(withText(fakerGeneratedTrackName))
            .perform(betterScrollTo())
            .check(matches(isDisplayed()))
    }

    @Test
    fun testEmptyAlbumDetail() {
        onView(withId(R.id.include_albums)).perform(click())
        onView(withId(R.id.rvAlbumList)).perform(
            RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText("Empty Album")), click())
        )

        onView(withId(R.id.tvDescription)).check(matches(withText("")))
        onView(withId(R.id.llTracksContainer)).check(matches(hasChildCount(0)))
    }

    @Test
    fun testAlbumDetailErrorCharge() {
        onView(withId(R.id.include_albums)).perform(click())
        onView(withId(R.id.rvAlbumList)).perform(
            RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText("Error Album")), click())
        )
        onView(withId(R.id.llEmptyState)).check(matches(isDisplayed()))
        onView(withId(R.id.tvEmptyState)).check(matches(withText("Error al cargar contenido. Por favor intenta de nuevo.")))
    }
}
