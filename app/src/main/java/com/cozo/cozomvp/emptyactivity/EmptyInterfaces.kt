package com.cozo.cozomvp.emptyactivity

interface EmptyInterfaces {
    interface Presenter{
        fun onCreatedInvoked()

    }
    interface Model{
        fun onTokenRetrieveCompleted()
        fun onTokenRetrieveFailed()
    }
}