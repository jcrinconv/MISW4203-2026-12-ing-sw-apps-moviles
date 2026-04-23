package com.misw.app

import android.view.ViewGroup
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.misw.app.ui.MainActivity
import org.hamcrest.Matchers
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

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
}