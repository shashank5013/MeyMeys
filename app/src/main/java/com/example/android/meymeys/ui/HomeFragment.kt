package com.example.android.meymeys.ui

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.android.meymeys.R
import com.example.android.meymeys.api.RetrofitInstance
import com.example.android.meymeys.databinding.FragmentHomeBinding
import com.example.android.meymeys.viewmodel.NetworkViewModel
import com.example.android.meymeys.viewmodelfactory.NetworkViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding=FragmentHomeBinding.inflate(layoutInflater, container, false)

        //Initialising viewmodel
        val application= requireNotNull(this.activity).application
        val viewModelFactory=NetworkViewModelFactory("wholesomememes", application)
        val viewModel=ViewModelProvider(this,viewModelFactory).get(NetworkViewModel::class.java)

        viewModel.testString.observe(viewLifecycleOwner,{
            binding.test.text=it
        })
        return binding.root
    }

}