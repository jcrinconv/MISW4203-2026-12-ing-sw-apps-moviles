package com.misw.app

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.misw.app.ui.HelloFragment
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HelloFragmentTest {

    @Test
    fun fragmentLaunches_andDisplaysGreeting() {
        launchFragmentInContainer<HelloFragment>(themeResId = R.style.Theme_MISW4203)
        Thread.sleep(2000)
        onView(withId(R.id.tv_greeting))
            .check(matches(withText("Hola Mundo")))
    }
}
