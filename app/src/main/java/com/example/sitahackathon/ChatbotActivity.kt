package com.example.sitahackathon

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sitahackathon.databinding.ActivityChatbotBinding

class ChatbotActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatbotBinding
    val adapter = MessageAdapter(ArrayList<Message>())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvMessages.adapter = this.adapter
        val layoutManager = LinearLayoutManager(binding.rvMessages.context, LinearLayoutManager.VERTICAL, false)
        binding.rvMessages.layoutManager = layoutManager
        binding.btnAddMessage.setOnClickListener {
            if (binding.etText.toString().trim()!="") {
                val message = Message(binding.etText.text.toString().trim(), MessageType.SENT)
                this.adapter.addMessage(message)
            }
        }

        binding.btnAddReply.setOnClickListener {
            if (binding.etText.toString().trim()!="") {
                val message = Message(binding.etText.text.toString().trim(), MessageType.RECEIVED)
                this.adapter.addMessage(message)
            }
        }




    }
}