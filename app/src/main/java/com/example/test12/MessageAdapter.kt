package com.example.test12

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.test12.databinding.MessageBinding


class MessageAdapter(var messages: ArrayList<Message>): RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    lateinit var context: Context
    inner class MessageViewHolder(var binding: MessageBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = MessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return MessageViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        if (messages[position].type == MessageType.SENT){
            holder.binding.chatbotCardView.visibility = View.INVISIBLE
            holder.binding.userTextView.text = messages[position].text
        }
        else{
            holder.binding.userCardView.visibility = View.INVISIBLE
            holder.binding.chatbotTextView.text = messages[position].text
        }

    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: Message){
        messages.add(message)
        this.notifyItemInserted(messages.size-1)
    }

}