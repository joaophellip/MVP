package com.cozo.cozomvp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.braintreepayments.cardform.view.CardForm
import kotlinx.android.synthetic.main.activity_payment.*
import org.jetbrains.anko.toast

class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        val cardForm: CardForm = findViewById(R.id.card_form)
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                .mobileNumberExplanation("SMS is required on this number")
                .actionLabel("Purchase")
                .setup(this)

        floatingActionButton.setOnClickListener {
            if (cardForm.isValid) {
                toast(cardForm.cardNumber.toString())
            }
            else {
                toast("Card not valid")
            }
        }
    }
}
