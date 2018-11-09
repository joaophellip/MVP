package com.example

import android.view.View
import com.cozo.cozomvp.mainactivity.listfragment.recyclerview.RestaurantRecyclerViewAdapter
import com.cozo.cozomvp.networkapi.NetworkModel
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RestaurantAdapterUnitTest {

    private var restaurantID = "abc123"
    private val item: NetworkModel.MenuMetadata = NetworkModel.MenuMetadata("pimenta",
            "itemTest",
            10,
            "null",
            10f,
            5f,
            1,
            "Kiosk da Ana",
            restaurantID)
    private val listenerImpl = object: RestaurantRecyclerViewAdapter.OnPlaceClickListener{
        override fun onRestaurantCardViewClicked(sharedView: View, transitionName: String, position: Int, data: NetworkModel.MenuMetadata) {
            //
        }
    }

    @Test
    fun restaurantRecyclerViewAdapter_currentRestID_notNull(){
        val adapter = RestaurantRecyclerViewAdapter(listenerImpl)
        adapter.setRestaurantList(listOf(item))
        Assert.assertNotNull(adapter.currentRestID(0))
    }

    @Test
    fun restaurantRecyclerViewAdapter_positionById_zero(){
        val adapter = RestaurantRecyclerViewAdapter(listenerImpl)
        adapter.setRestaurantList(listOf(item))
        Assert.assertEquals(0,adapter.positionById(restaurantID))
    }

}