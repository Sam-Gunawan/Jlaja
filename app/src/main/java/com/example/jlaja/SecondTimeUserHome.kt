package com.example.jlaja

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SecondTimeUserHome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second_time_user_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    fun toChatViewModel(view: View?) {
        val intent = Intent(
            this@SecondTimeUserHome,
            GeminiChatbot::class.java
        )
        startActivity(intent)
    }

    fun toSplitBillMain(view: View?) {
        val intent = Intent(
            this@SecondTimeUserHome,
            SplitBillMain::class.java
        )
        startActivity(intent)
    }
}