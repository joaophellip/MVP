package com.cozo.cozomvp.listfragment

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.cozo.cozomvp.transition.TransitionUtils


class SwipeController() : Callback() {

    private lateinit var listener: OnSwipeClickListener
    private var mSwipeBack : Boolean = false

    constructor(listener: OnSwipeClickListener) : this(){
        this.listener = listener
    }

    interface OnSwipeClickListener {
        fun onRestaurantCardViewSwiped(sharedView: View, transitionName: String, position: Int)
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(0, UP)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
         if (mSwipeBack) {
            mSwipeBack = false
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?) {
        listener.onRestaurantCardViewSwiped(viewHolder?.itemView!!,
                TransitionUtils.getRecyclerViewTransitionName(viewHolder.layoutPosition),
                viewHolder.layoutPosition)
        super.clearView(recyclerView, viewHolder)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                    dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ACTION_STATE_SWIPE) {
            setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
        // Disables swipe movement. Only captures the gesture
        //super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun setTouchListener(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                 dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        recyclerView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                mSwipeBack = event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
                return false
            }
        })
    }

}