package com.cozo.cozomvp

import android.support.v7.widget.RecyclerView
import android.util.Log
import butterknife.BindView
import com.hannesdorfmann.mosby3.mvp.MvpFragment

class LocalListFragment : MvpFragment<ListFragmentView, ListFragmentPresenter>(), ListFragmentView {

    @BindView(R.id.recyclerView)
    lateinit var recyclerView: RecyclerView

    override fun createPresenter(): ListFragmentPresenter {
        return ListFragmentPresenter()
    }

    /*override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mView = inflater.inflate(R.layout.fragment_list, container, false)
        val mRecyclerView = mView.recyclerView
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = mLayoutManager
        return mView
    }*/
    /*override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view!!, savedInstanceState)
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
    }*/

    override fun highlightCardView(restaurantID: String) {
        Log.d("highlightCardView", "test")
    }

    companion object {
        val TAG : String = LocalListFragment::class.java.simpleName

        fun newInstance():LocalMapFragment{
            return LocalMapFragment()
        }
    }

}