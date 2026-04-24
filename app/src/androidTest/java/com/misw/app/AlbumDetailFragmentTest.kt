package com.misw.app

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.*
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.Test
import com.misw.app.ui.MainActivity

@LargeTest
@RunWith(AndroidJUnit4::class)
class AlbumDetailFragmentTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun checkReleaseDateTest() {
        // 1. Ir a álbumes
        Espresso.onView(withId(R.id.include_albums)).perform(click())

        // 2. Seleccionar el primer álbum
        Espresso.onView(withId(R.id.rv_albums))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        // 3. Verificar fecha de lanzamiento
        Espresso.onView(
            allOf(
                withId(R.id.tv_release_date),
                isDisplayed()
            )
        ).check(matches(withText(containsString("Lanzado en \\w+ \\d+, \\d{4}"))))
    }

    @Test
    fun checkRecordLabelTest() {
        // 1. Ir a álbumes
        Espresso.onView(withId(R.id.include_albums)).perform(click())

        // 2. Seleccionar el primer álbum
        Espresso.onView(withId(R.id.rv_albums))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        // 3. Verificar sello discográfico
        Espresso.onView(
            allOf(
                withId(R.id.tv_record_label),
                isDisplayed()
            )
        ).check(matches(not(withText(containsString("")))))
    }

    @Test
    fun checkGenreTest() {
        // 1. Ir a álbumes
        Espresso.onView(withId(R.id.include_albums)).perform(click())

        // 2. Seleccionar el primer álbum
        Espresso.onView(withId(R.id.rv_albums))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        // 3. Verificar género
        Espresso.onView(
            allOf(
                withId(R.id.tv_genre),
                isDisplayed()
            )
        ).check(matches(not(withText(containsString("")))))
    }

    @Test
    fun checkDescriptionTest() {
        // 1. Ir a álbumes
        Espresso.onView(withId(R.id.include_albums)).perform(click())

        // 2. Seleccionar el primer álbum
        Espresso.onView(withId(R.id.rv_albums))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        // 3. Verificar descripción
        Espresso.onView(
            allOf(
                withId(R.id.tv_description),
                isDisplayed()
            )
        ).check(matches(not(withText(containsString("")))))
    }

    @Test
    fun checkTracklistTest() {
        // 1. Ir a álbumes
        Espresso.onView(withId(R.id.include_albums)).perform(click())

        // 2. Seleccionar el primer álbum
        Espresso.onView(withId(R.id.rv_albums))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        // 3. Verificar título
        Espresso.onView(
            allOf(
                withText(R.string.tracklist),
                isDisplayed()
            )
        ).check(matches(isDisplayed()))

        // 4. Verificar contador
        Espresso.onView(
            allOf(
                withId(R.id.tv_track_count),
                isDisplayed()
            )
        ).check(matches(not(withText(""))))

        // 5. Verificar que se muestre texto si cantidad = 0, y se muestren tracks si > 0
        Espresso.onView(withId(R.id.rv_tracks))
            .check { view, noViewFoundException ->
                noViewFoundException?.let{ throw it }
                val reclyclerView = view as RecyclerView
                val itemCount = reclyclerView.adapter?.itemCount ?: 0
                if (itemCount > 0) {
                    assert(view.visibility == View.VISIBLE)
                } else {
                    assert(view.visibility == View.GONE)
                    Espresso.onView(withId(R.id.tv_no_tracks))
                        .check(matches(isDisplayed()))
                }
            }
    }
}