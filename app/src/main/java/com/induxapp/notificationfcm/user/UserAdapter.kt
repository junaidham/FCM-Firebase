package com.induxapp.notificationfcm.user

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.induxapp.notificationfcm.R
import com.induxapp.notificationfcm.chat.ClickListenerPos

class UserAdapter (private val modelArrayList : List<UserModel>, private val listener: ClickListenerPos)
    : RecyclerView.Adapter<UserAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = modelArrayList[position]
        holder.tvName.text = model.name
        holder.tvMobile.text = model.mobileNumber

                // click on item
        holder.itemView.setOnClickListener {
            // listener.onRVSelected(model)
            listener.onAdapterItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return modelArrayList.size
    }


    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        //var hardwarePhoto: ImageView = itemView.findViewById(R.id.ivHardware)
        var tvName: AppCompatTextView = itemView.findViewById(R.id.tvName)
        var tvMobile: AppCompatTextView = itemView.findViewById(R.id.tvMobile)
    }




}