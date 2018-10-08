package com.cozo.cozomvp.paymentactivity.CardListFragment

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cozo.cozomvp.R
import com.cozo.cozomvp.paymentactivity.PaymentActivity
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import kotlinx.android.synthetic.main.fragment_card_list.*

class CardListFragment: MvpFragment<CardListInterface, CardListPresenter>(), CardListInterface{

    private lateinit var mActivityListener:CardListInterface.CardActivityListener
    lateinit var mRecyclerView:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityListener.updateCardList()
    }

    override fun createPresenter(): CardListPresenter {
        return CardListPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mview: View? = inflater.inflate(R.layout.fragment_card_list, container, false)
        return mview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addCardButton.setOnClickListener({
            mActivityListener.onAddCardButtonClicked()
        })
        mRecyclerView = view.findViewById(R.id.cardRecycler)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mActivityListener = activity as CardListInterface.CardActivityListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement MapFragmentView.MainActivityListener ")
        }
    }

    override fun onCardListAvailable(cards: List<PaymentActivity.CardData>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        val TAG : String = CardListFragment::class.java.simpleName
        fun newInstance(): CardListFragment {
            return CardListFragment()
        }
    }
}