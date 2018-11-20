package com.cozo.cozomvp.paymentactivity

import android.os.Bundle
import android.telephony.PhoneNumberUtils
import com.cozo.cozomvp.R
import com.cozo.cozomvp.paymentactivity.AddCardFragment.AddCardFragment
import com.cozo.cozomvp.paymentactivity.AddCardFragment.AddCardInterface
import com.cozo.cozomvp.paymentactivity.CardListFragment.CardListFragment
import com.cozo.cozomvp.paymentactivity.CardListFragment.CardListInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.hannesdorfmann.mosby3.mvp.MvpActivity

class PaymentActivity : MvpActivity<PaymentView, PaymentPresenter>(), CardListInterface.CardActivityListener,
        AddCardInterface.CardActivityListener, PaymentView  {

    private lateinit var cardListFragment : CardListFragment

    data class CardData(
        val cardId:String,
        val name: String,
        val cardNumber: String,
        val cardNetwork: Int
    )

    override fun createPresenter(): PaymentPresenter {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val phoneNumber = PhoneNumberUtil.getInstance().parse(firebaseUser!!.phoneNumber, "BR")
        val stateCode = phoneNumber.nationalNumber.toString().substring(0,2)
        val number = phoneNumber.nationalNumber.toString().substring(2)
        return PaymentPresenter(PaymentPresenter.UserInfo(null,firebaseUser.uid,firebaseUser.email!!,
                PaymentPresenter.PhoneNumber(phoneNumber.countryCode.toString(),stateCode, number)))
    }

    override fun onCardListFragmentReady(fragment: CardListFragment) {
        cardListFragment = fragment
        presenter.onCardListFragmentReady()
    }

    override fun onAddCardButtonClicked() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.payment_frame, AddCardFragment.newInstance(),AddCardFragment.TAG)
                .commit()
    }

    override fun onCardDeleted(cardId:String) {
        presenter.onCardDeleted()
    }

    override fun onNewCardCreated(creditCard: AddCardFragment.NewCreditCardData) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.payment_frame, CardListFragment.newInstance(),CardListFragment.TAG)
                .commit()
        presenter.onNewCardCreated(creditCard)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        supportFragmentManager
                .beginTransaction()
                .add(R.id.payment_frame, CardListFragment.newInstance(),CardListFragment.TAG)
                .commit()
    }

    override fun onCardsAvailable(cards: List<CardData>) {
        cardListFragment.onCardListAvailable(cards)
    }
}
