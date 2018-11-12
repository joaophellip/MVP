package com.cozo.cozomvp.emptyactivity

import android.content.Intent
import android.os.Bundle
import com.cozo.cozomvp.authentication.AuthActivity
import com.cozo.cozomvp.mainactivity.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.hannesdorfmann.mosby3.mvp.MvpActivity

class EmptyActivity : MvpActivity<EmptyView, EmptyPresenter>(), EmptyView {

    override fun createPresenter(): EmptyPresenter = EmptyPresenter(FirebaseAuth.getInstance())

    override fun onBackPressed() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter.onCreatedInvoked()

    }

    override fun startAuthActivity(){
        val mActivityIntent = Intent(this, AuthActivity::class.java)
        startActivity(mActivityIntent)
    }

    override fun startMainActivity(){
        val mActivityIntent = Intent(this, MainActivity::class.java)
        startActivity(mActivityIntent)
    }

}
