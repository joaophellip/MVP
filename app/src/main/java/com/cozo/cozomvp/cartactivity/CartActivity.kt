package com.cozo.cozomvp.cartactivity

import android.os.Bundle
import android.widget.TextView
import butterknife.BindView
import com.cozo.cozomvp.R
import com.cozo.cozomvp.cartactivity.listfragment.ListFragment
import com.cozo.cozomvp.cartactivity.listfragment.ListView
import com.cozo.cozomvp.usercart.OrderModel
import com.hannesdorfmann.mosby3.mvp.MvpActivity

class CartActivity: MvpActivity<CartView, CartPresenter>(), CartView, ListView.CartActivityListener {

    lateinit var listFragment : ListFragment
    @BindView(R.id.totalOrderPrice) lateinit var totalOrderPrice: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_cart)

        //start list fragment
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.listContainer, ListFragment.newInstance(), ListFragment.TAG)
                .addToBackStack(ListFragment.TAG)
                .commit()
    }

    override fun onCompleteListFragment(fragment: ListFragment) {
        listFragment = fragment
        presenter.onFragmentReady()
    }

    override fun createPresenter(): CartPresenter {
        return CartPresenter()
    }

    override fun updateViewData(orders: List<OrderModel>) {
        //calculate total
        var total: Float = 0f
        for (o in orders) {
            total += o.totalPrice
        }

        //show in screen
        totalOrderPrice.text = applicationContext.getString(R.string.total_label, total)

        //pass orders to list fragment
        listFragment.updateRecyclerView(orders)
    }

    override fun returnToParentActivity() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun editOrder(order: OrderModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}