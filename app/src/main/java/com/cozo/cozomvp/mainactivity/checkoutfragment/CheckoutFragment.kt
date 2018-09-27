package com.cozo.cozomvp.mainactivity.checkoutfragment

import com.hannesdorfmann.mosby3.mvp.MvpFragment

class CheckoutFragment : MvpFragment<CheckoutView, CheckoutPresenter>(){

    override fun createPresenter(): CheckoutPresenter = CheckoutPresenter()

    companion object {
        val TAG : String = CheckoutFragment::class.java.simpleName

        fun newInstance(): CheckoutFragment{
            return CheckoutFragment()
        }

    }

}