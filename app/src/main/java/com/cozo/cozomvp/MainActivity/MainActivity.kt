package com.cozo.cozomvp.MainActivity

import android.os.Bundle
import com.cozo.cozomvp.*
import com.hannesdorfmann.mosby3.mvp.MvpActivity

class MainActivity : MvpActivity<MainView, MainPresenter>(), MainView {

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        setContentView(R.layout.activity_main)

        // launch map fragment
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.mapContainer, LocalMapFragment.newInstance(), LocalMapFragment.TAG)
                .addToBackStack(LocalMapFragment.TAG)
                .commit()

        // launch list fragment
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.listContainer, LocalListFragment.newInstance(), LocalListFragment.TAG)
                .addToBackStack(LocalListFragment.TAG)
                .commit()
    }

    override fun createPresenter(): MainPresenter {
        return MainPresenter()
    }

    override fun highlightCard(restaurantID: String) {
        val mListFragment = supportFragmentManager.findFragmentByTag(LocalListFragment.TAG) as LocalListFragment
        presenter.highlightCardView(restaurantID, mListFragment)
    }

}