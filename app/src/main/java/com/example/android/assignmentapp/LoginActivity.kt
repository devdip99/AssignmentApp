package com.example.android.assignmentapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        loginbutton.setOnClickListener {

            if (emailTextView.text.isNotEmpty() && passwordTextView.text.isNotEmpty()) {
                mAuth.signInWithEmailAndPassword(emailTextView.text.toString(), passwordTextView.text.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                this.saveUserEmail(emailTextView.text.toString())
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                            }

                        }
            } else {
                Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show()
            }
        }

        register_link.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
            finish()
        }


    }


}
