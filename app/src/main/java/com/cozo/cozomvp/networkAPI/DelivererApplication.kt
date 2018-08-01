package com.cozo.cozomvp.networkAPI

import android.app.Application
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

class DelivererApplication : Application() {

    var mSocket: Socket? = null
        private set

    init {
        try {
            mSocket = IO.socket("http://35.192.108.177:80")
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }

    }
}