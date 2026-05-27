package com.motomeet.mobile.ui.auth

import android.content.Intent
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
import com.motomeet.mobile.data.network.TokenManager
import com.motomeet.mobile.ui.main.MainActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvGoToRegister = findViewById<TextView>(R.id.tvGoToRegister)

        btnLogin.setOnClickListener {
            viewModel.login(etEmail.text.toString(), etPassword.text.toString())
        }

        tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collect { state ->
                    when (state) {
                        is AuthState.Loading -> {
                            btnLogin.isEnabled = false
                            btnLogin.text = "SIGNING IN..."
                        }
                        is AuthState.Success -> {
                            // Fix: Handle nullable token safely to avoid argument type mismatch
                            state.token?.let {
                                TokenManager.getInstance(this@LoginActivity).saveToken(it)
                            }
                            Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                        is AuthState.Error -> {
                            btnLogin.isEnabled = true
                            btnLogin.text = "SIGN IN"
                            Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_LONG).show()
                            viewModel.resetLoginState()
                        }
                        else -> {
                            btnLogin.isEnabled = true
                            btnLogin.text = "SIGN IN"
                        }
                    }
                }
            }
        }
    }
}
