package com.example.android.meymeys.ui

import android.R
import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.doOnLayout
import androidx.core.view.doOnNextLayout
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.android.meymeys.adapter.MemeClickListener
import com.example.android.meymeys.adapter.MemeListAdapter
import com.example.android.meymeys.databinding.FragmentHomeBinding
import com.example.android.meymeys.model.Meme
import com.example.android.meymeys.utils.Resource
import com.example.android.meymeys.utils.SUBREDDIT_HOME
import com.example.android.meymeys.utils.Subreddits
import com.example.android.meymeys.viewmodel.NetworkViewModel
import com.example.android.meymeys.viewmodelfactory.NetworkViewModelFactory


class HomeFragment : Fragment() , AdapterView.OnItemSelectedListener {


    //Binding Variable
    private lateinit var binding: FragmentHomeBinding

    //ViewModel variable
    private lateinit var viewModel: NetworkViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentHomeBinding.inflate(layoutInflater, container, false)


        //Initialising viewmodel
        val application= requireNotNull(this.activity).application
        val viewModelFactory=NetworkViewModelFactory(SUBREDDIT_HOME, application)
        viewModel=ViewModelProvider(this,viewModelFactory).get(NetworkViewModel::class.java)



        val adapter = setUpRecyclerView()


        postponeEnterTransition()


        //Observing data coming from the internet
        viewModel.memeResponse.observe(viewLifecycleOwner,{
            when(it){
                is Resource.Loading ->{
                    showProgressBar()
                    hideRecyclerView()
                    adapter.differ.submitList(listOf())

                }
                is Resource.Success ->{
                    adapter.differ.submitList(it.data?.memes?.toList())


                    hideProgressBar()
                    showRecyclerView()

                    // Start the transition once all views have been
                    // measured and laid out
                    (binding.root.parent as? ViewGroup)?.doOnPreDraw{
                        startPostponedEnterTransition()
                    }

                }
                else -> {

                    showRecyclerView()
                    hideProgressBar()


                    Toast.makeText(context,"Error Occured",Toast.LENGTH_SHORT).show()
                }
            }
        })

        //Handling gaping strategy so that list doesn't swap columns
        val layoutManager=StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy=StaggeredGridLayoutManager.GAP_HANDLING_NONE
        layoutManager.invalidateSpanAssignments()
        binding.homeMemeList.layoutManager=layoutManager


        setUpSpinner(application)


        return binding.root
    }

    /** Shows Progress Bar */
    private fun showProgressBar(){
        binding.progressBar.visibility=View.VISIBLE
    }

    /** Hides Progress Bar */
    private fun hideProgressBar(){
        binding.progressBar.visibility=View.GONE
    }

    /** Shows Recycler View */
    private fun showRecyclerView(){
        binding.homeMemeList.visibility=View.VISIBLE
    }

    /** Hides Recycler View */
    private fun hideRecyclerView(){
        binding.homeMemeList.visibility=View.INVISIBLE
    }

    /** Setting up spinner for categories */
    private fun setUpSpinner(application: Application) {
        val spinnerArray = Subreddits.keys.toTypedArray()
        val spinnerAdapter =
            ArrayAdapter(application, R.layout.simple_spinner_item, spinnerArray).also {
                it.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            }
        binding.categoriesSpinner.apply {
            setSelection(0)
            this.adapter = spinnerAdapter
            onItemSelectedListener = this@HomeFragment
        }
    }

    /** Setting up Recycler View */
    private fun setUpRecyclerView(): MemeListAdapter {
        //setting up Recycler View
        val adapter = MemeListAdapter(object : MemeClickListener {
            override fun onclickImage(meme: Meme, imageView: ImageView) {
                val extras = FragmentNavigator.Extras.Builder()
                    .addSharedElement(imageView, meme.url)
                    .build()
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToDetailFragment(
                        meme
                    ), extras
                )
            }

        })
        binding.apply {
            this.homeMemeList.adapter = adapter
            this.viewModel = viewModel
            this.lifecycleOwner = this@HomeFragment
        }
        return adapter
    }


    /** Implementing onclick listeners for spinner list items */
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        parent?.let {
            if(position!=viewModel.spinnerPosition){
                viewModel.spinnerPosition=position
                val subreddit=it.getItemAtPosition(position) as String
                viewModel.getMemesFromInternet(Subreddits[subreddit]!!)
            }
        }
    }
    override fun onNothingSelected(parent: AdapterView<*>?) {

    }



}


