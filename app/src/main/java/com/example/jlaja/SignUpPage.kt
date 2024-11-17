package com.example.jlaja

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SignUpPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    fun toLoginPage(view: View?) {
        val intent = Intent(
            this@SignUpPage,
            LoginPage::class.java
        )
        startActivity(intent)
    }

    fun toFirstTimeUserHome(view: View?) {
        val intent = Intent(
            this@SignUpPage,
            FirstTimeUserHome::class.java
        )
        startActivity(intent)
    }
}