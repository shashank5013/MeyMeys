package com.example.android.meymeys.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.android.meymeys.R
import com.example.android.meymeys.adapter.MemeClickListener
import com.example.android.meymeys.adapter.MemeListAdapter
import com.example.android.meymeys.databinding.FragmentFavouriteBinding
import com.example.android.meymeys.model.FavouriteMeme
import com.example.android.meymeys.model.Meme
import com.example.android.meymeys.utils.Resource
import com.example.android.meymeys.viewmodel.FavouriteViewModel
import com.example.android.meymeys.viewmodelfactory.FavouriteViewModelFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 * Use the [FavouriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavouriteFragment : Fragment() {

    //binding variable
    private lateinit var binding: FragmentFavouriteBinding

    //adapter variable
    private lateinit var adapter: MemeListAdapter

    //viewModel variable
    private lateinit var viewModel:FavouriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentFavouriteBinding.inflate(layoutInflater,container,false)

        setUpRecyclerView()

        //Initialising viewModel
        val application=requireActivity().application
        val viewModelFactory=FavouriteViewModelFactory(application)
        viewModel=ViewModelProvider(this,viewModelFactory)[FavouriteViewModel::class.java]

        viewModel.memeResponse.observe(viewLifecycleOwner){
                when (it) {
                    is Resource.Loading -> {
                        showProgressBar()
                        hideRecyclerView()
                        hideConnectionError()
                    }
                    is Resource.Success -> {
                        adapter.differ.submitList(it.data?.memes?.toList())

                        hideConnectionError()
                        hideProgressBar()
                        showRecyclerView()

                    }
                    else -> {
                        hideRecyclerView()
                        hideProgressBar()
                        showConnectionError()
                        Toast.makeText(requireContext(),it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

        //Observing Exception variable
        viewModel.exception.observe(viewLifecycleOwner){
            if(it==1){
                Toast.makeText(requireContext(),getString(R.string.success_delete),Toast.LENGTH_SHORT).show()
                viewModel.resetException()
            }
            else if(it==0){
                Toast.makeText(requireContext(),getString(R.string.error_text),Toast.LENGTH_SHORT).show()
                viewModel.resetException()
            }
        }

        //Handling gaping strategy so that list doesn't swap columns
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        layoutManager.invalidateSpanAssignments()
        binding.favouriteList.layoutManager = layoutManager

        //setting up listener on retry button of error page
        binding.favouriteErrorLayout.retryBtn.setOnClickListener{
            retrieveDataFromFirebase()
        }

        return binding.root
    }

    /** Retrieves data from firebase */
    private fun retrieveDataFromFirebase() {
        viewModel.getMemesFromFirebase()
    }
    /** Sets up Recycler View */
    private fun setUpRecyclerView() {
        adapter= MemeListAdapter(object : MemeClickListener{
            override fun onclickImage(meme: Meme) {
                findNavController().navigate(FavouriteFragmentDirections.actionFavouriteFragmentToDetailFragment(meme))
            }

            override fun onclickShare(uri: Uri) {
                findNavController().navigate(FavouriteFragmentDirections.actionFavouriteFragmentToShareFragment(uri))
            }

            override fun onclickFavourite(meme: Meme) {
                val uid=Firebase.auth.uid!!
                val favouriteMeme=FavouriteMeme(meme,uid)
                viewModel.deleteMemeFromFirebase(favouriteMeme)
            }


        },true)
        binding.favouriteList.adapter=adapter
    }

    /** shows recycler view */
    private fun showRecyclerView(){
        binding.favouriteList.visibility=View.VISIBLE
    }

    /** hides recycler view */
    private fun hideRecyclerView(){
        binding.favouriteList.visibility=View.GONE

    }

    /** shows progress bar */
    private fun showProgressBar(){
        binding.favouriteShimmerProgressBar.apply {
            startShimmer()
            visibility=View.VISIBLE
        }
    }

    /** hides progress bar */
    private fun hideProgressBar(){
        binding.favouriteShimmerProgressBar.apply {
            stopShimmer()
            visibility=View.GONE
        }
    }

    /** shows connection error */
    private fun showConnectionError(){
        binding.favouriteError.visibility=View.VISIBLE
    }

    /** hides connection error */
    private fun hideConnectionError(){
        binding.favouriteError.visibility=View.GONE
    }

}