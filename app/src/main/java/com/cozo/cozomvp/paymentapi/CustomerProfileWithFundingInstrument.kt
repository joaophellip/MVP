package com.cozo.cozomvp.paymentapi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CustomerProfileWithFundingInstrument {

    class TaxDocument {
        @SerializedName("type")
        @Expose
        val mType: String
        @SerializedName("number")
        @Expose
        val mNumber: String
        constructor(mType: String, mNumber: String){
            this.mNumber = mNumber
            this.mType = mType
        }
    }

    class Phone {
        var countryCode: String
        var areaCode: String
        @SerializedName("number")
        @Expose
        var mNumber:String
        constructor(countryCode: String, areaCode: String, mNumber: String){
            this.countryCode = countryCode
            this.areaCode = areaCode
            this.mNumber = mNumber
        }
    }

    class ShippingAddress(
        var city : String,
        var district: String,
        var street: String,
        var streetNumber: String,
        var zipCode :String,
        var state: String,
        var country: String
    )

    class FundingInstrument{
        class CreditCard{
            class Holder{
                var fullname: String
                var birthdate: String
                var taxDocument: TaxDocument
                var phone: Phone
                constructor(fullname: String, birthdate: String, taxDocument: TaxDocument, phone: Phone){
                    this.fullname = fullname
                    this.birthdate = birthdate
                    this.taxDocument = taxDocument
                    this.phone = phone
                }
            }
            var holder: Holder
            constructor(fullname: String, birthdate: String, taxDocument: TaxDocument, phone: Phone){
                this.holder = Holder(fullname, birthdate, taxDocument, phone)
            }
        }
        var creditCard: CreditCard
        constructor(fullname: String, birthdate: String, taxDocument: TaxDocument, phone: Phone){
            this.creditCard = CreditCard(fullname, birthdate, taxDocument, phone)
        }
    }

    var ownId: String
    var fullname: String
    var email: String
    var birthDate: String
    var taxDocument: CustomerProfileWithFundingInstrument.TaxDocument
    var phone: Phone
    var shippingAddress: ShippingAddress
    var fundingInstrument: FundingInstrument

    constructor(ownId: String, fullname: String, email: String, birthDate: String,
                docType: String, docNumber: String,
                countryCode: String, areaCode: String, phoneNumber: String,
                city: String, district: String, street: String, streetNumber: String, zipCode: String, state: String, country: String){
        this.ownId = ownId
        this.fullname = fullname
        this.email = email
        this.birthDate = birthDate
        this.taxDocument = TaxDocument(docType, docNumber)
        this.phone = Phone(countryCode, areaCode, phoneNumber)
        this.shippingAddress = ShippingAddress(city, district, street, streetNumber, zipCode, state, country)
        this.fundingInstrument = FundingInstrument(fullname, birthDate, taxDocument, phone)
    }
}