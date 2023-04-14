package com.induxapp.notificationfcm


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.induxapp.notificationfcm.chat.ChatActivity
import com.induxapp.notificationfcm.user.SignupActivity
import com.induxapp.notificationfcm.user.UserListActivity


class MainActivity : AppCompatActivity() {
    var regid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        //generate Device Token for send Message
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task: Task<String> ->
                if (!task.isSuccessful) {
                    //Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                    return@addOnCompleteListener
                }
                // Get new FCM registration token
                regid = task.result
                Log.e("MainActivity","regid : $regid" )
               // updateDeviceToken()
            }


        findViewById<Button>(R.id.btnChat).setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        findViewById<Button>(R.id.btnSignup).setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        findViewById<Button>(R.id.btnUserList).setOnClickListener {
            startActivity(Intent(this, UserListActivity::class.java))
        }


    }




}