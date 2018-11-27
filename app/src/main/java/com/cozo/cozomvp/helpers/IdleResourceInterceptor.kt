package com.cozo.cozomvp.helpers

import okhttp3.Interceptor
import android.support.test.espresso.IdlingResource
import android.util.Log
import okhttp3.Response
import java.util.*

class IdleResourceInterceptor : Interceptor, IdlingResource, Runnable {

    private val stack : Stack<Int> = Stack()
    private val outerCallStack : Stack<Int> = Stack()

    @Volatile
    var callback: IdlingResource.ResourceCallback? = null

    override fun getName(): String {
        return "idleResourceInterceptor"
    }

    fun stackCall(tag: String) {
        Log.d("IdleDebug","outer call started on $tag")
        outerCallStack.push(1)
    }

    fun popCall(tag: String) {
        Log.d("IdleDebug","outer call ended on $tag")
        if (!outerCallStack.isEmpty()){
            outerCallStack.pop()
        }
        if(outerCallStack.isEmpty() && this.callback != null){
            this.callback!!.onTransitionToIdle()
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        stack.push(1)
        Log.d("IdleDebug","stacking ... ${chain.request().url().encodedPath()}")
        val res = chain.proceed(chain.request())
        stack.pop()
        if(stack.isEmpty() && this.callback != null) {
            this.callback!!.onTransitionToIdle()
        }
        return res
    }

    override fun isIdleNow(): Boolean {
        Log.d("IdleDebug","is Idle? ${stack.isEmpty()} and ${outerCallStack.isEmpty()}")
        return stack.isEmpty() && outerCallStack.isEmpty()
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
    }

    override fun run() {}

    companion object {

        private var aInstance = IdleResourceInterceptor()

        fun getInstance() : IdleResourceInterceptor {
            return aInstance
        }
    }
}