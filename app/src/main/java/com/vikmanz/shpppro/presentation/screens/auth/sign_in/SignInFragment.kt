package com.vikmanz.shpppro.presentation.screens.auth.sign_in

import android.app.AlertDialog
import android.util.Patterns
import androidx.fragment.app.viewModels
import com.vikmanz.shpppro.R
import com.vikmanz.shpppro.data.utils.auth.PasswordErrorsChecker.checkPasswordErrors
import com.vikmanz.shpppro.databinding.FragmentSignInBinding
import com.vikmanz.shpppro.presentation.base.BaseFragment
import com.vikmanz.shpppro.presentation.utils.extensions.clearError
import com.vikmanz.shpppro.presentation.utils.extensions.setKeyboardVisibility
import com.vikmanz.shpppro.presentation.utils.extensions.setMultipleGone
import com.vikmanz.shpppro.presentation.utils.extensions.setMultipleVisible
import com.vikmanz.shpppro.presentation.utils.extensions.startChangeLanguageActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment :
    BaseFragment<FragmentSignInBinding, SignInViewModel>(FragmentSignInBinding::inflate) {

    override val viewModel: SignInViewModel by viewModels()

    override fun setListeners() {
        with(binding) {
            buttonLoginRegisterByEmail.setOnClickListener { checkForm() }
            textViewLoginSwitchScreenToLoginButton.setOnClickListener { viewModel.onSignUpClick() }
            buttonLoginRegisterByGoogle.setOnClickListener { buttonLoginRegisterByGoogle.setFunText() }
        }
        initHelpTesterButtons()
        setLoginPasswordFocusListeners()        // Listeners to fields and buttons.
        backgroundFocusHandler()                // de-focus fields when click on bg
    }

    override fun setObservers() {
        viewModel.helperButtonsVisible.observe(viewLifecycleOwner) { showHelpers(it) }
    }

    /**
     * Observe and show/Hide help buttons.
     */
    private fun showHelpers(isVisible: Boolean) {
        with(binding) {
            if (isVisible) {
                setMultipleVisible(
                    buttonLoginFillLoginAndPass,
                    buttonLoginClearLoginPassFields,
                    buttonLoginChangeLanguage
                )
            } else {
                setMultipleGone(
                    buttonLoginFillLoginAndPass,
                    buttonLoginClearLoginPassFields,
                    buttonLoginChangeLanguage
                )
            }
        }
    }


    /**
     * Set onClickListeners for email and password text input fields.
     */
    private fun setLoginPasswordFocusListeners() {
        with(binding) {
            textInputLoginEmailField.setOnFocusChangeListener { _, focused ->
                if (focused && !viewModel.emailAlreadyFocused) {
                    viewModel.emailAlreadyFocused = true
                } else if (!focused && viewModel.emailAlreadyFocused) {
                    validEmail()
                }
            }

            textInputLoginPasswordField.setOnFocusChangeListener { _, focused ->
                if (focused && !viewModel.passwordAlreadyFocused)
                    viewModel.passwordAlreadyFocused = true
                else if (!focused && viewModel.passwordAlreadyFocused)
                    validPassword()
            }
        }
    }

    /**
     * Validate input email for errors.
     *
     * @return Invalid message if email contains errors, or null if not.
     */
    private fun validEmail(): String? {
        // Get email text
        val emailText = binding.textInputLoginEmailField.text.toString()

        // Do check for standard Patterns.EMAIL_ADDRESS regex.
        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            val error = getString(R.string.auth_activity_warning_message_invalid_email)
            binding.textInputLayoutLoginEmail.helperText = error
            return error
        }
        // If pass check, return null.
        binding.textInputLayoutLoginEmail.helperText = null
        return null
    }

    /**
     * Validate input password for errors.
     *
     * @return Invalid message if password contains errors, or null if not.
     */
    private fun validPassword(): String? {
        // Get password text
        val passwordText = binding.textInputLoginPasswordField.text.toString()
        val result = checkPasswordErrors(passwordText, this)
        // If pass all checks, return null or return errors.
        val message = if (result == "") null else result
        binding.textInputLayoutLoginPassword.helperText = message
        return message
    }

    /**
     *  Submit Login/Register form and init MainActivity or show error messages.
     */
    private fun checkForm() {
        // Check correct or not.
        val isEmailCorrect = validEmail() == null
        val isPasswordCorrect = validPassword() == null
        // If correct - start main activity.
        if (isEmailCorrect && isPasswordCorrect) initMainActivity()
        // If have error - show error message.
        else showInvalidFormMessage(isEmailCorrect, isPasswordCorrect)
    }


    private fun initMainActivity() {
        with(binding) {
            val email = textInputLoginEmailField.text.toString()        // Take email text.
            val password = textInputLoginPasswordField.text.toString()  // Take pass text.
            if (checkboxLoginRememberMe.isChecked) {                    // If check Remember...
                viewModel.saveUserEmailToDatastore(email, password)     // ... save to Data Store.
            }
            viewModel.onSignInClick(email, password)                    // Start main activity.
        }
    }

    /**
     *  Get error status of email and password, and show error message.
     */
    private fun showInvalidFormMessage(isEmailCorrect: Boolean, isPasswordCorrect: Boolean) {
        // Create error message.
        val message = createErrorMessage(isEmailCorrect, isPasswordCorrect)
        // Show AlertDialog with error message.
        showErrorAlertDialog(message, isEmailCorrect)
    }

    private fun createErrorMessage(isEmailCorrect: Boolean, isPasswordCorrect: Boolean): String {
        // Create error message.
        var message = ""
        with(binding) {
            message += if (!isEmailCorrect)
                "\n\n${getString(R.string.auth_activity_warning_message_email_title)}\n${textInputLayoutLoginEmail.helperText}"
            else ""
            message += if (!isPasswordCorrect)
                "\n\n${getString(R.string.auth_activity_warning_message_password_title)}\n${textInputLayoutLoginPassword.helperText}"
            else ""
        }
        return message
    }

    private fun showErrorAlertDialog(message: String, isEmailCorrect: Boolean) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.auth_activity_warning_message_invalid_register_form))
            .setMessage(message)
            .setPositiveButton(getString(R.string.auth_activity_warning_message_ok_button_text)) { _, _ ->
                with(binding) {
                    // Set focus to first field with error.
                    if (isEmailCorrect) textInputLoginEmailField.requestFocus()
                    else textInputLoginPasswordField.requestFocus()
                }
            }
            .show()
    }

    /**
     *  Init helper buttons for tests.
     *  It will be removed from release version.
     */
    private fun initHelpTesterButtons() {

        // Set onClickListeners().
        with(binding) {
            // Set onClickListener to message "Do you have account" to see help buttons.
            textViewLoginAlreadyHaveAccountMessage.setOnClickListener { viewModel.showOrHideHelpers() }

            // Fill fields button listener.
            buttonLoginFillLoginAndPass.setOnClickListener {
                textInputLoginEmailField.setText(TEST_LOGIN)
                textInputLoginPasswordField.setText(TEST_PASSWORD)
                textInputLayoutLoginEmail.clearError()
                textInputLayoutLoginPassword.clearError()
                checkboxLoginRememberMe.isChecked = false
            }

            // Clear fields button listener.
            buttonLoginClearLoginPassFields.setOnClickListener {
                textInputLoginEmailField.setText("")
                textInputLoginPasswordField.setText("")
                textInputLayoutLoginEmail.clearError()
                textInputLayoutLoginPassword.clearError()
                checkboxLoginRememberMe.isChecked = false
            }

            buttonLoginChangeLanguage.setOnClickListener { startChangeLanguageActivity() }
        }
    }

    /**
     *  De-focus views, when user do click on background.
     */
    private fun backgroundFocusHandler() = with(binding) {
        root.setOnClickListener {
            textInputLoginEmailField.clearFocus()
            textInputLoginPasswordField.clearFocus()
            setKeyboardVisibility(false)
        }
    }


    companion object {
        /**
         * Constants.
         */
        const val TEST_LOGIN = "viktor.manza@gmail.com"
        const val TEST_PASSWORD = "passwordE3@a"
    }

}