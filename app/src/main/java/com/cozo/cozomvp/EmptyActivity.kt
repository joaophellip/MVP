package com.cozo.cozomvp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.cozo.cozomvp.authentication.AuthActivity
import com.cozo.cozomvp.mainactivity.MainActivity
import com.cozo.cozomvp.userprofile.ProfileServiceImpl
import com.cozo.cozomvp.userprofile.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class EmptyActivity : AppCompatActivity(), ProfileServiceImpl.ProfileServiceListener {

    private val mFirebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onBackPressed() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user : FirebaseUser? = mFirebaseAuth.currentUser

        if(user == null) {
            startAuthActivity()
        }
        else {
            // tries to refresh user data from Firebase servers. Forces user to login when refresh
            // fails, which means either Token is no longer valid or User has been deleted/disabled
            // from DB
            user.reload().addOnCompleteListener { mTask ->
                if (mTask.isSuccessful){
                    //retrieve userProfile from backend
                    user.getIdToken(true).addOnSuccessListener{
                        ProfileServiceImpl.getInstance().loadUserProfile(it.token!!, this)
                    }
                } else {
                    startAuthActivity()
                }
            }
        }
    }

    override fun onComplete(userProfile: UserModel) {
        //start Main Activity
        startMainActivity()
    }

    override fun onError() {}

    private fun startAuthActivity(){
        val mActivityIntent = Intent(this, AuthActivity::class.java)
        startActivity(mActivityIntent)
    }

    private fun startMainActivity(){
        val mActivityIntent = Intent(this, MainActivity::class.java)
        startActivity(mActivityIntent)
    }


}
