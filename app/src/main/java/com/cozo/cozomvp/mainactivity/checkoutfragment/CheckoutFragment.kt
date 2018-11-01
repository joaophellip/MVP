package com.cozo.cozomvp.mainactivity.checkoutfragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cozo.cozomvp.R
import com.cozo.cozomvp.usercart.CartServiceImpl
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import kotlinx.android.synthetic.main.fragment_checkout.*

class CheckoutFragment : MvpFragment<CheckoutView, CheckoutPresenter>(){

    private lateinit var listenerMainActivity : CheckoutView.MainActivityListener

    override fun createPresenter(): CheckoutPresenter = CheckoutPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View? = inflater.inflate(R.layout.fragment_checkout, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenerMainActivity.onCompleteCheckoutFragment(this)
        checkoutFragment.setOnClickListener { listenerMainActivity.onCheckoutClicked()}
    }

    override fun onAttach(context: Context?) {
        //https://developer.android.com/training/basics/fragments/communicating
        super.onAttach(context)
        try {
            listenerMainActivity = activity as CheckoutView.MainActivityListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement CheckoutView.MainActivityListener ")
        }
    }

    fun updateContainer(){
        quantityText.text = CartServiceImpl.myInstance.getOrders().size.toString()
        listenerMainActivity.displayContainer()
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