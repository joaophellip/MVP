package com.example

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.cozo.cozomvp.R
import com.cozo.cozomvp.authentication.AuthActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthenticationUITests {

    @Rule
    @JvmField
    val mActivity = ActivityTestRule<AuthActivity>(AuthActivity::class.java)

    @Test
    fun testEmptyLogIn(){
        onView(withId(R.id.authButton)).perform(click())
        onView(withId(R.id.subTitleText))
                .check(matches(withText("Invalid Number. Please try again")))
    }
}