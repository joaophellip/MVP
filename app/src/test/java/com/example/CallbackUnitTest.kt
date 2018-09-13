package com.example

import com.cozo.cozomvp.Callback
import com.cozo.cozomvp.Data
import com.cozo.cozomvp.MyResponse
import com.cozo.cozomvp.Service
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers.any
import org.mockito.Matchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.MockitoAnnotations
import com.cozo.cozomvp.ActionHandler
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CallbackUnitTest {

    @Mock
    lateinit var service : Service

    @Before
    fun setup(){
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun givenServiceWithValidResponse_whenCallbackReceived_thenAck() {

        doAnswer {
            val callback = it.arguments[1] as Callback
            callback.ack()
            return@doAnswer null
        }.`when`(service).doAction(anyString(), any(Callback::class.java))

    }

    @Test
    fun givenServiceWithValidResponse_whenCallbackReceived_thenProcessed() {

        val response = MyResponse()

        doAnswer {
            val callback = it.arguments[1] as Callback
            callback.reply(response)

            val expectedMessage = "Successful data response"
            val data : Data = response.getData()

            assertEquals(expectedMessage, data.message)

            return@doAnswer null
        }.`when`(service).doAction(anyString(), any(Callback::class.java))

        val handler = ActionHandler(service)
        handler.doAction()
    }
}
