package com.example.jlaja

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirstTimeUserHome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_first_time_user_home)
        val username: TextView = findViewById(R.id.Username)
        val user = Firebase.auth.currentUser
        if (user != null) {
            Firebase.firestore.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { data ->
                    if (data.exists()) {
                        username.setText(data.getString("username"))   //get the data with data.getString((what value))
                    }
                }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun toSplitBillMain(view: View?) {
        val intent = Intent(
            this@FirstTimeUserHome,
            SplitBillMain::class.java
        )
        startActivity(intent)
    }


    fun toProfile(view: View) {
        val intent = Intent(
            this@FirstTimeUserHome,
            Profile::class.java
        )
        startActivity(intent)
    }
    fun toChat(view: View) {
        val intent = Intent(
            this@FirstTimeUserHome,
            ChatRoom::class.java
        )
        startActivity(intent)
    }
}