package com.cozo.cozomvp

class MyResponse {
    private lateinit var data : Data
    private var valid : Boolean = true

    fun isValid() : Boolean  = valid
    fun setIsValid(boolean: Boolean) {
        valid = boolean
    }
    fun setData(value: Data){
        data = value
    }
    fun getData() : Data = data
}

interface Service {
    fun doAction(request: String, callback: Callback?)
}

interface Callback {
    fun ack()
    fun reply(response: MyResponse)
}

data class Data(var message: String)

class ActionHandler(private var service: Service){

    fun doAction() {
        service.doAction("our-request", object : Callback {
            override fun ack() {
            }
            override fun reply(response: MyResponse) {
                handleResponse(response)
            }
        })
    }

    private fun handleResponse(response: MyResponse) {
        if (response.isValid()) {
            response.setData(Data("Successful data response"))
        }
    }

}