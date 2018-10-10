package com.cozo.cozomvp.paymentactivity.AddCardFragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cozo.cozomvp.R
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import kotlinx.android.synthetic.main.fragment_new_card.*

class AddCardFragment:MvpFragment<AddCardInterface, AddCardPresenter>() {

    private lateinit var mActivityListener: AddCardInterface.CardActivityListener

    override fun createPresenter(): AddCardPresenter {
        return AddCardPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createCardButton.setOnClickListener{
            //capturar mActivityListener.onNewCardCreated(NewCreditCardData(birthDate.rawText,holderName.text))
        }
        //https://stackoverflow.com/questions/20824634/android-on-text-change-listener
        //creditCardNumber.addTextChangedListener()
        //cpf.addTextChangedListener()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mActivityListener = activity as AddCardInterface.CardActivityListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement AddCardInterface.CardActivityListener ")
        }
    }

    companion object {
        val TAG : String = AddCardFragment::class.java.simpleName
        fun newInstance(): AddCardFragment {
            return AddCardFragment()
        }
    }

    data class NewCreditCardData(
            val cardNumber : String,
            val holderName: String,
            val cvc: String,
            val expireDate: String,
            val holderCpf: String,
            val birthDate: String)
}