package com.misw.app

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.misw.app.ui.MainActivity
import org.hamcrest.Matchers.allOf
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
        onView(withId(R.id.include_albums)).perform(click())
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
        Thread.sleep(2000)
        val albumToSearch = "Buscando América"

        onView(withId(R.id.etSearchAlbum))
            .perform(replaceText(albumToSearch), closeSoftKeyboard())

        onView(withId(R.id.rvAlbumList))
            .check(matches(atPosition(0, hasDescendant(withText(albumToSearch)))))
    }

    @Test
    fun testSortingButtonsInteraction() {
        Thread.sleep(1500)
        onView(withId(R.id.btnSortDate)).perform(click())
        onView(withId(R.id.btnSortDate)).check(matches(isDisplayed()))

        onView(withId(R.id.btnSortName)).perform(click())
        onView(withId(R.id.btnSortName)).check(matches(isDisplayed()))
    }

    @Test
    fun testSwapOrderButton() {
        Thread.sleep(1500)
        onView(withId(R.id.btnSwapOrder)).perform(click())

        onView(withId(R.id.etSearchAlbum)).check(matches(isDisplayed()))
    }

    @Test
    fun testRecyclerViewContent() {
        Thread.sleep(1500)

        onView(withId(R.id.rvAlbumList))
            .check(matches(atPosition(0, hasDescendant(withText("A Day at the Races")))))

        onView(withId(R.id.rvAlbumList))
            .check(matches(atPosition(1, hasDescendant(withText("A Night at the Opera")))))

        onView(withId(R.id.rvAlbumList))
            .check(matches(atPosition(2, hasDescendant(withText("Buscando América")))))
    }
}

fun atPosition(position: Int, itemMatcher: org.hamcrest.Matcher<View>): org.hamcrest.Matcher<View> {
    return object :
        androidx.test.espresso.matcher.BoundedMatcher<View, androidx.recyclerview.widget.RecyclerView>(
            androidx.recyclerview.widget.RecyclerView::class.java
        ) {
        override fun describeTo(description: org.hamcrest.Description) {
            description.appendText("has item at position $position: ")
            itemMatcher.describeTo(description)
        }

        override fun matchesSafely(view: androidx.recyclerview.widget.RecyclerView): Boolean {
            val viewHolder = view.findViewHolderForAdapterPosition(position) ?: return false
            return itemMatcher.matches(viewHolder.itemView)
        }
    }
}