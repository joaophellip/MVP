package com.cozo.cozomvp.cartactivity

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.cozo.cozomvp.R
import com.cozo.cozomvp.cartactivity.listfragment.ListFragment
import com.cozo.cozomvp.cartactivity.listfragment.ListView
import com.cozo.cozomvp.usercart.OrderModel
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import kotlinx.android.synthetic.main.activity_cart.*

class CartActivity: MvpActivity<CartView, CartPresenter>(), CartView, ListView.CartActivityListener,
    View.OnClickListener{

    private lateinit var listFragment : ListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()

        setContentView(R.layout.activity_cart)

        //start list fragment
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.listContainer, ListFragment.newInstance(), ListFragment.TAG)
                .addToBackStack(ListFragment.TAG)
                .commit()

        //set confirmButton onClickListener
        activityCartConfirmOrderButton.setOnClickListener(this)
    }

    override fun onCompleteListFragment(fragment: ListFragment) {
        listFragment = fragment
        presenter.onFragmentReady()
    }

    override fun createPresenter(): CartPresenter {
        return CartPresenter()
    }

    override fun onUpdateCartOrder(orderID: Int, quantity: Int, notes: String) {
        presenter.onOrderUpdate(orderID, quantity, notes)
    }

    override fun onDeleteCartOrder(order: OrderModel) {
        presenter.onOrderDeleted(order)
    }

    override fun updateViewData(orders: List<OrderModel>) {
        //calculate total
        var total = 0f
        for (o in orders) {
            total += o.totalPrice
        }
        val formattedTotalPrice: String = String.format("%02.2f", total).replace(".",",")

        //show total in screen
        activityCartPrice.text = applicationContext.getString(R.string.total_label, formattedTotalPrice)

        //pass orders to list fragment
        listFragment.updateRecyclerView(orders)
    }

    override fun returnToParentActivity() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun editOrder(order: OrderModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClick(v: View?) {
        when(v){
            activityCartConfirmOrderButton -> {
                presenter.onConfirmOrderButtonClicked()
            }
        }
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     * See https://developer.android.com/training/appbar/up-action
     */
    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}