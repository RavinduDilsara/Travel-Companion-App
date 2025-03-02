package com.project.travelcompanionapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.google.firebase.auth.FirebaseAuth
import com.project.travelcompanionapp.databinding.ActivitySignUpBinding

class SignUpActivity : BaseActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // If you want to navigate from SignUpActivity to SignInActivity
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()  // Optionally finish SignUpActivity
        }

        binding.button.setOnClickListener {

            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {

                if (pass.length < 6) {
                    Toast.makeText(this, "Password should be at least 6 characters long", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                if (pass == confirmPass) {
                    // Attempt to create the user in Firebase Authentication
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Start MainActivity after successful sign-up
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()  // Finish SignUpActivity so user cannot go back to it
                        } else {
                            // Show error message if the sign-up fails
                            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Empty fields are not allowed !!", Toast.LENGTH_LONG).show()
            }
        }
    }
}
