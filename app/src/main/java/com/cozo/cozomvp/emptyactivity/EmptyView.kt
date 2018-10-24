package com.cozo.cozomvp.emptyactivity

import com.hannesdorfmann.mosby3.mvp.MvpView

interface EmptyView : MvpView {
    fun startAuthActivity()
    fun startMainActivity()
}