package com.vikmanz.shpppro

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.vikmanz.shpppro.constants.Constants.INTENT_EMAIL_ID
import com.vikmanz.shpppro.databinding.ActivityAuthBinding


class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private var isLoginScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.tiTextEmail.setText(getString(R.string.my_main_email))
        binding.tiTextPassword.setText(getString(R.string.my_main_password))

        // Focus to bg and checkbox login functions.
        backgroundFocusHandler()
        binding.checkBox.setOnClickListener { printEmail() }    // disable this to own email

        // Listeners to text fields and register button.
        emailFocusListener()
        passwordFocusListener()
        binding.bRegisterByEmailPassword.setOnClickListener { submitRegisterForm() }

        // Change Register to Login and another.
        binding.alreadyHaveAccLink.setOnClickListener { changeRegisterLoginScreen() }
    }

    private fun changeRegisterLoginScreen() {

        isLoginScreen = !isLoginScreen

        if (isLoginScreen) {
            binding.apply {
                tvHelloText.text = getString(R.string.helloText_signin)
                tvHelloSubText.text = getString(R.string.helloSubText_signin)
                tiLayoutPassword.isCounterEnabled = false
                forgotPassword?.visibility = View.VISIBLE
                bRegisterByGoogle.visibility = View.GONE
                googleOrRegister.visibility = View.GONE
                bRegisterByEmailPassword.text = getString(R.string.registerButton_signin)
                warningAboutTerms.visibility = View.GONE
                alreadyHaveAccMessage.text = getString(R.string.already_have_an_account_signin)
                alreadyHaveAccLink.text = getString(R.string.sign_in_signin)
            }
        }
        else {
            binding.apply {
                tvHelloText.text = getString(R.string.helloText)
                tvHelloSubText.text = getString(R.string.helloSubText)
                tiLayoutPassword.isCounterEnabled = true
                forgotPassword?.visibility = View.GONE
                bRegisterByGoogle.visibility = View.VISIBLE
                googleOrRegister.visibility = View.VISIBLE
                bRegisterByEmailPassword.text = getString(R.string.registerButton)
                warningAboutTerms.visibility = View.VISIBLE
                alreadyHaveAccMessage.text = getString(R.string.already_have_an_account)
                alreadyHaveAccLink.text = getString(R.string.sign_in)
            }
        }
    }


    private fun doRegister() {
        val intentObject = Intent(this, MainActivity::class.java)
        intentObject.putExtra(INTENT_EMAIL_ID, binding.tiTextEmail.text.toString())
        Log.d("MyLog", "email: ${binding.tiTextEmail.text.toString()}")
        startActivity(intentObject)
        overridePendingTransition(R.anim.zoom_in_inner, R.anim.zoom_in_outter)
    }


    private fun printEmail() {
        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            Log.d("MyLog", "$isChecked")
            if (isChecked) {
                val text = getString(R.string.my_main_email)
                binding.apply {
                    tiTextEmail.setText(text)
                    tiLayoutEmail.helperText = null
                    tiLayoutPassword.helperText = null
                }
            }
        }
    }

    private fun backgroundFocusHandler() {
        binding.root.setOnClickListener { binding.tiTextEmail.clearFocus() }
    }

    private fun submitRegisterForm() {
        if (isLoginScreen) {
            AlertDialog.Builder(this)
                .setTitle("NOT YET")
                .setPositiveButton("Okay") { _, _ ->
                    // do nothing
                }
                .show()
        }
        else {
            binding.tiLayoutEmail.helperText = validEmail()
            binding.tiLayoutPassword.helperText = validPassword()

            val isValid =
                binding.tiLayoutEmail.helperText == null && binding.tiLayoutPassword.helperText == null

            if (isValid) {
                doRegister()
            } else {
                invalidForm()
            }
        }
    }



    private fun invalidForm() {
        var message = ""
        message += if (binding.tiLayoutEmail.helperText != null) "\n\nEmail: ${binding.tiLayoutEmail.helperText}" else ""
        message += if (binding.tiLayoutPassword.helperText != null) "\n\nPassword: ${binding.tiLayoutPassword.helperText}" else ""

        Log.d("MyLog", message)
        AlertDialog.Builder(this)
            .setTitle("Invalid registration form!")
            .setMessage(message)
            .setPositiveButton("Okay") { _, _ ->
                if (binding.tiLayoutEmail.helperText != null) {
                    binding.tiTextEmail.requestFocus()
                } else {
                    binding.tiTextPassword.requestFocus()
                }
            }
            .show()
    }

    private fun emailFocusListener() {
        binding.tiTextEmail.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.tiLayoutEmail.helperText = validEmail()
            }
        }
    }

    private fun validEmail(): String? {
        val emailText = binding.tiTextEmail.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            return "Invalid email address"
        }
        return null
    }

    private fun passwordFocusListener() {
        binding.tiTextPassword.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.tiLayoutPassword.helperText = validPassword()
            }
        }
    }

    private fun validPassword(): String? {
        if (!binding.checkBox.isChecked) {
            val passwordText = binding.tiTextPassword.text.toString()
            if (passwordText.length < 8) {
                return "Minimum 8 Characters Password"
            }
            if (!passwordText.matches(".*[A-Z].*".toRegex())) {
                return "Must Contain 1 Upper-case Character"
            }
            if (!passwordText.matches(".*[a-z].*".toRegex())) {
                return "Must Contain 1 Lower-case Character"
            }
            if (!passwordText.matches(".*[@#\$%^&+=].*".toRegex())) {
                return "Must Contain 1 Special Character (@#\$%^&+=)"
            }
        }
        return null
    }
}