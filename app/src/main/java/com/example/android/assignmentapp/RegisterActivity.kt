package com.example.android.assignmentapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth = FirebaseAuth.getInstance()

        registerbutton.setOnClickListener {
            if (emailTextView.text.isNotEmpty() && passwordTextView.text.isNotEmpty()) {
                if (isValidEmail(emailTextView.text)) {
                    mAuth.createUserWithEmailAndPassword(emailTextView.text.toString(), passwordTextView.text.toString())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    this.saveUserEmail(emailTextView.text.toString())
                                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                } else {
                    Toast.makeText(this, "Please enter a valid email Id", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show()
            }
        }

        login_link.setOnClickListener { startActivity(Intent(this, LoginActivity::class.java))
        finish()}

    }

    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}
