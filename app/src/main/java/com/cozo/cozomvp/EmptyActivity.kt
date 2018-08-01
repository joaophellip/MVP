package com.cozo.cozomvp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.cozo.cozomvp.authentication.AuthActivity
import com.cozo.cozomvp.mainActivity.MainActivity
import com.google.firebase.auth.FirebaseAuth

class EmptyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser

        if(user == null) {
            val activityIntent = Intent(this, AuthActivity::class.java)
            Log.d("Teste Nulo", "teste = NULO")
            startActivity(activityIntent)
        } else {
            val activityIntentTest = Intent(this, MainActivity::class.java)
            var userName: String? = null

            for (userInfo in user.providerData) {
                when (userInfo.providerId) {
                    "google.com" -> {
                        userName = userInfo.displayName
                    }
                }
            }
            Log.d("Teste Nulo", "teste != NULO" + user.displayName)
            Log.d("Teste Nulo", "teste != NULO" + userName + "/" + user.phoneNumber)
            startActivity(activityIntentTest)
        }
    }
}
