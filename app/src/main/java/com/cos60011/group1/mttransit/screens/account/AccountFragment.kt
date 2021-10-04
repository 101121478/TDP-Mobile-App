package com.cos60011.group1.mttransit.screens.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.cos60011.group1.mttransit.R
import com.cos60011.group1.mttransit.databinding.FragmentAccountBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentAccountBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_account, container, false)
        binding.textAccountEmail.text = Firebase.auth.currentUser?.email.toString()

        binding.buttonLogout.setOnClickListener { view: View ->
            // Log out user
            Firebase.auth.signOut()
            view.findNavController().navigate(R.id.action_accountFragment_to_loginFragment3)
        }
        
        return binding.root
    }

}