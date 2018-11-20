package com.cozo.cozomvp.authentication.validationservice

import com.cozo.cozomvp.authentication.AuthModel
import com.cozo.cozomvp.emptyactivity.EmptyModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GetTokenResult

interface ValidationService{
    fun isThereALoggedUser(emptyModel: EmptyModel)
    fun getCurrentToken() : Task<GetTokenResult>
    fun linkWithAccount(accountData: ValidationData, authModel: AuthModel)
    fun signUserIn(signInData: ValidationData, authModel: AuthModel)
    fun signUserOut(authModel: AuthModel)
}
