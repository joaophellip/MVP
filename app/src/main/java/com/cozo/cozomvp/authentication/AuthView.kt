package com.cozo.cozomvp.authentication

import com.hannesdorfmann.mosby3.mvp.MvpView

interface AuthView : MvpView {

    fun showLoading()
    fun showError()
    fun showInvalidNumber()
    fun showAuthenticationFail()
    fun showLinkFail()

    fun showMainActivity()
    fun showLogOffActivity()
    fun startLinkWithGoogle()

}
