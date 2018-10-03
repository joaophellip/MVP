package com.cozo.cozomvp.mainactivity.listfragment

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import com.cozo.cozomvp.R
import com.cozo.cozomvp.mainactivity.MainActivity
import com.cozo.cozomvp.networkapi.CardInfoData
import com.cozo.cozomvp.networkapi.CardMenuData
import com.cozo.cozomvp.networkapi.NetworkModel
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import org.jetbrains.anko.displayMetrics
import java.lang.Math.ceil
import org.jetbrains.anko.forEachChild
import android.support.v7.widget.helper.ItemTouchHelper
import com.cozo.cozomvp.mainactivity.listfragment.recyclerview.PartnersRecyclerViewAdapter
import com.cozo.cozomvp.mainactivity.listfragment.recyclerview.RestaurantItemsRecyclerViewAdapter
import com.cozo.cozomvp.mainactivity.listfragment.recyclerview.RestaurantRecyclerViewAdapter
import com.cozo.cozomvp.mainactivity.listfragment.recyclerview.SwipeController

class LocalListFragment : MvpFragment<ListFragmentView, ListPresenter>(), ListFragmentView,
        RestaurantRecyclerViewAdapter.OnPlaceClickListener, PartnersRecyclerViewAdapter.OnPlaceClickListener,
        SwipeController.OnSwipeClickListener, RestaurantItemsRecyclerViewAdapter.OnPlaceClickListener {

    @BindView(R.id.recyclerView) lateinit var mRecyclerView: RecyclerView

    private var recyclerViewDx : Int = 0

    lateinit var mRestaurantsRecyclerAdapter: RestaurantRecyclerViewAdapter
    private lateinit var mPartnersRecyclerAdapter: PartnersRecyclerViewAdapter
    private lateinit var restaurantItemsRecyclerAdapter: RestaurantItemsRecyclerViewAdapter
    private lateinit var listenerMainActivity : ListFragmentView.MainActivityListener
    private lateinit var mRootView: ViewGroup
    private var isCardViewShownRestaurant: Boolean = true
    private var currentRestaurantId : String? = null

    override fun addItemsDataToCards(items: List<NetworkModel.MenuMetadata>) {
        restaurantItemsRecyclerAdapter.setItemList(items)
        mRecyclerView.adapter = restaurantItemsRecyclerAdapter
        isCardViewShownRestaurant = false
        listenerMainActivity.onItemsCardDataReady()
    }

    override fun addPartnersDataToCards(cards: MutableMap<String, CardInfoData>) {
        mPartnersRecyclerAdapter.setPartnerList(cards)
        mRecyclerView.adapter = mPartnersRecyclerAdapter
        isCardViewShownRestaurant = false
        val locations: MutableMap<String, NetworkModel.Location> = mutableMapOf()
        val routes: MutableMap<String, List<NetworkModel.Leg>> = mutableMapOf()
        cards.forEach {
            locations[it.key] = it.value.info?.location!!
            routes[it.key] = it.value.info?.route!!
        }
        listenerMainActivity.onPartnersCardDataReady(locations, routes)
    }

    override fun addRestaurantsDataToCards(items: List<NetworkModel.MenuMetadata>) {
        mRestaurantsRecyclerAdapter.setRestaurantList(items)
        mRecyclerView.adapter = mRestaurantsRecyclerAdapter
    }

    override fun onAttach(context: Context?) {
        //https://developer.android.com/training/basics/fragments/communicating
        super.onAttach(context)
        try {
            listenerMainActivity = activity as ListFragmentView.MainActivityListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement ListFragmentView.MainActivityListener ")
        }
    }

    override fun createPresenter(): ListPresenter {
        return ListPresenter()
    }

    override fun currentRestID(listPosition: Int): String {
        return if (currentRestaurantId != null) {
            currentRestaurantId!!
        } else {
            currentRestaurantId = mRestaurantsRecyclerAdapter.currentRestID(listPosition)
            currentRestaurantId!!
        }
    }

    override fun dishOrderCreation(listPosition: Int) {
        presenter.dishNew(this.currentRestID(listPosition))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        //Adds Swipe Controller to recyclerView
        val mSwipeController = SwipeController(this)
        val mItemTouchHelper = ItemTouchHelper(mSwipeController)
        mItemTouchHelper.attachToRecyclerView(mRecyclerView)

        mRestaurantsRecyclerAdapter = RestaurantRecyclerViewAdapter(this)
        mPartnersRecyclerAdapter = PartnersRecyclerViewAdapter(this)
        restaurantItemsRecyclerAdapter = RestaurantItemsRecyclerViewAdapter(this)

        mRootView = view as ViewGroup
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mView : View? = inflater.inflate(R.layout.fragment_list, container, false)

        // initialize 'late init' properties
        mRecyclerView = mView?.findViewById(R.id.recyclerView) as RecyclerView

        // object RecyclerView.OnScrollListener with implementation of onScrolled method. Controls the behavior whenever recycler view is scrolled.
        val scrollHandler = object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // stores dx shift
                recyclerViewDx += dx

                // calculates the position inside the list for the card shown in screen
                val mCardWidth: Int = mRestaurantsRecyclerAdapter.mCardViewLayoutParams.leftMargin + mRestaurantsRecyclerAdapter.mCardViewLayoutParams.rightMargin + mRestaurantsRecyclerAdapter.mCardViewLayoutParams.width
                val mTargetPosition: Int = when(recyclerViewDx){
                    0 -> 1
                    (mCardWidth*ListPresenter.restListSize  - recyclerView!!.width) -> ListPresenter.restListSize
                    else -> ceil(recyclerView.width.toDouble()/mCardWidth - 1 + recyclerViewDx.toDouble()/mCardWidth).toInt()
                }

                // changes elevation for the card shown in screen when it's restaurants
                if (isCardViewShownRestaurant) {
                    recyclerView?.forEachChild {
                        when(recyclerView.getChildAdapterPosition(it) + 1) {
                            mTargetPosition -> {
                                // change elevation and triggers callback onExpandMapIcon
                                it.elevation = 24f
                            }
                            else -> it.elevation = 2f
                        }
                    }
                }

                // triggers listener to inform position to map fragment
                //listenerMainActivity.onRestCardViewHighlighted(this@LocalListFragment.currentRestID(mTargetPosition-1))
            }
        }
        mRecyclerView.addOnScrollListener(scrollHandler)

        ButterKnife.bind(mView)
        listenerMainActivity.onCompleteListFragment(this)

        return mView
    }

    override fun onActivityRequired(): MainActivity {
        return listenerMainActivity.onActivityRequired()
    }

    override fun onItemCardViewClicked(sharedView: View, transitionName: String, position: Int, data: CardMenuData) {
        listenerMainActivity.onItemCardViewClicked(sharedView, transitionName, data)
    }

    override fun onUserLocationDataAvailable(location: NetworkModel.Location) {
        presenter.onUserLocationDataAvailable(location)
    }

    override fun onRecyclerViewRequired(): RecyclerView {
        return mRecyclerView
    }

    override fun onRestLocationDataAvailable(location: NetworkModel.Location) {
        presenter.onRestLocationDataAvailable(location)
    }

    override fun onRestaurantCardViewClicked(sharedView: View, transitionName: String, position: Int, data: CardMenuData) {
        listenerMainActivity.onRestaurantCardViewClicked(sharedView, transitionName, data)
    }

    override fun onRestaurantCardViewSwiped(sharedView: View, transitionName: String, position: Int) {
        val restID: String = mRestaurantsRecyclerAdapter.currentRestID(position)
        val data: CardMenuData? = mRestaurantsRecyclerAdapter.cardData(restID)
        listenerMainActivity.onRestaurantCardViewClicked(sharedView, transitionName, data!!)
    }

    override fun onPartnerCardViewClicked(partnerID: String) {
        listenerMainActivity.onPartnerCardViewClicked(partnerID)
    }

    override fun highlightRestCardView(restID: String) {
        val mPosition : Int? = mRestaurantsRecyclerAdapter.positionById(restID)
        if (mPosition != null){
            when {
                mPosition == -1 -> {
                    Log.d("MVPdebug","position in recycler view list could not be found")
                    // do something
                }
                mPosition < mRecyclerView.adapter.itemCount -> {
                    // Centers card in the screen
                    val mCardWidth : Int = mRestaurantsRecyclerAdapter.mCardViewLayoutParams.leftMargin + mRestaurantsRecyclerAdapter.mCardViewLayoutParams.rightMargin + mRestaurantsRecyclerAdapter.mCardViewLayoutParams.width
                    val mShift: Int = (2*mRestaurantsRecyclerAdapter.mCardViewLayoutParams.leftMargin + mRestaurantsRecyclerAdapter.mCardViewLayoutParams.rightMargin + mRestaurantsRecyclerAdapter.mCardViewLayoutParams.width)/2
                    val mScreenWidth : Int? = context?.displayMetrics?.widthPixels
                    when (mPosition){
                        0 -> mRecyclerView.smoothScrollBy(-recyclerViewDx,0)
                        in 1..5 -> mRecyclerView.smoothScrollBy(mPosition*mCardWidth - mShift - recyclerViewDx,0)
                        6 -> mRecyclerView.smoothScrollBy(7*mCardWidth-mScreenWidth!! - recyclerViewDx,0)
                    }
                    // Changes card elevation
                }
                else -> {
                    Log.d("MVPdebug","position in recycler view list can't be bigger than adapter itemCount")
                    // do something
                }
            }
        } else {
            Log.d("MVPdebug","no card associated with restaurant $restID")

        }
    }

    override fun requestLayout() {
        mRecyclerView.requestLayout()
        //mRestaurantsRecyclerAdapter.notifyItemChanged(childPosition)
    }

    override fun sharedViewByPosition(childPosition: Int): View? {
        for (i: Int in 0 until mRecyclerView.childCount) {
            if (childPosition == mRecyclerView.getChildAdapterPosition(mRecyclerView.getChildAt(i))) {
                return mRecyclerView.getChildAt(i)
            }
        }
        return null
    }

    companion object {
        val TAG : String = LocalListFragment::class.java.simpleName

        fun newInstance(): LocalListFragment {
            return LocalListFragment()
        }
    }

}