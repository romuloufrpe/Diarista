package com.example.ferreira.diaristas

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

private val mAuth = FirebaseAuth.getInstance()

class SplashActivity : AppCompatActivity() {
    private var mDelayHandler: Handler? = null
    private var SPLASH_DELAY: Long = 3000

    internal val mRunnable: Runnable = Runnable {

            if(FirebaseAuth.getInstance().currentUser == null){
                val intent = Intent(applicationContext, SingInActivity::class.java)
                startActivity(intent)
                finish()
            }else {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        //Initialize Handler
        mDelayHandler = Handler()

        //Navigation with Delay
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)
    }

    public override fun onDestroy() {
        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }
        super.onDestroy()
    }
}
