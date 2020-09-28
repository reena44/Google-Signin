package com.example.googlesignib

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var googleSignInClient :GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val googleSignInOptions=  GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail().requestProfile().requestIdToken(getString(R.string.default_clientt_id)).build()

        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions)

        val account:GoogleSignInAccount?  = GoogleSignIn.getLastSignedInAccount(this)
        updateUI(account)
        google_button.setOnClickListener(this::signIn)
    }

    @SuppressLint("SetTextI18n")
    fun updateUI(account:GoogleSignInAccount?) = if(account == null){
        button.visibility = View.GONE
        tv_text.text = getString(R.string.h)
        google_button.visibility = View.VISIBLE
    }
    else{
        button.visibility = View.VISIBLE
        tv_text.text = account.displayName+"\n"+ account.email
        google_button.visibility = View.GONE
    }

    private fun signIn(view: View){
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent,1001)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001){
           val task =  GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignIn(task)
        }
    }

    private fun handleGoogleSignIn(task: Task<GoogleSignInAccount>?) = try{
       val account= task!!.getResult((ApiException::class.java))
        updateUI(account!!)
    }
    catch (ex :ApiException){
        tv_text.text= GoogleSignInStatusCodes.getStatusCodeString(ex.statusCode)
        Log.d("myLog",ex.statusCode.toString() )
        ex.printStackTrace()
    }

    fun signOut(view: View) {
        googleSignInClient.signOut().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                Toast.makeText(this@MainActivity, "Sign out", Toast.LENGTH_LONG).show()
                updateUI(GoogleSignIn.getLastSignedInAccount(this@MainActivity))

            }
        }

    }
}


