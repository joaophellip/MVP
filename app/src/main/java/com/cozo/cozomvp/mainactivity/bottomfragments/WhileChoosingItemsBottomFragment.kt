package com.cozo.cozomvp.mainactivity.bottomfragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cozo.cozomvp.R
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import kotlinx.android.synthetic.main.fragment_bottom_while_choosing_items.*

class WhileChoosingItemsBottomFragment : MvpFragment<WhileChoosingItemsBottomView, WhileChoosingItemsBottomPresenter>(){

    private lateinit var listenerMainActivity : WhileChoosingItemsBottomView.MainActivityListener

    override fun createPresenter(): WhileChoosingItemsBottomPresenter = WhileChoosingItemsBottomPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bottom_while_choosing_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenerMainActivity.onCompleteWhileChoosingItemsBottomFragment(this)
        choosingItemsDeliveryPartnerButton.setOnClickListener { listenerMainActivity.onChoosingItemsDeliveryPartnerButtonClicked()}
    }

    override fun onAttach(context: Context?) {
        //https://developer.android.com/training/basics/fragments/communicating
        super.onAttach(context)
        try {
            listenerMainActivity = activity as WhileChoosingItemsBottomView.MainActivityListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement WhileChoosingItemsBottomView.MainActivityListener ")
        }
    }

    fun updateContainerPrice(currentPrice: String){
        choosingItemsPriceText.text = context?.getString(R.string.fragment_bottom_while_choosing_items_price , currentPrice)
    }

    companion object {
        val TAG : String = WhileChoosingItemsBottomFragment::class.java.simpleName

        fun newInstance(): WhileChoosingItemsBottomFragment{
            return WhileChoosingItemsBottomFragment()
        }

    }

}