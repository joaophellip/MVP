package com.cozo.cozomvp.emptyactivity

import com.cozo.cozomvp.authentication.validationservice.PhoneValidationServiceImpl
import com.cozo.cozomvp.networkapi.APIServices
import com.cozo.cozomvp.networkapi.AuthorizationToken
import io.reactivex.Observable

class EmptyModel(var modelListener: EmptyInterfaces.ModelListener, baseUrl: String? = null) {

    private val serviceAPI : APIServices = APIServices.create(baseUrl)

    fun retrieveAuthorizationToken(): Observable<AuthorizationToken> {
        return serviceAPI.authenticationToken()
    }

    fun getCurrentUser() {
        PhoneValidationServiceImpl.getInstance().isThereALoggedUser(this)
    }

}