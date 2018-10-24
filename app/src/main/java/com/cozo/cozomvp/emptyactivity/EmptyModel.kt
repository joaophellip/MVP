package com.cozo.cozomvp.emptyactivity

import com.cozo.cozomvp.networkapi.APIServices
import com.cozo.cozomvp.networkapi.AuthorizationToken
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Observable

class EmptyModel(firebaseAuth: FirebaseAuth, baseUrl: String? = null) {

    private val serviceAPI : APIServices = APIServices.create(baseUrl)

    fun retrieveAuthorizationToken(): Observable<AuthorizationToken> {
        return serviceAPI.authenticationToken()
    }


}