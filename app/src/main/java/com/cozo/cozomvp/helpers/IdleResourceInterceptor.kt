package com.cozo.cozomvp.helpers

import okhttp3.Interceptor
import android.support.test.espresso.IdlingResource
import android.util.Log
import okhttp3.Response
import java.util.*

class IdleResourceInterceptor : Interceptor, IdlingResource, Runnable {

    private val stack : Stack<Int> = Stack()
    private val outerCallStack : Stack<Int> = Stack()

    //var outerCall: Boolean = false

    @Volatile
    var callback: IdlingResource.ResourceCallback? = null

    override fun getName(): String {
        return "idleResourceInterceptor"
    }

    fun stackCall(tag: String) {
        Log.d("DeuRuim","outer call started on $tag")
        //outerCall = true;
        outerCallStack.push(1)
    }

    fun popCall(tag: String) {
        Log.d("DeuRuim","outer call ended on $tag")
        //outerCall = false
        outerCallStack.pop()    // treat EmptyStackException
        if(outerCallStack.isEmpty() && this.callback != null){
            this.callback!!.onTransitionToIdle()
        }
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
        //Log.d("DeuRuim","is Idle? ${stack.isEmpty() && !outerCall}")
        //return stack.isEmpty() && !outerCall
        Log.d("DeuRuim","is Idle? ${stack.isEmpty()} and ${outerCallStack.isEmpty()}")
        return stack.isEmpty() && outerCallStack.isEmpty()
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
            return aInstance
        }
    }
}