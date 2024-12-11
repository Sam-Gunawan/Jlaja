package com.example.jlaja

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONArray
import org.json.JSONException

class FirstTimeUserHome : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var checkboxcontainersecond: LinearLayout
    private val KEY_CHECKBOXES = "key_checkboxes"


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

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)


        //load previous Checkbox
        loadcheckbox();
    }

    fun toSplitBillMain(view: View?) {
        val intent = Intent(
            this@FirstTimeUserHome,
            SplitBillMain::class.java
        )
        startActivity(intent)
    }


    fun tochecklist2(view: View?) {
        val intent = Intent(
            this@FirstTimeUserHome,
            checklist2::class.java
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




    @Throws(JSONException::class)
    private fun loadcheckbox() {
        val savedData: String = sharedPreferences.getString(KEY_CHECKBOXES, null).toString()

        //        if (savedData != null) {
//            try {
//                JSONArray jsonArray = new JSONArray(savedData);
//
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONArray checkBoxData = jsonArray.getJSONArray(i);
//                    String text = checkBoxData.getString(0);
//                    boolean isChecked = checkBoxData.getBoolean(1);
//
//                    addcheckbox();
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }




//        if (savedData == null) {
//            Log.d("SavedData", "No data saved in SharedPreferences")
//            return  // Exit if there's no data
//        } else {
//            Log.d("SavedData", "Retrieved JSON: $savedData")
//        }
//
//        val jsonArray = JSONArray(savedData)
//
//        for (i in 0 until jsonArray.length()) {
//            // Extract each CheckBox data
//            val checkBoxData = jsonArray.optJSONArray(i) // Safe access to array
//            if (checkBoxData != null && checkBoxData.length() == 2) {
//                val text = checkBoxData.optString(0, "") // Default empty text if missing
//                val isChecked = checkBoxData.optBoolean(1, false) // Default unchecked
//
//                // Add the CheckBox to the layout
//                addcheckbox2(text)
//            } else {
//                Log.e("LoadCheckBoxes", "Invalid CheckBox data at index $i")
//            }
//        }


        try {
            val jsonArray = JSONArray(savedData)

            for (i in 0 until jsonArray.length()) {
                val checkBoxData = jsonArray.optJSONArray(i)
                if (checkBoxData != null && checkBoxData.length() == 2) {
                    val text = checkBoxData.optString(0, "")
                    val isChecked = checkBoxData.optBoolean(1, false)
                    addCheckBox(text)
                } else {
                    Log.e("LoadCheckBoxes", "Invalid CheckBox data at index $i")
                }
            }
        } catch (e: Exception) {
            Log.e("LoadCheckBoxes", "Unexpected error parsing saved data: ${e.message}")
        }
    }


    private fun addCheckBox(text: String) {
        val checkBox = CheckBox(this).apply {
            this.text = text
//                this.isChecked = isChecked
        }
        checkboxcontainersecond.addView(checkBox)
    }
}