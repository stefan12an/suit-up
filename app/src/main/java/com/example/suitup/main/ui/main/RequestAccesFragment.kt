package com.example.suitup.main.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.suitup.databinding.FragmentRequestAccesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestAccesFragment : Fragment() {
    private lateinit var binding: FragmentRequestAccesBinding
    private val viewModel: MainViewModel by activityViewModels()
    

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestAccesBinding.inflate(inflater, container, false)
        binding.requestAccessBtn.setOnClickListener {
            viewModel.action(PermissionIntent.Request)
        }
        return binding.root
    }



}