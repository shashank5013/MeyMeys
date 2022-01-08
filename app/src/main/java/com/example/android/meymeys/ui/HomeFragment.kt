package com.example.android.meymeys.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.meymeys.adapter.MemeClickListener
import com.example.android.meymeys.adapter.MemeListAdapter
import com.example.android.meymeys.databinding.FragmentHomeBinding
import com.example.android.meymeys.model.Meme
import com.example.android.meymeys.utils.SUBREDDIT_HOME
import com.example.android.meymeys.viewmodel.NetworkViewModel
import com.example.android.meymeys.viewmodelfactory.NetworkViewModelFactory


class HomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding=FragmentHomeBinding.inflate(layoutInflater, container, false)

        //Initialising viewmodel
        val application= requireNotNull(this.activity).application
        val viewModelFactory=NetworkViewModelFactory(SUBREDDIT_HOME, application)
        val viewModel=ViewModelProvider(this,viewModelFactory).get(NetworkViewModel::class.java)

        //setting up Recycler View
        val adapter=MemeListAdapter(object:MemeClickListener{
            override fun onclick(meme: Meme) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDetailFragment(meme))
            }

        })
        binding.apply {
            this.homeMemeList.adapter=adapter
            this.viewModel=viewModel
            this.lifecycleOwner=this@HomeFragment
        }

        //Observing data coming from the internet
        viewModel.memeResponse.observe(viewLifecycleOwner,{
            adapter.differ.submitList(it.memes)
        })
        return binding.root
    }

}