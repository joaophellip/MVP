package com.cozo.cozomvp.authentication

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.cozo.cozomvp.R
import com.cozo.cozomvp.mainactivity.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import kotlinx.android.synthetic.main.activity_auth.*
import org.jetbrains.anko.toast

class AuthActivity : MvpActivity<AuthView, AuthPresenter>(), AuthView {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth
    private lateinit var task: Task<GoogleSignInAccount>
    private lateinit var phoneNumber: String
    val RC_SIGN_IN: Int = 123
    val TAG: String = "Authentication"

    override fun createPresenter(): AuthPresenter {
        return AuthPresenter()
    }

    override fun onBackPressed() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mAuth = FirebaseAuth.getInstance()

        authButton.setOnClickListener{
            val phoneNumber = phoneNumberEditText.text.toString()
            if (phoneNumber != "") {
                presenter.requestAuth(phoneNumber)
            } else{
                showInvalidNumber()
            }
        }
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun showError() {
        progressBar.visibility = View.INVISIBLE
        subTitleText.text = "We're sorry, a problem occurred. Please try again"
    }

    override fun showInvalidNumber() {
        progressBar.visibility = View.INVISIBLE
        subTitleText.text = "Invalid Number. Please try again"
    }

    override fun showAuthenticationFail() {
        progressBar.visibility = View.INVISIBLE
        subTitleText.text = "Authentication Failed. Please try again"
    }

    override fun showLinkFail() {
        progressBar.visibility = View.INVISIBLE
        titleText.text = "Tell us who you are"
        subTitleText.text = "We need you to choose a google account for safety reasons. Please try again"

    }

    override fun showMainActivity() {
        val activityIntent = Intent (this,MainActivity::class.java)
        activityIntent.putExtra("IS_FIRST_TIME_LOGGED", true)
        startActivity(activityIntent)
        finish()
    }

    override fun showLogOffActivity() {
        progressBar.visibility = View.INVISIBLE
        subTitleText.text = "you're logged off"
    }

    override fun showAuthAndLinkedCompletedActivity() {

        val providerData: MutableList<out UserInfo> = mAuth.currentUser!!.providerData
        val mMapUserInfo: MutableMap<String, UserInfo> = mutableMapOf()

        providerData.forEach {
            mMapUserInfo[it.providerId] = it
        }

        progressBar.visibility = View.INVISIBLE
        titleText.text = "Bem Vindo " + mMapUserInfo["google.com"]!!.displayName
        subTitleText.text = "Seu telefone é: " + mMapUserInfo["phone"]!!.phoneNumber
    }

    override fun startLinkWithGoogle() {
        signIn()
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            task = GoogleSignIn.getSignedInAccountFromIntent(data)
            presenter.requestLinkWithGoogle(task)
        }
        else {
            toast("Não funciona assim")
        }
    }

}
