package com.cos60011.group1.mttransit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.cos60011.group1.mttransit.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentLoginBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        // Initialize Firebase Auth
        auth = Firebase.auth
        val email = binding.textInputEmail.editText
        val password = binding.textInputPassword.editText

        binding.buttonLogin.setOnClickListener { view: View ->
            // TODO Add input Validation
            val emailInput = email?.text.toString()
            val passwordInput = password?.text.toString()
            signIn(emailInput, passwordInput)
        }

        return binding.root
    }

    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                val user = auth.currentUser
                Toast.makeText(
                    context, "Authentication succeed.",
                    Toast.LENGTH_SHORT
                ).show()
                Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_busBoardFragment)
            } else {
                // If sign in fails, display a message to the user.
                Toast.makeText(
                    context, "Authentication failed. $email",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}



