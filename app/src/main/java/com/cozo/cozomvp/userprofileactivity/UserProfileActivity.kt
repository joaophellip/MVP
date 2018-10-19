package com.cozo.cozomvp.userprofileactivity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.cozo.cozomvp.R
import com.cozo.cozomvp.mainactivity.listfragment.recyclerview.ImageDownload
import com.cozo.cozomvp.userprofile.ProfileServiceImpl
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
    }

    override fun onStart() {
        super.onStart()
        val userProfile = ProfileServiceImpl.getInstance().getUserProfile()
        userName.text = userProfile?.name
        userEmail.text = userProfile?.email
        phoneNumber.text = getString(R.string.phone_number, userProfile?.phone?.countryCode, userProfile?.phone?.areaCode, userProfile?.phone?.phoneNumber)

        // launch asynchronous process to download image
        if(userProfile?.avatarUrl != null){
            ImageDownload(this.applicationContext, userPicture, userProfile.avatarUrl!!)
        }

    }

}