package com.cozo.cozomvp.mainactivity.showdelivfragment

import com.hannesdorfmann.mosby3.mvp.MvpView

interface ShowDeliverersView : MvpView {

    interface MainActivityListener{

        /*
        Informs activity that method onCreateView from fragment run successfully. Passes
        reference to the class as argument.
         */
        fun onCompleteShowDeliverersFragment(showDeliverersFragment: ShowDeliverersFragment)

        fun displayContainer()

        fun onFragmentClicked()
    }
}