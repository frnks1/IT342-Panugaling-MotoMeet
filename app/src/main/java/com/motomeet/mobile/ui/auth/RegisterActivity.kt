package com.motomeet.mobile.ui.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.motomeet.mobile.R
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etLastName = findViewById<EditText>(R.id.etLastName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvGoToLogin = findViewById<TextView>(R.id.tvGoToLogin)

        btnRegister.setOnClickListener {
            viewModel.register(
                etFirstName.text.toString(),
                etLastName.text.toString(),
                etEmail.text.toString(),
                etPassword.text.toString(),
                etConfirmPassword.text.toString()
            )
        }

        tvGoToLogin.setOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registerState.collect { state ->
                    when (state) {
                        is AuthState.Loading -> {
                            btnRegister.isEnabled = false
                            btnRegister.text = "CREATING ACCOUNT..."
                        }
                        is AuthState.Success -> {
                            Toast.makeText(this@RegisterActivity, state.message, Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        is AuthState.Error -> {
                            btnRegister.isEnabled = true
                            btnRegister.text = "CREATE ACCOUNT"
                            Toast.makeText(this@RegisterActivity, state.message, Toast.LENGTH_LONG).show()
                            viewModel.resetRegisterState()
                        }
                        else -> {
                            btnRegister.isEnabled = true
                            btnRegister.text = "CREATE ACCOUNT"
                        }
                    }
                }
            }
        }
    }
}
