package com.example.jlaja

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


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
//        if (currentUser != null) {
//            val intent = Intent(this, HomeActivity::class.java)
//            startActivity(intent)
//        }
        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                if (user.isEmailVerified) {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, "Please verify your email address.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        auth.addAuthStateListener(authListener)
    }

    fun signIn(email: String,password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                Toast.makeText(applicationContext, "Verification email sent!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(applicationContext, "Verification email error!", Toast.LENGTH_SHORT).show()
                                logOutHere()                            }
                        }
//                    setContentView(R.layout.home)
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
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    setContentView(R.layout.activity_main)
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
        initLogIn()
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

}
