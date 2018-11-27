package com.cozo.cozomvp.emptyactivity

import com.cozo.cozomvp.helpers.IdleResourceInterceptor
import com.cozo.cozomvp.paymentapi.PaymentAPIService
import com.cozo.cozomvp.userprofile.ProfileServiceImpl
import com.cozo.cozomvp.userprofile.UserModel
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class EmptyPresenter : MvpBasePresenter<EmptyView>(),
        EmptyInterfaces.Presenter, EmptyInterfaces.ModelListener, ProfileServiceImpl.ProfileServiceListener {

    private lateinit var emptyModel: EmptyModel

    override fun onCreateInvoked(){

        // retrieve payment API authorization token
        emptyModel = EmptyModel(this)
        IdleResourceInterceptor.getInstance().stackCall("EmptyPresenter - onCreateInvoked")
        retrieveAuthorizationToken()
    }

    override fun onComplete(userProfile: UserModel) {
        ifViewAttached {
            it.startMainActivity()
        }
    }

    override fun onError() {
    }

    private fun retrieveAuthorizationToken(){
        val dispose = emptyModel.retrieveAuthorizationToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {authToken ->
                            // storeAuthToken in PaymentAPIService
                            PaymentAPIService.setHeader(authToken.encryptedToken)

                            // check whether a user is logged in app
                            emptyModel.getCurrentUser()
                            IdleResourceInterceptor.getInstance().popCall("EmptyPresenter - retrieveAuthorizationToken")

                        },
                        {

                        }
                )
    }

    override fun userAvailable() {
        // if user is available, go to Main Activity
        ifViewAttached {
            it.startMainActivity()
        }
    }

    override fun noUserAvailable() {
        // if user is not available, go to Auth Activity
        ifViewAttached {
            it.startAuthActivity()
        }
    }
}