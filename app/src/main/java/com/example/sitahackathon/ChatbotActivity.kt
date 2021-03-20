package com.example.sitahackathon

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sitahackathon.data.BotMessage
import com.example.sitahackathon.databinding.ActivityChatbotBinding
import com.example.sitahackathon.util.BotResponse
import com.example.sitahackathon.util.Constants.open_google

class ChatbotActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatbotBinding
    val adapter = MessageAdapter(ArrayList<Message>())
    private val botList = listOf("Peter", "Francesca", "Luigi", "Igor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvMessages.adapter = this.adapter
        val layoutManager = LinearLayoutManager(binding.rvMessages.context, LinearLayoutManager.VERTICAL, false)
        binding.rvMessages.layoutManager = layoutManager
        binding.btnSend.setOnClickListener {
            if (binding.etText.toString().trim()!="") {
                binding.etText.error = null
                val message = Message(binding.etText.text.toString().trim(), MessageType.SENT)
                adapter.addMessage(message)
                BotResponse.basicResponses(message.text, this)
            }
            else{
                binding.etText.error = "Text can't be empty"
            }
        }

    }

    fun onBotReply(reply: String){
        adapter.addMessage(Message(reply, MessageType.RECEIVED))
        //TODO: if reply contains something that the user can interact with, add a listener for it here (to open Uber)
        when(reply){
            open_google -> {
                Toast.makeText(this, "Opening google", Toast.LENGTH_SHORT).show()
            }
            else -> {

            }
        }



    }
}