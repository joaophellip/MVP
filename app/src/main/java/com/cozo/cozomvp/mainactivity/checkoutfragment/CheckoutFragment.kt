package com.cozo.cozomvp.mainactivity.checkoutfragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.cozo.cozomvp.R
import com.cozo.cozomvp.usercart.CartServiceImpl
import com.hannesdorfmann.mosby3.mvp.MvpFragment

class CheckoutFragment : MvpFragment<CheckoutView, CheckoutPresenter>(){

    @BindView(R.id.quantityText) lateinit var mQuantityText: TextView

    private lateinit var mListenerMainActivity : CheckoutView.MainActivityListener

    override fun createPresenter(): CheckoutPresenter = CheckoutPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mView : View? = inflater.inflate(R.layout.fragment_checkout, container, false)
        mListenerMainActivity.onCompleteCheckoutFragment(this)
        ButterKnife.bind(mView!!)
        return mView
    }

    override fun onAttach(context: Context?) {
        //https://developer.android.com/training/basics/fragments/communicating
        super.onAttach(context)
        try {
            mListenerMainActivity = activity as CheckoutView.MainActivityListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement CheckoutView.MainActivityListener ")
        }
    }

    fun updateContainer(){
        mQuantityText.text = CartServiceImpl.myInstance.getOrders().size.toString()
        mListenerMainActivity.displayContainer()
    }

    fun hideContainer(){
        //TODO
    }

    companion object {
        val TAG : String = CheckoutFragment::class.java.simpleName

        fun newInstance(): CheckoutFragment{
            return CheckoutFragment()
        }

    }

}