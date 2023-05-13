package com.example.tallerpracticoi_dsm

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.tallerpracticoi_dsm.schedules.CitesList
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlin.math.log
private  val TAG = "GitHubLogin"

class gitHubLogin : AppCompatActivity() {
    private var emailGitHub: EditText? = null
    private var btnGitHub: Button? = null
    private val GITHUB_SIGN_IN = 100
    private var firebaseAuthInstance: FirebaseAuth? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_git_hub_login)
        initializeUI()
        btnGitHub!!.setOnClickListener { loginGitHubAccount() }
        verifyAuth()
        firebaseAuthInstance = FirebaseAuth.getInstance()

    }

    private fun initializeUI() {
        btnGitHub = findViewById<Button>(R.id.btnGitHubLogin)
        emailGitHub = findViewById<EditText>(R.id.githubEmail)

    }
    private fun loginGitHubAccount(){
        val email: String
        email = emailGitHub?.text.toString()
        val provider = OAuthProvider.newBuilder("github.com")
        // Target specific email with login hint.
        provider.addCustomParameter("login", email)

        // Request read access to a user's email addresses.
        // This must be preconfigured in the app's API permissions.
        provider.scopes = listOf("user:email")

        firebaseAuthInstance?.startActivityForSignInWithProvider( /* activity = */this, provider.build())
            ?.addOnSuccessListener {
                // User is signed in.
                // IdP data available in
                // authResult.getAdditionalUserInfo().getProfile().
                // The OAuth access token can also be retrieved:
                // ((OAuthCredential)authResult.getCredential()).getAccessToken().
                // The OAuth secret can be retrieved by calling:
                // ((OAuthCredential)authResult.getCredential()).getSecret().
                val profile =it.getAdditionalUserInfo()?.getProfile();
                Toast.makeText(
                    applicationContext,
                    "GitHubLogin Success: "+profile?.get("login").toString(),
                    Toast.LENGTH_LONG
                ).show()
                val preps = getSharedPreferences(getString(R.string.preps_file),Context.MODE_PRIVATE).edit()
                preps.putString("email",profile?.get("login").toString())
                preps.putString("provider",originType.GITHUB.toString())
                preps.apply()
                showHome(profile?.get("login").toString()?:"",originType.GITHUB)
                Log.w(TAG,profile.toString())
            }
            ?.addOnFailureListener {
                // Handle failure.
                Toast.makeText(
                    applicationContext,
                    "GitHubLogin Failed: 1",
                    Toast.LENGTH_LONG
                ).show()
                Toast.makeText(
                    applicationContext,
                    it.message.toString(),
                    Toast.LENGTH_LONG
                ).show()
                Log.w(TAG,it.message.toString())

            }
    }

    private fun verifyAuth (){
        val pendingResultTask = firebaseAuthInstance?.pendingAuthResult
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                .addOnSuccessListener {
                    // User is signed in.
                    // IdP data available in
                    //authResult.getAdditionalUserInfo().getProfile().
                    // ((OAuthCredential)authResult.getCredential()).getSecret().
                    val profile =it.getAdditionalUserInfo()?.getProfile();
                    Toast.makeText(
                        applicationContext,
                        "GitHubLogin recovery Success: "+profile?.get("login").toString(),
                        Toast.LENGTH_LONG
                    ).show()
                    val preps = getSharedPreferences(getString(R.string.preps_file),Context.MODE_PRIVATE).edit()
                    preps.putString("email",profile?.get("login").toString())
                    preps.putString("provider",originType.GITHUB.toString())
                    preps.apply()
                    showHome(profile?.get("login").toString()?:"",originType.GITHUB)
                    Log.w(TAG,profile.toString())

                    // The OAuth access token can also be retrieved:
                    // ((OAuthCredential)authResult.getCredential()).getAccessToken().
                    // The OAuth secret can be retrieved by calling:
                    // ((OAuthCredential)authResult.getCredential()).getSecret().
                }
                .addOnFailureListener {
                    Toast.makeText(
                        applicationContext,
                        "GitHub Api login failed! Please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }
        } else {
            // There's no pending result so you need to start the sign-in flow.
            // See below.
           // val i = Intent(this@gitHubLogin, LoginActivity::class.java)
           // startActivity(i)
        }
    }
    private fun showHome(email:String?,provider:originType){
        val i = Intent(this@gitHubLogin, CitesList::class.java)
        i.putExtra("itemMenuSelected", R.id.home)
        i.putExtra("email", email)
        i.putExtra("provider", provider.toString())
        startActivity(i)
    }

}