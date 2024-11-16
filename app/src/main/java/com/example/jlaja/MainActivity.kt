package com.example.jlaja

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class MainActivity : ComponentActivity() {
    //    private var auth : FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        initLogIn()

    }
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                checkEmailVerification(user)
            }

        }

        auth.addAuthStateListener(authListener)
    }

    fun signIn(email: String,password: String){
        logOutHere()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                Toast.makeText(applicationContext, "Verification email sent!", Toast.LENGTH_SHORT).show()
                                checkEmailVerification(user)
                            } else {
                                Toast.makeText(applicationContext, "Verification email error!", Toast.LENGTH_SHORT).show()
                                logOutHere()                            }
                        }
                } else {
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    fun logIn(email: String,password: String){
        logOutHere()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        checkEmailVerification(user)
                    }
                } else {
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    fun toSignUp(view: View){
        setContentView(R.layout.sign_up)
        var signInEmail: EditText = findViewById(R.id.signUpEmail)
        var signInPassword: EditText = findViewById(R.id.signUpPassword)
        val signInButton: Button = findViewById(R.id.signUpButton)

        signInButton.setOnClickListener{
            signIn(signInEmail.text.toString(),signInPassword.text.toString())
        }

    }
    fun toLogIn(view: View){
        initLogIn()
    }

    fun logOut(view: View){
        Firebase.auth.signOut()
        initLogIn()
    }
    fun logOutHere(){
        Firebase.auth.signOut()
    }

    fun initLogIn(){
        setContentView(R.layout.log_in)
        var logInEmail: EditText = findViewById(R.id.logInEmail)
        var logInPassword: EditText = findViewById(R.id.logInPassword)
        val logInButton: Button = findViewById(R.id.logInButton)
        logInButton.setOnClickListener{
            logIn(logInEmail.text.toString(),logInPassword.text.toString())
//            if (logInEmail.text.isEmpty()){
//                logInEmail.setText("ok")
//            }
        }
    }

    fun checkEmailVerification(user: FirebaseUser) {
        if (user.isEmailVerified) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            return
        } else {
            val handler = Handler(Looper.getMainLooper())
            val checkVerificationTask = object : Runnable {
                override fun run() {
                    user.reload().addOnCompleteListener { task ->
                        if (task.isSuccessful && user.isEmailVerified) {
                            Toast.makeText(applicationContext, "Email verified! You can now proceed.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, HomeActivity::class.java)
                            startActivity(intent)
                            handler.removeCallbacks(this)
                        } else {
                            handler.postDelayed(this, 3000) // Check again after 3 seconds
                        }
                    }
                }
            }
            handler.post(checkVerificationTask)
        }
    }
}
