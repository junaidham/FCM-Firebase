package com.induxapp.notificationfcm.chat



import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.induxapp.notificationfcm.R
import java.util.*



class ChatAdapter(private val mActivity: Activity, private val mMessagesList: List<Messages>,  private val userId: String)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private val VIEW_TYPE_FILE_MESSAGE_IMAGE_ME = 22
    private val VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER = 23
    private val VIEW_TYPE_USER_MESSAGE_ME = 10
    private val VIEW_TYPE_USER_MESSAGE_OTHER = 11



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            VIEW_TYPE_USER_MESSAGE_ME -> {
                val myUserMsgView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_me, parent, false)
                MyUserMessageHolder(myUserMsgView)
            }
            VIEW_TYPE_USER_MESSAGE_OTHER -> {
                val otherUserMsgView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_other, parent, false)
                OtherUserMessageHolder(otherUserMsgView)
            }
            VIEW_TYPE_FILE_MESSAGE_IMAGE_ME -> {
                val myImageFileMsgView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_image_me, parent, false)
                MyImageMessageHolder(myImageFileMsgView)
            }
            VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER -> {
                val otherImageFileMsgView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_image_other, parent, false)
                OtherUserImageMessageHolder(otherImageFileMsgView)
            }

            else -> {
                val myUserMsgView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_me, parent, false)
                MyUserMessageHolder(myUserMsgView)
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message: Messages = mMessagesList[position]
        when (holder.itemViewType) {
            VIEW_TYPE_USER_MESSAGE_ME -> (holder as MyUserMessageHolder).bind(mActivity, message)
            VIEW_TYPE_USER_MESSAGE_OTHER -> (holder as OtherUserMessageHolder).bind(
                mActivity,
                message
            )
            VIEW_TYPE_FILE_MESSAGE_IMAGE_ME -> (holder as MyImageMessageHolder).bind(
                mActivity,
                message
            )
            VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER -> (holder as OtherUserImageMessageHolder).bind(
                mActivity,
                message
            )
            else -> {}
        }
    }


    override fun getItemViewType(position: Int): Int {
        val message: Messages = mMessagesList[position]
        if (message.getType().equals("text") || message.getType()
                .equals("new")
        ) {
            val userMessage: Messages = message as Messages
            // If the sender is current user
            //return if (!userMessage.getFrom().equals(userId)) {
            return if (!userMessage.getSendId().equals(userId)) {
                VIEW_TYPE_USER_MESSAGE_OTHER
            } else {
                VIEW_TYPE_USER_MESSAGE_ME
            }
        } else if (message.getType().equals("image")) {
            val userMessage: Messages = message as Messages
            //return if (!userMessage.getFrom().equals(userId)) {
            return if (!userMessage.getSendId().equals(userId)) {
                VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER
            } else {
                VIEW_TYPE_FILE_MESSAGE_IMAGE_ME
            }
        }

       /* else if (message.getType().equalsIgnoreCase("time")) {
            val userMessage: Messages = message as Messages
            return VIEW_TYPE_TIMEMESSAGE
        }*/
        return -1
    }

    override fun getItemCount(): Int {
        return mMessagesList.size
    }



    private class MyUserMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageText: AppCompatTextView
        var timeText: AppCompatTextView

        init {
            setIsRecyclable(false)
            messageText = itemView.findViewById<View>(R.id.tvMsg) as AppCompatTextView
            timeText = itemView.findViewById<View>(R.id.tvDateTime) as AppCompatTextView
        }

        fun bind(context: Context?, mes: Messages) {
            //val from_user_id: String = mes.getFrom()
            val from_user_id: String = mes.getSendId()
            setIsRecyclable(false)
            val message_type = mes.getType()
            val timeStamp: Long = mes.getDateTime()
            val calendar: Calendar = GregorianCalendar.getInstance()
            calendar.setTimeInMillis(timeStamp)
            val cal: List<String> = calendar.getTime().toString().split(" ")
            val time_of_message = cal[1] + "," + cal[2] + "  " + cal[3].substring(0, 5)
            //            String time_of_message = cal[3].substring(0,5);
            Log.d("TIME IS : ", calendar.getTime().toString())
            timeText.text = time_of_message
            messageText.text = mes.getMessage()
        }
    }


    private class OtherUserMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageText: AppCompatTextView
        var timeText: AppCompatTextView

        init {
            setIsRecyclable(false)
            messageText = itemView.findViewById<View>(R.id.tv_receive_msg) as AppCompatTextView
            timeText = itemView.findViewById<View>(R.id.tvDateTime) as AppCompatTextView
        }

        fun bind(context: Context?, mes: Messages) {
            //val from_user_id: String = mes.getFrom()
            val from_user_id: String = mes.getSendId()
            setIsRecyclable(false)
            val message_type = mes.getType()
            val timeStamp: Long = mes.getDateTime()
            val calendar: Calendar = GregorianCalendar.getInstance()
            calendar.setTimeInMillis(timeStamp)
            val cal: List<String> = calendar.getTime().toString().split(" ")
            val time_of_message = cal[1] + "," + cal[2] + "  " + cal[3].substring(0, 5)
            //            String time_of_message = cal[3].substring(0,5);
            Log.d("TIME IS : ", calendar.getTime().toString())
            timeText.text = time_of_message
            messageText.text = mes.getMessage()
        }
    }



    private class MyImageMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageText: AppCompatTextView
        var timeText: AppCompatTextView
        var imageView: AppCompatImageView

        init {
            setIsRecyclable(false)
            messageText = itemView.findViewById<View>(R.id.tvMsg) as AppCompatTextView
            timeText = itemView.findViewById<View>(R.id.tvDateTime) as AppCompatTextView
            imageView = itemView.findViewById<View>(R.id.imageView) as AppCompatImageView
        }

        fun bind(context: Context?, mes: Messages) {
            //val from_user_id: String = mes.getFrom()
            val from_user_id: String = mes.getSendId()
            setIsRecyclable(false)
            val message_type = mes.getType()
            val timeStamp: Long = mes.getDateTime()
            val calendar: Calendar = GregorianCalendar.getInstance()
            calendar.setTimeInMillis(timeStamp)
            val cal: List<String> = calendar.getTime().toString().split(" ")
            val time_of_message = cal[1] + "," + cal[2] + "  " + cal[3].substring(0, 5)
            //            String time_of_message = cal[3].substring(0,5);
            Log.d("TIME IS : ", calendar.getTime().toString())
            timeText.text = time_of_message
            //messageText.text = mes.getMessage()


            Glide.with(context!!).load(mes.getMessage()).into(imageView)

        }
    }


    private class OtherUserImageMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageText: AppCompatTextView
        var timeText: AppCompatTextView
        var imageView: AppCompatImageView

        init {
            setIsRecyclable(false)
            messageText = itemView.findViewById<View>(R.id.tvMsg) as AppCompatTextView
            timeText = itemView.findViewById<View>(R.id.tvDateTime) as AppCompatTextView
            imageView = itemView.findViewById<View>(R.id.imageView) as AppCompatImageView
        }

        fun bind(context: Context?, mes: Messages) {
            //val from_user_id: String = mes.getFrom()
            val from_user_id: String = mes.getSendId()
            setIsRecyclable(false)
            val message_type = mes.getType()
            val timeStamp: Long = mes.getDateTime()
            val calendar: Calendar = GregorianCalendar.getInstance()
            calendar.setTimeInMillis(timeStamp)
            val cal: List<String> = calendar.getTime().toString().split(" ")
            val time_of_message = cal[1] + "," + cal[2] + "  " + cal[3].substring(0, 5)
            //            String time_of_message = cal[3].substring(0,5);
            Log.d("TIME IS : ", calendar.getTime().toString())
            timeText.text = time_of_message
            messageText.text = mes.getMessage()
        }
    }



}