package com.example.android.meymeys.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.android.meymeys.adapter.MemeClickListener
import com.example.android.meymeys.adapter.MemeListAdapter
import com.example.android.meymeys.databinding.FragmentHomeBinding
import com.example.android.meymeys.model.Meme
import com.example.android.meymeys.utils.Resource
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

        postponeEnterTransition()

        //Initialising viewmodel
        val application= requireNotNull(this.activity).application
        val viewModelFactory=NetworkViewModelFactory(SUBREDDIT_HOME, application)
        val viewModel=ViewModelProvider(this,viewModelFactory).get(NetworkViewModel::class.java)

        //setting up Recycler View
        val adapter=MemeListAdapter(object:MemeClickListener{
            override fun onclickImage(meme: Meme,imageView: ImageView) {
                val extras= FragmentNavigator.Extras.Builder()
                    .addSharedElement(imageView ,meme.url)
                    .build()
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDetailFragment(meme),extras)
            }

        })
        binding.apply {
            this.homeMemeList.adapter=adapter
            this.viewModel=viewModel
            this.lifecycleOwner=this@HomeFragment
        }



        //Observing data coming from the internet
        viewModel.memeResponse.observe(viewLifecycleOwner,{
            when(it){
                is Resource.Loading ->{
                    binding.apply {
                        homeMemeList.visibility=View.GONE
                        progressBar.visibility=View.VISIBLE
                    }
                }
                is Resource.Success ->{
                    adapter.differ.submitList(it.data?.memes)
                    binding.apply {
                        homeMemeList.visibility=View.VISIBLE
                        progressBar.visibility=View.GONE
                    }
                    // Start the transition once all views have been
                    // measured and laid out
                    (binding.root.parent as? ViewGroup)?.doOnPreDraw{
                        startPostponedEnterTransition()
                    }

                }
                else -> {
                    binding.apply {
                        homeMemeList.visibility=View.VISIBLE
                        progressBar.visibility=View.GONE
                    }
                    Toast.makeText(context,"Error Occured",Toast.LENGTH_SHORT).show()
                }
            }
        })
        return binding.root
    }

}