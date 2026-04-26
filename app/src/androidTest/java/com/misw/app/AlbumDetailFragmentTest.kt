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
import com.misw.app.network.EspressoIdlingResource
import com.misw.app.ui.MainActivity
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

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
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
                withText(startsWith("Released "))
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
        
        onView(withText("Tracks")).perform(betterScrollTo()).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

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
}
