package com.cozo.cozomvp.helpers

import okhttp3.Interceptor
import android.support.test.espresso.IdlingResource;
import android.util.Log
import okhttp3.Response
import java.util.*

class IdleResourceInterceptor : Interceptor, IdlingResource, Runnable {

    val stack : Stack<Int> = Stack()
    var outerCall: Boolean = false

    @Volatile
    var callback: IdlingResource.ResourceCallback? = null

    override fun getName(): String {
        return "idleResourceInterceptor"
    }

    fun stackCall() {
        Log.d("DeuRuim","outer call started")
        outerCall = true;
    }

    fun popCall() {
        Log.d("DeuRuim","outer call ended")
        outerCall = false
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        stack.push(1)
        Log.d("DeuRuim","stacking... ${chain.request().url().encodedPath()}")
        val res = chain.proceed(chain.request())
        stack.pop()
        if(stack.isEmpty() && this.callback != null) {
            this.callback!!.onTransitionToIdle()
        }
        return res
    }

    override fun isIdleNow(): Boolean {
        Log.d("DeuRuim","is Idle? ${stack.isEmpty() && !outerCall}")
        return stack.isEmpty() && !outerCall
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
    }

    override fun run() {
//        stack.clear()
//        Log.d("DeuRuim","called run")
//        if(this.callback != null && !outerCall) {
//            Log.d("DeuRuim","idling....")
//            this.callback!!.onTransitionToIdle()
//        }
    }

    companion object {

        private var aInstance = IdleResourceInterceptor()

        fun getInstance() : IdleResourceInterceptor {
            return aInstance;
        }
    }
}