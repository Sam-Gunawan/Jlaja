package com.example.jlaja
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class UserAuth : ComponentActivity() {
    //    private var auth : FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)
        auth = Firebase.auth

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

    fun signIn(name: String, email: String,password: String){
        logOutHere()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                Toast.makeText(applicationContext, "Verification email sent!", Toast.LENGTH_SHORT).show()
                                storeUserData(user.uid, name, email)
                                getUserData(user.uid)
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

    fun storeUserData(userId: String, name: String, email: String){
        val db = Firebase.firestore //init database
        val userData = hashMapOf( //store user data into hash map
            "username" to name, //"username", "email", "uid" is column name to (value)
            "email" to email,
            "uid" to userId
        )
        db.collection("users").document(userId).set(userData)
        //store the data into database, "users" is table/collection name
        //'userID' is the firebase user id used to store data in respect to the user
        //'userData' is the hashmap from before
        //to get uid, first get user by 'val user = FirebaseAuth.getInstance().currentUser'
        //then get uid by var uid = user.uid
    }

    fun getUserData(userId: String) {
        val db = Firebase.firestore //init database
        db.collection("users").document(userId)
            //getting the data from database, "users" is table/collection name
            //'userID' is to know which user's data to get
            .get() //get
            .addOnSuccessListener { data -> //on success store to 'data'
                if (data.exists()) { //check if the information is there
                    val username = data.getString("username") //get the data with data.getString((what value))
                    val email = data.getString("email")
                    Log.d("UserData", "Username: $username, Email: $email")
                } else {
                    Log.d("UserData","Fail to get data")
                }
            }
            .addOnFailureListener { exception ->
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

    fun logOut(view: View){
        Firebase.auth.signOut()
        initLogIn()
    }
    fun logOutHere(){
        Firebase.auth.signOut()
    }
    fun initLogIn(){
        var logInEmail: EditText = findViewById(R.id.EnterEmailBox)
        var logInPassword: EditText = findViewById(R.id.EnterPasswordBox)
        val logInButton: ImageView = findViewById(R.id.SignInButton)
        Log.i("data from initLogIn", logInEmail.text.toString())
        logInButton.setOnClickListener{
            logIn(logInEmail.text.toString(),logInPassword.text.toString())
//            if (logInEmail.text.isEmpty()){
//                logInEmail.setText("ok")
//            }
        }
    }
    fun checkEmailVerification(user: FirebaseUser) {
        if (user.isEmailVerified) {
            val intent = Intent(this, FirstTimeUserHome::class.java)
            startActivity(intent)
            return
        } else {
            val handler = Handler(Looper.getMainLooper())
            val checkVerificationTask = object : Runnable {
                override fun run() {
                    user.reload().addOnCompleteListener { task ->
                        if (task.isSuccessful && user.isEmailVerified) {
                            Toast.makeText(applicationContext, "Email verified! You can now proceed.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, FirstTimeUserHome::class.java)
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
    fun toSignUp(){
        setContentView(R.layout.activity_sign_up_page)
        val signInEmail: EditText = findViewById(R.id.EnterEmailBox)
        val signInPassword: EditText = findViewById(R.id.EnterPasswordBox)
        val signInFirstName: EditText = findViewById(R.id.EnterFirstNameBox)
        val signInSecondName: EditText = findViewById(R.id.EnterSecondNameBox)
        val signInButton: ImageView = findViewById(R.id.SignInButton)

        val signInUserName: String = "$signInFirstName $signInSecondName"
        signInButton.setOnClickListener{
            signIn(signInUserName, signInEmail.text.toString(), signInPassword.text.toString())
        }
    }

    fun toLogIn(){
        initLogIn()
    }
}