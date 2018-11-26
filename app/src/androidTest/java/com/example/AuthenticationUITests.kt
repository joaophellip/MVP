package com.example

import android.support.test.espresso.Espresso
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.cozo.cozomvp.emptyactivity.EmptyActivity
import com.cozo.cozomvp.helpers.IdleResourceInterceptor
import com.cozo.cozomvp.mainactivity.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthenticationUITests {

    @Rule
    @JvmField
    val mActivity = ActivityTestRule<EmptyActivity>(EmptyActivity::class.java)

    @Before
    fun setup(){
        Intents.init()
        IdlingRegistry.getInstance().register(IdleResourceInterceptor.getInstance())
    }

    @Test
    fun launchMainActivity(){
        //Thread.sleep(4000)
        intended(hasComponent(MainActivity::class.java.name))
    }

    //https://stackoverflow.com/questions/30733718/how-to-use-espresso-idling-resource-for-network-calls

    /*@Test
    fun testEmptyLogIn(){
        onView(withId(R.id.authButton)).perform(click())
        onView(withId(R.id.subTitleText))
                .check(matches(withText("Invalid Number. Please try again")))
    }*/

    /*@Test
    fun testFakeUserLogin(){
        val number = "59911111111"
        val verif_code = "123456"
        onView(withId(R.id.phoneNumberEditText)).perform(typeText(number), closeSoftKeyboard())
        onView(withId(R.id.authButton)).perform(click())
        onView(withId(R.id.progressBar))
                .check(matches(isDisplayed()))
    }*/
}