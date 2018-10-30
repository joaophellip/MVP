package com.cozo.cozomvp.networkapi

import android.app.Application
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

class DelivererApplication : Application() {

    var mSocket: Socket? = null
        private set

    init {
        try {
            mSocket = IO.socket("http://nearby.cozo.com.br")
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }

    }
}