package com.cozo.cozomvp.paymentapi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FundingInstrument{
    class CreditCard{
        val expirationMonth: String
        val expirationYear: String
        @SerializedName("number")
        @Expose
        val mNumber: String
        val cvc: String
        class Holder{
            var fullname: String
            var birthdate: String
            var taxDocument: CustomerProfile.TaxDocument
            var phone: CustomerProfile.Phone
            constructor(fullname: String, birthdate: String, taxDocument: CustomerProfile.TaxDocument, phone: CustomerProfile.Phone){
                this.fullname = fullname
                this.birthdate = birthdate
                this.taxDocument = taxDocument
                this.phone = phone
            }
        }
        var holder: Holder
        constructor(expirationMonth: String, expirationYear: String, mNumber: String, cvc: String,
                    fullname: String, birthdate: String,
                    taxDocument: CustomerProfile.TaxDocument,
                    phone: CustomerProfile.Phone){
            this.expirationMonth = expirationMonth
            this.expirationYear = expirationYear
            this.mNumber = mNumber
            this.cvc = cvc
            this.holder = Holder(fullname, birthdate, taxDocument, phone)
        }
    }

    var method: String
    var creditCard: CreditCard

    constructor(method: String,
                expirationMonth: String, expirationYear: String, mNumber: String, cvc: String,
                fullname: String, birthdate: String,
                taxDocument: CustomerProfile.TaxDocument,
                phone: CustomerProfile.Phone){
        this.method = method
        this.creditCard = CreditCard(expirationMonth, expirationYear, mNumber, cvc, fullname, birthdate, taxDocument, phone)
    }
}