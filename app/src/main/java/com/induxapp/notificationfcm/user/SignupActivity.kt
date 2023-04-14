package com.induxapp.notificationfcm.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.induxapp.notificationfcm.R
import com.induxapp.notificationfcm.chat.ChatAppConstant
import java.util.HashMap

class SignupActivity : AppCompatActivity() {
    var btnSubmit : MaterialButton? =null
    var editMobile : TextInputEditText? =null
    var editName : TextInputEditText? =null

    private var mRootReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        btnSubmit = findViewById(R.id.btnSubmit)
        editMobile = findViewById(R.id.editMobile)
        editName = findViewById(R.id.editName)

        //Initialization
        mRootReference = FirebaseDatabase.getInstance().reference


        btnSubmit?.setOnClickListener {


            // TableName +  Group_Id
            val current_user_ref: String = ChatAppConstant.TABLE_USER
            val user_message_push: DatabaseReference? = mRootReference?.child(ChatAppConstant.TABLE_USER)?.push()
            val push_id = editMobile?.text.toString()
            val messageMap: HashMap<String, Any> = HashMap()


            messageMap["mobileNumber"] = editMobile?.text.toString()
            messageMap["name"] = editName?.text.toString()
            val messageUserMap: HashMap<String, Any> = HashMap()
            messageUserMap["$current_user_ref/$push_id"] = messageMap
            mRootReference?.updateChildren(messageUserMap,
                DatabaseReference.CompletionListener { databaseError, databaseReference ->
                    if (databaseError != null) {
                        Log.e("SignupActivity", "Cannot add message to database")
                    } else {
                        editMobile?.setText("")
                        editName?.setText("")

                        Log.e("SignupActivity", "database push")

                        startActivity(Intent(this, UserListActivity::class.java))
                        //finish()
                    }
                })
        }
    }


}