package com.cozo.cozomvp.mainactivity.showdelivfragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cozo.cozomvp.R
import com.cozo.cozomvp.R.id.cartContainer
import com.cozo.cozomvp.R.id.showQuantityText
import com.cozo.cozomvp.usercart.CartServiceImpl
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_show_deliverers.*

class ShowDeliverersFragment : MvpFragment<ShowDeliverersView, ShowDeliverersPresenter>(){

    private lateinit var listenerMainActivity : ShowDeliverersView.MainActivityListener

    override fun createPresenter(): ShowDeliverersPresenter = ShowDeliverersPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_show_deliverers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenerMainActivity.onCompleteShowDeliverersFragment(this)
        showDeliverersFragment.setOnClickListener { listenerMainActivity.onFragmentClicked()}
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            listenerMainActivity = activity as ShowDeliverersView.MainActivityListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement ShowDeliverersView.MainActivityListener ")
        }
    }

    fun updateContainerQuantityText(){
        showQuantityText.text = CartServiceImpl.myInstance.getOrders().size.toString()
        listenerMainActivity.displayContainer()
    }

    companion object {
        val TAG : String = ShowDeliverersFragment::class.java.simpleName
        fun newInstance(): ShowDeliverersFragment = ShowDeliverersFragment()
    }

}