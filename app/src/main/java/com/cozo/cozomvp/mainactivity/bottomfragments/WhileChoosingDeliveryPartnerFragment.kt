package com.cozo.cozomvp.mainactivity.bottomfragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cozo.cozomvp.R
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import kotlinx.android.synthetic.main.fragment_bottom_while_choosing_items.*

class WhileChoosingDeliveryPartnerFragment : MvpFragment<WhileChoosingDeliveryPartnerView, WhileChoosingDeliveryPartnerPresenter>(){

    private lateinit var listenerMainActivity : WhileChoosingDeliveryPartnerView.MainActivityListener

    override fun createPresenter(): WhileChoosingDeliveryPartnerPresenter = WhileChoosingDeliveryPartnerPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bottom_while_choosing_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenerMainActivity.onCompleteShowDeliverersFragment(this)
        choosingItemsContainer.setOnClickListener { listenerMainActivity.onChoosingDeliveryPartnerCheckoutButtonClicked()}
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            listenerMainActivity = activity as WhileChoosingDeliveryPartnerView.MainActivityListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement WhileChoosingDeliveryPartnerView.MainActivityListener ")
        }
    }

    companion object {
        val TAG : String = WhileChoosingDeliveryPartnerFragment::class.java.simpleName
        fun newInstance(): WhileChoosingDeliveryPartnerFragment = WhileChoosingDeliveryPartnerFragment()
    }

}