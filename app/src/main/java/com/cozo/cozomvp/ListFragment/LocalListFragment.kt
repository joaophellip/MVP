package com.cozo.cozomvp.listFragment

import android.os.Bundle
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import butterknife.BindView
import com.cozo.cozomvp.CardMenuData
import com.cozo.cozomvp.mapFragment.LocalMapFragment
import com.cozo.cozomvp.NetworkModel
import com.cozo.cozomvp.R

class LocalListFragment : MvpFragment<ListFragmentView, ListFragmentPresenter>(), ListFragmentView {

    @BindView(R.id.recyclerView)
    lateinit var recyclerView: RecyclerView

    private lateinit var mRecyclerAdapter: RecyclerViewAdapter

    override fun createPresenter(): ListFragmentPresenter {
        return ListFragmentPresenter()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        mRecyclerAdapter = RecyclerViewAdapter()
    }

    override fun onLocationDataAvailable(location: NetworkModel.Location) {
        presenter.onLocationDataAvailable(location)
    }

    override fun addRestaurantsDataToCards(cards: List<CardMenuData>) {
        mRecyclerAdapter.setRestaurantList(cards)
        recyclerView.adapter = mRecyclerAdapter
    }

    override fun highlightCardView(restaurantID: String) {
        Log.d("highlightCardView", "test")
    }

    companion object {
        val TAG : String = LocalListFragment::class.java.simpleName

        fun newInstance(): LocalMapFragment {
            return LocalMapFragment()
        }
    }

}