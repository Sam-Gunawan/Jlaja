package com.example.jlaja

import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class CreateTripDialog(
    private val context: Context,
    private val onTripCreated: (String, List<String>) -> Unit
) {
    fun showDialog() {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_create_trip)

        val tripNameEditText: EditText = dialog.findViewById(R.id.et_trip_name)
        val usernameContainer: LinearLayout = dialog.findViewById(R.id.username_container)
        val addUsernameButton: Button = dialog.findViewById(R.id.btn_add_username)
        val createTripButton: Button = dialog.findViewById(R.id.btn_create_trip)

        val usernames = mutableListOf<EditText>()

        addUsernameButton.setOnClickListener {
            val usernameEditText = EditText(context).apply {
                hint = "Enter Username"
            }

            val verifyTextView = TextView(context).apply {
                text = "Verifying..."
                visibility = View.GONE
            }

            usernameEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val username = s.toString()
                    verifyUser(username) { exists ->
                        verifyTextView.text = if (exists) "User exists" else "User not found"
                        verifyTextView.visibility = View.VISIBLE
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            val usernameLayout = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                addView(usernameEditText, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
                addView(verifyTextView)
            }

            usernameContainer.addView(usernameLayout)
            usernames.add(usernameEditText)
        }

        createTripButton.setOnClickListener {
            val tripName = tripNameEditText.text.toString()
            val userNamesList = usernames.map { it.text.toString() }.filter { it.isNotEmpty() }

            if (tripName.isNotEmpty() && userNamesList.isNotEmpty()) {
                onTripCreated(tripName, userNamesList)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun verifyUser(username: String, callback: (Boolean) -> Unit) {
        FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { callback(it.documents.isNotEmpty()) }
            .addOnFailureListener { callback(false) }
    }
}