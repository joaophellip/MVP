package com.cozo.cozomvp

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

    fun showAuthAndLinkedCompletedActivity()
}
