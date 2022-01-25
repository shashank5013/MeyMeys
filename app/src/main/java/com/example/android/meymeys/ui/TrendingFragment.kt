package com.example.android.meymeys.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.meymeys.adapter.MemeClickListener
import com.example.android.meymeys.adapter.MemeListAdapter
import com.example.android.meymeys.databinding.FragmentTrendingBinding
import com.example.android.meymeys.model.Meme
import com.example.android.meymeys.utils.SUBREDDIT_TRENDING
import com.example.android.meymeys.viewmodel.NetworkViewModel
import com.example.android.meymeys.viewmodelfactory.NetworkViewModelFactory


class TrendingFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding= FragmentTrendingBinding.inflate(layoutInflater, container, false)

        //Initialising viewmodel
        val application= requireNotNull(this.activity).application
        val viewModelFactory= NetworkViewModelFactory(SUBREDDIT_TRENDING, application)
        val viewModel= ViewModelProvider(this,viewModelFactory).get(NetworkViewModel::class.java)

        //setting up Recycler View
        val adapter=MemeListAdapter(object: MemeClickListener {
            override fun onclickImage(meme: Meme) {
                findNavController().navigate(TrendingFragmentDirections.actionTrendingFragmentToDetailFragment(meme))
            }

        })
        binding.apply {
            this.trendingMemeList.adapter=adapter
            this.viewModel=viewModel
            this.lifecycleOwner=this@TrendingFragment
        }

        //Observing data coming from the internet
        viewModel.memeResponse.observe(viewLifecycleOwner,{
            adapter.differ.submitList(it.data?.memes)
        })
        return binding.root
    }
}
