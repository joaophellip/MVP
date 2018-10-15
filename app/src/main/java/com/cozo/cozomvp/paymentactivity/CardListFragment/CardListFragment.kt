package com.cozo.cozomvp.paymentactivity.CardListFragment

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cozo.cozomvp.R
import com.cozo.cozomvp.paymentactivity.PaymentActivity
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import kotlinx.android.synthetic.main.fragment_card_list.*

class CardListFragment: MvpFragment<CardListInterface, CardListPresenter>(), CardListInterface,
        CardListRecyclerAdapter.CardListeners{

    private lateinit var mActivityListener: CardListInterface.CardActivityListener
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRecyclerAdapter: CardListRecyclerAdapter

    override fun createPresenter(): CardListPresenter {
        return CardListPresenter()
    }

    override fun deleteCard(cardId: String) {
        mActivityListener.onCardDeleted(cardId)
    }

    override fun selectCard(cardId: String) {
        context?.getSharedPreferences("CozoApp",Context.MODE_PRIVATE)?.edit()?.putString("MainCardId",cardId)?.apply()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_card_list, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addCardButton.setOnClickListener{
            mActivityListener.onAddCardButtonClicked()
        }
        mRecyclerView = view.findViewById(R.id.cardRecycler)
        mRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mRecyclerAdapter = CardListRecyclerAdapter(this)
        mActivityListener.onCardListFragmentReady(this)
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
        mRecyclerAdapter.updateCardData(cards)
        mRecyclerView.adapter = mRecyclerAdapter
        if (cards.isEmpty()){
            noCardText.visibility = View.VISIBLE
        }
    }

    companion object {
        val TAG : String = CardListFragment::class.java.simpleName
        fun newInstance(): CardListFragment {
            return CardListFragment()
        }
    }
}