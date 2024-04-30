package com.example.cardioguardian

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ActionMenuView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.lang.ref.PhantomReference

class RegisterActivity : AppCompatActivity() {

    private lateinit var Email_text:EditText
    private lateinit var Pass_text:EditText
    private lateinit var dbReference: DatabaseReference
    private lateinit var databases:FirebaseDatabase
    private lateinit var auth:FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Email_text = findViewById(R.id.Email_text)
        Pass_text = findViewById(R.id.Pass_text)

        databases=FirebaseDatabase.getInstance()
        auth=FirebaseAuth.getInstance()

        dbReference=databases.reference.child("User")

        val loginbtn : Button = findViewById<Button>(R.id.Inicio_Butt)
        loginbtn.setOnClickListener{
            action()
        }

    }

    fun register(view: View){
        createNewAccount()

    }
    private fun createNewAccount(){
        val email:String=Email_text.text.toString()
        val password:String=Pass_text.text.toString()

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){
                    task ->

                    if(task.isComplete){
                        val user:FirebaseUser?=auth.currentUser
                        verifyEmail(user)
                        
                        val userBD= user?.uid?.let { dbReference.child(it) }

                        userBD?.child("email")?.setValue(email)
                        userBD?.child("Password")?.setValue(password)
                        action()

                    }

                }

        }

    }
    private fun action(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun verifyEmail(user: FirebaseUser?){
        user?.sendEmailVerification()
            ?.addOnCompleteListener(this){
                task ->

                if (task.isComplete){
                    Toast.makeText(this,"Email enviado",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this,"Error al enviar ",Toast.LENGTH_LONG).show()
                }
            }

    }

}