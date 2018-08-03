package com.cozo.cozomvp.transition

import android.content.Context
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionSet
import android.view.View
import com.cozo.cozomvp.R
import com.cozo.cozomvp.mainactivity.DetailsLayout

class HideDetailsTransitionSet(private val context: Context, private val transitionName: String, private val fromView: View, private val toView: DetailsLayout) : TransitionSet() {

    init {
        addTransition(textResize())
        addTransition(shared())
    }

    private fun titleTransitionName(): String {
        return transitionName + TITLE_TEXT_VIEW_TRANSITION_NAME
    }

    private fun textResize(): Transition {
        return TransitionBuilder(TextResizeTransition())
            .link(fromView.findViewById(R.id.foodTitle), toView.textViewTitle, titleTransitionName())
            .build()
    }

    private fun cardViewTransitionName(): String {
        return transitionName + CARD_VIEW_TRANSITION_NAME
    }

    private fun shared(): Transition {
        return TransitionBuilder(TransitionInflater.from(context).inflateTransition(android.R.transition.move))
                .link(fromView.findViewById(R.id.foodImage), toView.imageViewPlaceDetails, transitionName)
                .link(fromView, toView.cardViewContainer, cardViewTransitionName())
                .build()
    }

    companion object {
        private const val TITLE_TEXT_VIEW_TRANSITION_NAME = "titleTextView"
        private const val CARD_VIEW_TRANSITION_NAME = "cardView"
    }
}