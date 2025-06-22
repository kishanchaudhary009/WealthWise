package com.android.fintech

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseUser

class LoginSignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private const val RC_SIGN_IN = 1001
        private const val TAG = "LoginSignupActivity"
        private const val WEB_CLIENT_ID = "361534257768-e04i261ifb38f9l0lo8i9hl8pksc3qc0.apps.googleusercontent.com"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_signup)

        auth = FirebaseAuth.getInstance()

        // Step 1: If already logged in, go to MainActivity
        val currentUser = auth.currentUser
        if (currentUser != null) {
            launchMainActivity(currentUser)
            return
        }

        // Step 2: Google Sign-In Options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Step 3: Set Google Sign-In Button
        val googleSignInButton = findViewById<SignInButton>(R.id.google_sign_in_button)
        googleSignInButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    // Step 4: Handle Google Sign-In result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<com.google.android.gms.auth.api.signin.GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                auth.signInWithCredential(credential).addOnCompleteListener(this) { firebaseTask ->
                    if (firebaseTask.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            launchMainActivity(user)
                        }
                    } else {
                        Toast.makeText(this, "Firebase authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: ApiException) {
                Log.e(TAG, "Google sign in failed", e)
                Toast.makeText(this, "Google Sign-In failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Step 5: Launch MainActivity with UID
    private fun launchMainActivity(user: FirebaseUser) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("uid", user.uid)
        startActivity(intent)
        finish()
    }
}
