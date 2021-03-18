package com.example.test12


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test12.databinding.ActivityTestBinding

//TODO: Delete this activity once you finish testing the recyclerview
class TestActivity : AppCompatActivity() {
    lateinit var binding: ActivityTestBinding
    val adapter = MessageAdapter(ArrayList<Message>())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvMessages.adapter = this.adapter
        val layoutManager = LinearLayoutManager(binding.rvMessages.context, LinearLayoutManager.VERTICAL, false)
        binding.rvMessages.layoutManager = layoutManager
        binding.rvMessages.addItemDecoration(DividerItemDecoration(binding.rvMessages.context, layoutManager.orientation))
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