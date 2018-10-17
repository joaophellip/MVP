package com.cozo.cozomvp.authentication.validationservice

import com.cozo.cozomvp.authentication.AuthModel

interface ValidationService{
    fun signUserIn(signInData: ValidationData, authModel: AuthModel)
    fun linkWithAccount(accountData: ValidationData, authModel: AuthModel)
    fun signUserOut(authModel: AuthModel)
}
