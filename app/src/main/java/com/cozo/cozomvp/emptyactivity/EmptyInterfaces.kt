package com.cozo.cozomvp.emptyactivity

interface EmptyInterfaces {
    interface Presenter{
        fun onCreateInvoked()
    }
    interface ModelListener{
        fun userAvailable()
        fun noUserAvailable()
    }
}