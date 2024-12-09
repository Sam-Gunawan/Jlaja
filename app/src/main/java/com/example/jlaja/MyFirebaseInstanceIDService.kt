package com.example.jlaja

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseInstanceIDService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        // Logic to save the token to Firestore or send it to your backend server
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").document(userId)
            userRef.update("fcmToken", token)
                .addOnSuccessListener {
                    Log.d("FCM", "Token updated in Firestore")
                }
                .addOnFailureListener { e ->
                    Log.e("FCM", "Failed to update token in Firestore", e)
                }
        }
    }
}