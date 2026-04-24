package com.misw.app

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.misw.app.ui.MainActivity
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

    @Before
    fun navigateToAlbumsList() {
        // 1. Primero navegamos desde el Home hasta la pantalla de álbumes
        // Usamos el ID del include que definimos antes
        onView(withId(R.id.include_albums)).perform(click())
    }

    @Test
    fun checkSearchBar_isVisibleAndWorks() {
        // Verificamos que el buscador esté presente
        onView(withId(R.id.etSearchAlbum)).check(matches(isDisplayed()))
            .check(matches(withHint("Buscar álbum...")))

        // Simulamos que el usuario escribe un álbum (ej: "Electric Dreams")
        onView(withId(R.id.etSearchAlbum)).perform(typeText("Electric Dreams"), closeSoftKeyboard())

        // Verificamos que el texto quedó escrito
        onView(withId(R.id.etSearchAlbum)).check(matches(withText("Electric Dreams")))
    }

    @Test
    fun checkSortingButtons_areCorrect() {
        // Verificamos el botón de Nombre (A-Z)
        // Nota: Al tener textAllCaps="true", Espresso a veces requiere el texto en MAYÚSCULAS
        onView(withId(R.id.btnSortName)).check(matches(isDisplayed()))
            .check(matches(withText("NOMBRE (A-Z)")))

        // Verificamos el botón de Fecha
        onView(withId(R.id.btnSortDate)).check(matches(isDisplayed()))
            .check(matches(withText("Fecha")))
    }

    @Test
    fun checkTopAppBar_titleIsCorrect() {
        // Verificamos que el título de la barra haya cambiado a "Álbumes"
        // (Asegúrate de que este sea el texto que pusiste en el nav_graph o en la Activity)
        onView(withText("Álbumes")).check(matches(isDisplayed()))
    }
}