package com.example.ferreira.diaristas

import android.app.Activity
import android.content.Intent
import android.net.Uri

import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sing_up.*
import java.util.*

class SingUpActivity : AppCompatActivity() {

    companion object {
        val TAG = "SingUpActivity"
    }

    private val mAuth = FirebaseAuth.getInstance()
    private val mDataBase =  FirebaseDatabase.getInstance()
    private val myRef = mDataBase.getReference()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up)

        val _btnSingUp = findViewById<View>(R.id.btn_signup) as Button
        val _singInLink = findViewById<View>(R.id.link_login) as TextView
        val _btnPhoto = findViewById<View>(R.id.selectphoto_button_register) as Button


        _singInLink.setOnClickListener(View.OnClickListener {
                view ->login()
        })


       _btnSingUp.setOnClickListener(View.OnClickListener {
                view ->register()
       })

        _btnPhoto.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        })
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
            Log.d(TAG, "Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview_register.setImageBitmap(bitmap)

            selectphoto_button_register.alpha = 0f

//      val bitmapDrawable = BitmapDrawable(bitmap)
//      selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }


    private fun login(){
        startActivity(Intent(this, SingInActivity::class.java))
    }

    private fun register(){

        val nameText = findViewById<View>(R.id.input_name) as TextView
        val emailText = findViewById<View>(R.id.input_email) as TextView
        val mobileNumerText = findViewById<View>(R.id.input_mobile) as TextView
        val passwordText = findViewById<View>(R.id.input_password) as TextView
        val rePasswordText = findViewById<View>(R.id.input_reEnterPassword) as TextView
        val addressText = findViewById<View>(R.id.input_address) as TextView
        val imageUri = findViewById<View>(R.id.selectphoto_imageview_register)as ImageView

        var name = nameText.text.toString()
        var email = emailText.text.toString()
        var mobileNumber = mobileNumerText.text.toString()
        var password = passwordText.text.toString()
        var rePassword = rePasswordText.text.toString()
        var address = addressText.text.toString()


        if(!email.isEmpty() && !password.isEmpty() && !name.isEmpty()) {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener { task ->
                if(task.isSuccessful) {
                    val user = mAuth.currentUser
                    val uid = user!!.uid
                    uploadImageToFirebaseStorage()
                    startActivity(Intent(this, MainActivity::class.java))
                    Toast.makeText(this, "Sucesso!! :)", Toast.LENGTH_LONG).show()
                }else {
                    Toast.makeText(this, "Erro ao registrar tente novamente :(", Toast.LENGTH_LONG).show()
                }
            })
        }else {
            Toast.makeText(this, "Por favor Verifique suas credências", Toast.LENGTH_LONG).show()
        }
    }

    private fun uploadImageToFirebaseStorage(){
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/image/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, input_name.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved the user to Firebase Database")
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to set value to database: ${it.message}")
            }
    }

}

class User(val uid: String, val username:String, val profileImageUrl:String)
