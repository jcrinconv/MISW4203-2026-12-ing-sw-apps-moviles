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
import com.misw.app.network.EspressoIdlingResource
import com.misw.app.ui.MainActivity
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

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        onView(withId(R.id.include_albums)).perform(click())
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testVisibilityOfAllComponents() {
        onView(withId(R.id.etSearchAlbum)).check(matches(isDisplayed()))

        onView(withId(R.id.btnSortName)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSortDate)).check(matches(isDisplayed()))

        onView(withId(R.id.btnSwapOrder)).check(matches(isDisplayed()))

        onView(withId(R.id.rvAlbumList)).check(matches(isDisplayed()))
    }

    @Test
    fun testSearchFiltering() {
        val albumToSearch = "Buscando América"

        onView(withId(R.id.etSearchAlbum))
            .perform(replaceText(albumToSearch), closeSoftKeyboard())

        onView(withId(R.id.rvAlbumList))
            .check(matches(atPosition(0, hasDescendant(withText(containsString(albumToSearch))))))
    }

    @Test
    fun testSortingButtonsInteraction() {
        onView(withId(R.id.btnSortDate)).perform(click())
        onView(withId(R.id.btnSortDate)).check(matches(isDisplayed()))

        onView(withId(R.id.btnSortName)).perform(click())
        onView(withId(R.id.btnSortName)).check(matches(isDisplayed()))
    }

    @Test
    fun testSwapOrderButton() {
        onView(withId(R.id.btnSwapOrder)).perform(click())

        onView(withId(R.id.etSearchAlbum)).check(matches(isDisplayed()))
    }

    @Test
    fun testRecyclerViewContent() {
        // En lugar de posiciones fijas que dependen del orden de la API, 
        // validamos que los álbumes esperados existan en la lista haciendo scroll hacia ellos.
        
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
