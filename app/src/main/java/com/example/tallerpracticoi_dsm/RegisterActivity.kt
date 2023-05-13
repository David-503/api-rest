package com.example.tallerpracticoi_dsm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.telephony.TelephonyCallback.CallStateListener
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.tallerpracticoi_dsm.schedules.CitesList
import com.example.tallerpracticoi_dsm.scholar.Scholar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private var emailTV: EditText? = null
    private var passwordTV: EditText? = null
    private var regBtn: Button? = null
    private var progressBar: ProgressBar? = null
    private var mAuth: FirebaseAuth? = null
    private var loginRedirect: TextView? = null

    //Escuchador de FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth = FirebaseAuth.getInstance()
        initializeUI()
        regBtn!!.setOnClickListener { registerNewUser() }
        loginRedirect!!.setOnClickListener { redirectLoginActivity() }
        //Verifica si hay una sesión
        this.checkUser();
    }

    private fun registerNewUser() {
        progressBar!!.visibility = View.VISIBLE
        val email: String
        val password: String
        email = emailTV!!.text.toString()
        password = passwordTV!!.text.toString()
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, "Please enter email...", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, "Please enter password!", Toast.LENGTH_LONG).show()
            return
        }
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "Registration successful!",
                        Toast.LENGTH_LONG
                    ).show()
                    progressBar!!.visibility = View.GONE
                    showHome(email,originType.BASIC)

                } else {
                    Toast.makeText(
                        applicationContext,
                        "Registration failed! Please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                    progressBar!!.visibility = View.GONE
                }
            }
    }

    private fun initializeUI() {
        emailTV = findViewById(R.id.email)
        passwordTV = findViewById(R.id.password)
        regBtn = findViewById(R.id.register)
        progressBar = findViewById(R.id.progressBar)
        loginRedirect = findViewById<Button>(R.id.loginRedirect)

    }

    private fun redirectLoginActivity() {

        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        mAuth?.addAuthStateListener(authStateListener)
    }

    override fun onPause() {
        super.onPause()
        mAuth?.removeAuthStateListener(authStateListener)
    }

    private fun checkUser() {
        // Verificacion del usuario
        authStateListener = FirebaseAuth.AuthStateListener { auth ->
            if (auth.currentUser != null) {
                // Cambiando la vista
                showHome(auth.currentUser?.email,originType.BASIC)

                finish()

            }
        }
    }

    private fun showHome(email:String?,provider:originType){
        val i = Intent(this@RegisterActivity, Scholar::class.java)
        i.putExtra("itemMenuSelected", R.id.home)
        i.putExtra("email", email)
        i.putExtra("provider", provider)
        startActivity(i)
    }
}
