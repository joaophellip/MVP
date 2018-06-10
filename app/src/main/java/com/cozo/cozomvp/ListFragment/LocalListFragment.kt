package com.cozo.cozomvp.listFragment


import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import com.cozo.cozomvp.R
import com.cozo.cozomvp.networkAPI.ListPresenterData
import com.cozo.cozomvp.networkAPI.NetworkModel
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import org.jetbrains.anko.displayMetrics
import java.lang.Math.ceil


class LocalListFragment : MvpFragment<ListFragmentView, ListFragmentPresenter>(), ListFragmentView {

    @BindView(R.id.recyclerView)
    lateinit var mRecyclerView: RecyclerView

    private var recyclerViewDx : Int = 0
    private lateinit var mRecyclerAdapter: RecyclerViewAdapter
    private lateinit var mListenerMainActivity : ListFragmentView.MainActivityListener
    private lateinit var mRootView: ViewGroup

    override fun onAttach(context: Context?) {     //https://developer.android.com/training/basics/fragments/communicating
        super.onAttach(context)
        try {
            mListenerMainActivity = activity as ListFragmentView.MainActivityListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement ListFragmentView.MainActivityListener ")
        }
    }

    override fun createPresenter(): ListFragmentPresenter {
        return ListFragmentPresenter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        mRecyclerAdapter = RecyclerViewAdapter()
        mRootView = view as ViewGroup
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mView : View? = inflater.inflate(R.layout.fragment_list, container, false)
        mRecyclerView = mView?.findViewById(R.id.recyclerView) as RecyclerView  //had to initialize the variable recyclerView. But why since I annotated with ButterKnife (this bind wasn't enough)
        // object RecyclerView.OnScrollListener with implementation of onScrolled method. Controls the behavior whenever recycler view is scrolled.
        val scrollHandler = object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                recyclerViewDx += dx
                val mCardWidth: Int = mRecyclerAdapter.mCardViewLayoutParams.leftMargin + mRecyclerAdapter.mCardViewLayoutParams.rightMargin + mRecyclerAdapter.mCardViewLayoutParams.width
                val mTargetPosition: Int = when(recyclerViewDx){
                    0 -> 1
                    (mCardWidth*ListFragmentPresenter.listSize  - recyclerView!!.width) -> ListFragmentPresenter.listSize  // get listSize from presenter??
                    else -> ceil(recyclerView.width.toDouble()/mCardWidth - 1 + recyclerViewDx.toDouble()/mCardWidth).toInt()
                }
                mListenerMainActivity.onCardViewHighlighted(mRecyclerAdapter.currentRestID(mTargetPosition-1))  //for another phones its getting out of range (> ListFragmentPresenter.listSize-1)
            }
        }
        mRecyclerView.addOnScrollListener(scrollHandler)

        ButterKnife.bind(mView)
        return mView
    }

    override fun onLocationDataAvailable(location: NetworkModel.Location) {
        presenter.onLocationDataAvailable(location)
    }

    override fun addRestaurantsDataToCards(cards: List<ListPresenterData>) {
        mRecyclerAdapter.setRestaurantList(cards)
        mRecyclerView.adapter = mRecyclerAdapter
    }

    override fun highlightCardView(restID: String) {
        val mPosition : Int? = mRecyclerAdapter.positionById(restID)
        if (mPosition != null){
            when {
                mPosition == -1 -> {
                    Log.d("MVPdebug","position in recycler view list could not be found")
                }
                mPosition < mRecyclerView.adapter.itemCount -> {
                    // Centers card in the screen
                    val mCardWidth : Int = mRecyclerAdapter.mCardViewLayoutParams.leftMargin + mRecyclerAdapter.mCardViewLayoutParams.rightMargin + mRecyclerAdapter.mCardViewLayoutParams.width
                    val mShift: Int = (2*mRecyclerAdapter.mCardViewLayoutParams.leftMargin + mRecyclerAdapter.mCardViewLayoutParams.rightMargin + mRecyclerAdapter.mCardViewLayoutParams.width)/2
                    val mScreenWidth : Int? = context?.displayMetrics?.widthPixels
                    when (mPosition){
                        0 -> mRecyclerView.smoothScrollBy(-recyclerViewDx,0)
                        in 1..5 -> mRecyclerView.smoothScrollBy(mPosition*mCardWidth - mShift - recyclerViewDx,0)
                        6 -> mRecyclerView.smoothScrollBy(7*mCardWidth-mScreenWidth!! - recyclerViewDx,0)
                    }
                }
                else -> {
                    Log.d("MVPdebug","position in recycler view list can't be bigger than adapter itemCount")
                }
            }
        } else {
            Log.d("MVPdebug","no card associated with restaurant $restID")
        }
    }

    companion object {
        val TAG : String = LocalListFragment::class.java.simpleName

        fun newInstance(): LocalListFragment {
            return LocalListFragment()
        }
    }

}