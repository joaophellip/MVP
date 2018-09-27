package com.cozo.cozomvp.cartactivity.listfragment

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import com.cozo.cozomvp.R
import com.cozo.cozomvp.listfragment.LocalListFragment
import com.cozo.cozomvp.usercart.OrderModel
import com.hannesdorfmann.mosby3.mvp.MvpFragment

class ListFragment : MvpFragment<ListView, ListPresenter>(), ListView {

    @BindView(R.id.recyclerView) lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: RecyclerViewAdapter
    private lateinit var listenerCartActivity : ListView.CartActivityListener

    override fun createPresenter(): ListPresenter = ListPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View? = inflater.inflate(R.layout.fragment_order, container, false)

        // initialize 'late init' properties
        recyclerView = view?.findViewById(R.id.recyclerView) as RecyclerView

        ButterKnife.bind(view)
        listenerCartActivity.onCompleteListFragment(this)

        return view
    }

    fun updateRecyclerView(orders: List<OrderModel>) {
        recyclerAdapter.setData(orders)
        recyclerView.adapter = recyclerAdapter
    }

    companion object {
        val TAG : String = ListFragment::class.java.simpleName

        fun newInstance(): ListFragment {
            return ListFragment()
        }
    }
}