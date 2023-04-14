package com.induxapp.notificationfcm.chat


import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.induxapp.notificationfcm.R
import java.util.*


class ChatActivity : AppCompatActivity() {
    lateinit var editChat: TextInputEditText
    lateinit var uploadIv: ImageView
    lateinit var sendIv: ImageView
    lateinit var recyclerView: RecyclerView
    lateinit var messageAdapter: ChatAdapter
    var fileUri: Uri? = null;

    var itemPos : Int = 0
    var mLastKey : String = ""
    var mPrevKey : String = ""
    var roomId : String = "123456"
    var userId : String = "123"

    lateinit var mLinearLayoutManager: LinearLayoutManager

    private var mRootReference: DatabaseReference? = null
    var mImageStorage: FirebaseStorage? = null
    var storageRef: StorageReference? = null
    var childListener: ChildEventListener? = null
    private var messagesList: ArrayList<Messages> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        editChat = findViewById<TextInputEditText>(R.id.editChat)
        uploadIv = findViewById<ImageView>(R.id.imgUpload)
        sendIv = findViewById<ImageView>(R.id.imgSend)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        mRootReference = FirebaseDatabase.getInstance().reference
        mImageStorage = FirebaseStorage.getInstance();
        messagesList = ArrayList()

        //messageAdapter = ChatAdapter(this, messagesList,  userId, listener1)
        messageAdapter = ChatAdapter(this, messagesList, userId)
        mLinearLayoutManager = LinearLayoutManager(this@ChatActivity)
        recyclerView.layoutManager = mLinearLayoutManager
        recyclerView.adapter = messageAdapter
//        setMessageInputEnabled(true);
        //        setMessageInputEnabled(true);
        recyclerView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int,
                oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                if (bottom < oldBottom) {
                    mLinearLayoutManager.smoothScrollToPosition(recyclerView, null,
                        messageAdapter.getItemCount()
                    )
                }
            }
        })


        loadMessages()

        sendIv.setOnClickListener {
            sendMessage("text",editChat.text.toString())
        }

        uploadIv.setOnClickListener {
            // on below line calling intent to get our image from phone storage.
            val intent = Intent()
            // on below line setting type of files which we want to pick in our case we are picking images.
            intent.type = "image/*"
            // on below line we are setting action to get content
            intent.action = Intent.ACTION_GET_CONTENT
            // on below line calling start activity for result to choose image.
            startActivityForResult(
                // on below line creating chooser to choose image.
                Intent.createChooser(
                    intent,
                    "Pick your image to upload"
                ),
                22
            )
        }

    }


    /**
     * upload the Photo/video to server/FCM FirebaseStorage
     * on below line creating a function to upload our image.
     */
    fun uploadImage() {
        val progressDialog = ProgressDialog(this)
        // on below line setting title and message for our progress dialog and displaying our progress dialog.
        progressDialog.setTitle("Uploading...")
        progressDialog.setMessage("Uploading your image..")
        progressDialog.show()
        val firebaseStorage = FirebaseStorage.getInstance()
        val storageRefPhoto =  firebaseStorage.reference.child(UUID.randomUUID().toString())
        val uploadTask: UploadTask = storageRefPhoto.putFile(fileUri!!)
        uploadTask.addOnFailureListener {
            progressDialog.dismiss()
            Toast.makeText(applicationContext, "Image Uploaded fail..", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            storageRefPhoto.getDownloadUrl().addOnSuccessListener(
                OnSuccessListener<Uri?> {
                    val taskMap: Map<String, Any> = HashMap()
                    sendMessage("image",it.toString())
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "Image Uploaded..", Toast.LENGTH_SHORT).show()
                })
        }
        // on below line checking weather our file uri is null or not.
      /*  if (fileUri != null) {
            // on below line displaying a progress dialog when uploading an image.
            val progressDialog = ProgressDialog(this)
            // on below line setting title and message for our progress dialog and displaying our progress dialog.
            progressDialog.setTitle("Uploading...")
            progressDialog.setMessage("Uploading your image..")
            progressDialog.show()

            // on below line creating a storage refrence for firebase storage and creating a child in it with
            // random uuid.
            val firebaseStorage = FirebaseStorage.getInstance()
            val storageRefPhoto =  firebaseStorage.reference
                //.child("photos")
                .child(UUID.randomUUID().toString())
            // on below line adding a file to our storage.
            storageRefPhoto.putFile(fileUri!!).addOnSuccessListener {
                // this method is called when file is uploaded.
                // in this case we are dismissing our progress dialog and displaying a toast message

                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                val downloadUrl = storageRefPhoto.downloadUrl
                //val downloadUrl = storageRefPhoto.downloadUrl

                val imgURL = downloadUrl.toString()
                val name = storageRefPhoto.name
                val root = storageRefPhoto.root
                Log.e("StorageReference", "imgURL: $imgURL")
                Log.e("StorageReference", "name: $name")
                Log.e("StorageReference", "root: $root")
                sendMessage("image",downloadUrl.result.toString())


                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Image Uploaded..", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                // this method is called when there is failure in file upload.
                // in this case we are dismissing the dialog and displaying toast message
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Fail to Upload Image..", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(applicationContext, "Getting null file", Toast.LENGTH_LONG).show();
        }*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // on below line we are checking if the result is ok
        if (requestCode == 22 && resultCode == RESULT_OK && data != null && data.data != null) {
            // on below line initializing file uri with the data which we get from intent
            fileUri = data.data
            try {
                Log.e("StorageReference", "File: ${data.data}")
                // on below line getting bitmap for image from file uri.
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fileUri);
                // on below line setting bitmap for our image view.
               // imageView.setImageBitmap(bitmap)
                uploadImage()
            } catch (e: Exception) {
                // handling exception on below line
                e.printStackTrace()
            }
        }
    }


    /**
     * Display the sent the messages
     */
    private fun loadMessages() {
        // TableName +  Group_Id
        val messageRef = mRootReference!!.child(ChatAppConstant.TABLE_NAME).child(roomId)
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
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })
        childListener = messageRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val messages = dataSnapshot.getValue(Messages::class.java) as  Messages
                itemPos++
                if (itemPos === 1) {
                    val mMessageKey = dataSnapshot.key
                    if (mMessageKey != null) {
                        mLastKey = mMessageKey
                    }
                    if (mMessageKey != null) {
                        mPrevKey = mMessageKey
                    }
                }

                messages.setKey(dataSnapshot.key)
                if (messages!!.getType().equals("new", ignoreCase = true)) {

                } else {

                    messagesList.add(messages)
                }
            /*    if (messages.getSendId().equals(userId)) {
                } else {
                    mRootReference!!.child(ChatAppConstant.TABLE_NAME).child(roomId)
                        .child(dataSnapshot.key!!).child("isReadOther").setValue(2)
                }*/
                if (messagesList.size == 1) {
                } else {
                }
                messageAdapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(messagesList.size - 1)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.value != null) {
                    val messages = dataSnapshot.getValue(
                        Messages::class.java
                    )
                    if (!messages?.getSendId().equals(userId)) {
                        val key = dataSnapshot.key
                        var index = -1
                        for (i in messagesList.indices) {
                            if (key.equals(messagesList[i].getKey(), ignoreCase = true)) {
                                index = i
                                break
                            }
                        }
                        messages?.setKey(dataSnapshot.key)
                        if (messagesList.size == 0) {
                            if (messages != null) {
                                messagesList.add(messages)
                            }
                        } else {
                            if (index == -1) {
                                //  messagesArr.set(0, messages);
                            } else {
                                if (messages != null) {
                                    messagesList.set(index, messages)
                                }
                            }
                        }
                        messageAdapter.notifyDataSetChanged()
                        recyclerView.scrollToPosition(messagesList.size - 1)
                    }
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    /**
     * send the messages to server/ FCM
     */
    fun sendMessage(type: String, message: String) {
        // TableName +  Group_Id
        val current_user_ref: String = ChatAppConstant.TABLE_NAME + "/" + roomId
        val user_message_push: DatabaseReference? = mRootReference?.child(ChatAppConstant.TABLE_NAME)?.child( roomId)?.push()
        val push_id = user_message_push?.key
        val messageMap: HashMap<String, Any> = HashMap()


        messageMap["message"] = message
        messageMap["type"] = type
        messageMap["dateTime"] = ServerValue.TIMESTAMP
        messageMap["sendId"] = userId // user's ID
        val messageUserMap: HashMap<String, Any> = HashMap()
        messageUserMap["$current_user_ref/$push_id"] = messageMap
        mRootReference?.updateChildren(messageUserMap,
            DatabaseReference.CompletionListener { databaseError, databaseReference ->
                if (databaseError != null) {
                    Log.e("CHAT_ACTIVITY", "Cannot add message to database")
                } else {
                    editChat.setText("")
                }
            })
    }


}