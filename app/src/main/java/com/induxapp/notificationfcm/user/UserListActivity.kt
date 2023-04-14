package com.induxapp.notificationfcm.user

import android.app.Activity
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.induxapp.notificationfcm.R
import com.induxapp.notificationfcm.chat.ChatAppConstant
import com.induxapp.notificationfcm.chat.ClickListenerPos

import java.util.ArrayList

class UserListActivity : AppCompatActivity(), ClickListenerPos {
    private val TAG = javaClass.simpleName
    private var mContext: Activity? = null
    private var mRecyclerView: RecyclerView?= null
    private var mAdapter: UserAdapter?= null

    private var mRootReference: DatabaseReference? = null
    var childListener: ChildEventListener? = null
    var myList : ArrayList<UserModel>  = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)
        mContext = this@UserListActivity
        mRecyclerView = findViewById(R.id.userRecyclerView)

        mRootReference = FirebaseDatabase.getInstance().reference
//        mImageStorage = FirebaseStorage.getInstance();
//        messagesList = ArrayList()

        val verticalManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        mRecyclerView?.layoutManager = verticalManager
        mAdapter = UserAdapter(myList, this)
        mRecyclerView?.adapter = mAdapter
        loadUsers()



    }

    private fun loadUsers() {
        // TableName +  Group_Id
        val messageRef = mRootReference!!.child(ChatAppConstant.TABLE_USER)
        messageRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value == null) {

                }
                for (postSnapshot in dataSnapshot.children) {
                    // TODO: handle the post
                    val aa = "aa"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                val aa = "aa"
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })
        childListener = messageRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val messages = dataSnapshot.getValue(UserModel::class.java) as UserModel
            /*    itemPos++
                if (itemPos === 1) {
                    val mMessageKey = dataSnapshot.key
                    if (mMessageKey != null) {
                        mLastKey = mMessageKey
                    }
                    if (mMessageKey != null) {
                        mPrevKey = mMessageKey
                    }
                }*/

                messages.setKey(dataSnapshot.key)


                myList.add(messages)


                mAdapter?.notifyDataSetChanged()
                mRecyclerView?.scrollToPosition(myList.size - 1)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.value != null) {
                    val messages = dataSnapshot.getValue(
                        UserModel::class.java
                    )

                        val key = dataSnapshot.key
                        var index = -1
                        for (i in myList.indices) {
                            if (key.equals(myList[i].getKey(), ignoreCase = true)) {
                                index = i
                                break
                            }
                        }
                        messages?.setKey(dataSnapshot.key)
                        if (myList.size == 0) {
                            if (messages != null) {
                                myList.add(messages)
                            }
                        } else {
                            if (index == -1) {
                                //  messagesArr.set(0, messages);
                            } else {
                                if (messages != null) {
                                    myList.set(index, messages)
                                }
                            }
                        }
                        mAdapter?.notifyDataSetChanged()
                        mRecyclerView?.scrollToPosition(myList.size - 1)

                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }



    override fun onAdapterItemClick(position: Int) {

    }




}