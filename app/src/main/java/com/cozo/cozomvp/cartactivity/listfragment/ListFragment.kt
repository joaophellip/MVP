package com.cozo.cozomvp.cartactivity.listfragment

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cozo.cozomvp.R
import com.cozo.cozomvp.usercart.OrderModel
import com.hannesdorfmann.mosby3.mvp.MvpFragment

class ListFragment : MvpFragment<ListView, ListPresenter>(), ListView,
        RecyclerViewAdapter.OnRecyclerListener {

    lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: RecyclerViewAdapter
    private lateinit var listenerCartActivity : ListView.CartActivityListener

    override fun createPresenter(): ListPresenter = ListPresenter()

    override fun onAttach(context: Context?) {
        //https://developer.android.com/training/basics/fragments/communicating
        super.onAttach(context)
        try {
            listenerCartActivity = activity as ListView.CartActivityListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement ListView.CartActivityListener ")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View? = inflater.inflate(R.layout.fragment_order, container, false)

        // initialize 'late init' properties
        recyclerView = view?.findViewById(R.id.recyclerView) as RecyclerView
        recyclerAdapter = RecyclerViewAdapter(this)

        listenerCartActivity.onCompleteListFragment(this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onMinusButtonClicked(orderID: Int, quantity: Int, notes: String) {
        listenerCartActivity.onUpdateCartOrder(orderID,quantity,notes)
    }

    override fun onPlusButtonClicked(orderID: Int, quantity: Int, notes: String) {
        listenerCartActivity.onUpdateCartOrder(orderID,quantity,notes)
    }

    override fun onDeleteButtonClicked(order: OrderModel) {
        listenerCartActivity.onDeleteCartOrder(order)
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