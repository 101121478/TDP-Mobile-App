package com.cos60011.group1.mttransit.screens.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.cos60011.group1.mttransit.R
import com.cos60011.group1.mttransit.databinding.FragmentLoginBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentLoginBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_login, container, false)

        // Initialize Firebase Auth
        auth = Firebase.auth

        val emailInput = binding.textInputEmail
        val passwordInput = binding.textInputPassword
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        binding.buttonLogin.setOnClickListener { view: View ->
            emailInput.error = ""
            passwordInput.error = ""
            val emailText = emailInput.editText!!.text
            val passwordText = passwordInput.editText!!.text

            if (emailText.isEmpty()) {
                emailInput.error = "The email field is required."
            } else if (!isValidEmail(emailText.toString())) {
                emailInput.error = "Please enter a valid email address."
            } else if (passwordText.isEmpty()) {
                passwordInput.error = "The password field is required."
            } else {
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                signIn(emailText.toString(), passwordText.toString())
            }
        }
        return binding.root
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                // Initialize sharedPref
                sharedPreferences = requireActivity()
                    .getSharedPreferences("com.cos60011.group1.mttransit.settings.${Firebase.auth.currentUser?.email.toString()}", Context.MODE_PRIVATE)

                val userLocation = sharedPreferences.getString("userLocation", "Unknown")
                if (userLocation.toString() == "Unknown") {
                    Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_setStationFragment)
                } else {
                    Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_busBoardFragment)
                }

                Snackbar.make(requireView(), "Welcome to MT system", Snackbar.LENGTH_LONG).show()
            } else {
                // If sign in fails, display a message to the user.
                MaterialAlertDialogBuilder(requireContext()).setTitle("Invalid Account").
                setMessage("Incorrect email or password").
                setPositiveButton("OK") {
                    dialog, which -> dialog.dismiss()
                }.show()
            }
        }
    }
}



