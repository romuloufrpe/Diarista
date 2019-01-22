package com.example.ferreira.diaristas

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.twisty.interlude.lib.IndicatorType
import com.twisty.interlude.lib.Interlude
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_sing_in.*
import java.util.*




class SingInActivity : AppCompatActivity() {


    val RC_SIGN_IN = 1
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    lateinit var alertDialog: android.app.AlertDialog

    //construct
    var interlude: Interlude = Interlude()


    private lateinit var firebaseAuth: FirebaseAuth
    //private val mAuth = FirebaseAuth.getInstance()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_in)
        configureGoogleSignIn()
        setupUI()
        showDialog()
        firebaseAuth = FirebaseAuth.getInstance()

        val _singUpLink = findViewById<View>(R.id.link_singup) as TextView
        val _buttonLogin = findViewById<View>(R.id.btn_login) as Button
        val _btnGoogle = findViewById<View>(R.id.btn_google)as Button

        _buttonLogin.setOnClickListener(View.OnClickListener {
            view -> login()
        })



        _singUpLink.setOnClickListener(View.OnClickListener {
            view -> register()
        })

    }

    private fun showDialog(){
        alertDialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Por favor aguarde...")
            .setCancelable(false)
            .build()
    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user !=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun configureGoogleSignIn(){
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    private fun setupUI(){
        btn_google.setOnClickListener {
            signIn()
        }
    }

    private fun signIn(){
        var signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthGoogle(account)
                }

            }catch (e: ApiException){
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun firebaseAuthGoogle(account: GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        alertDialog.show()
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener{
            if (it.isSuccessful){
                startActivity(Intent(this, MainActivity::class.java))
                alertDialog.dismiss()
            }else {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()

            }
        }
    }

    private fun login(){
        val emailText = findViewById<View>(R.id.inputEmail) as TextView
        var email = emailText.text.toString()
        val passwordText = findViewById<View>(R.id.inputPassword) as TextView
        var password = passwordText.text.toString()

        if (!email.isEmpty() && !password.isEmpty()){
            alertDialog.show()
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, OnCompleteListener<AuthResult> {
                task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                        startActivity(Intent(this, MainActivity::class.java))
                        alertDialog.dismiss()

                }else{
                    Toast.makeText(this, "Erro ao fazer Loggin", Toast.LENGTH_LONG).show()
                }
            })
        }else {
            Toast.makeText(this, "Erro nas credênciais", Toast.LENGTH_LONG).show()
        }
    }

    private fun register(){
        startActivity(Intent(this, SingUpActivity::class.java))
    }

}