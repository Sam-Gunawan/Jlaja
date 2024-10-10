package com.example.jlaja

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.jlaja.ui.theme.JlajaTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : ComponentActivity() {
    //    private var auth : FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        var logInEmail: EditText = findViewById(R.id.logInEmail)
        var logInPassword: EditText = findViewById(R.id.logInPassword)
        val logInButton: Button = findViewById(R.id.logInButton)



        logInButton.setOnClickListener{
            signIn(logInEmail.text.toString(),logInPassword.text.toString())
//            if (logInEmail.text.isEmpty()){
//                logInEmail.setText("ok")
//            }
        }

    }
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            setContentView(R.layout.home)
        }
    }

    fun signIn(email: String,password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    setContentView(R.layout.home)
                } else {
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    fun SignUp(email: String,password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    setContentView(R.layout.home)
                } else {
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    fun toSignIn(view: View){
        setContentView(R.layout.sign_in)
        var signInEmail: EditText = findViewById(R.id.signInEmail)
        var signInPassword: EditText = findViewById(R.id.signInPassword)
        val signInButton: Button = findViewById(R.id.signInButton)

        signInButton.setOnClickListener{
            signIn(signInEmail.text.toString(),signInPassword.text.toString())
        }

    }
    fun toLogIn(view: View){
        setContentView(R.layout.login)
    }

}
