package com.cozo.cozomvp.mainactivity

/*
Interface DetailsInterface contains the abstract methods implemented by MainActivity.kt and which are seen by DetailsLayout.
 */
interface DetailsInterface {

    interface MainActivityListener {

        /*
        Informs activity that order button was clicked by user.
         */
        fun onOrderButtonClicked()

        /*
        Informs main activity that addItemToCart button was clicked by user.
         */
        fun onItemAddedToCart()
    }

}