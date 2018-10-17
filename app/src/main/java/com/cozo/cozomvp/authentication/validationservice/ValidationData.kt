package com.cozo.cozomvp.authentication.validationservice

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

open class ValidationData{

    constructor(phoneNumber : String){
        this.phoneNumber = phoneNumber
    }
    constructor(account: GoogleSignInAccount){
        this.account = account
    }

    lateinit var phoneNumber : String
    lateinit var account : GoogleSignInAccount
}
