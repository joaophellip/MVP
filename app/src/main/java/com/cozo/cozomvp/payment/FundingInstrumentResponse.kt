package com.cozo.cozomvp.payment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FundingInstrumentResponse{
    class CreditCard{
        val id: String
        val brand: String
        val first6: String
        val last4: String
        val store: Boolean
        constructor(id: String, brand: String, first6: String, last4: String, store: Boolean){
            this.id = id
            this.brand = brand
            this.first6 = first6
            this.last4 = last4
            this.store = store
        }
    }
    class Card{
        val brand: String
        val store: Boolean
        constructor(brand: String, store: Boolean){
            this.brand = brand
            this.store = store
        }
    }

    var creditCard: CreditCard
    var card: Card
    var method: String

    constructor(id: String, brand: String, first6: String, last4: String, store: Boolean,
                method: String){
        this.method = method
        this.card = Card(brand, store)
        this.creditCard = CreditCard(id, brand, first6, last4, store)
    }
}