package com.example

import android.content.res.Resources
import com.cozo.cozomvp.R
import org.robolectric.Robolectric
import org.robolectric.shadows.ShadowApplication
import java.io.File

open class TestBase {

    /**
     * Helper function which will load JSON from
     * the path specified
     *
     * @param path : Path of JSON file
     * @return json : JSON from file at given path
     */
    fun getJson(path : Int) : String {
        // Load the JSON response

        val inputStream = ShadowApplication.getInstance().applicationContext.resources.openRawResource(path)
        return inputStream.bufferedReader().use { it.readText() }
    }
}