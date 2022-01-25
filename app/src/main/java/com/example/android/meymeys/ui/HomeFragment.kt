package com.example.android.meymeys.ui

import android.R
import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.android.meymeys.adapter.MemeClickListener
import com.example.android.meymeys.adapter.MemeListAdapter
import com.example.android.meymeys.databinding.FragmentHomeBinding
import com.example.android.meymeys.model.Meme
import com.example.android.meymeys.utils.QUERY_PAGE_SIZE
import com.example.android.meymeys.utils.Resource
import com.example.android.meymeys.utils.SUBREDDIT_HOME
import com.example.android.meymeys.utils.Subreddits
import com.example.android.meymeys.viewmodel.NetworkViewModel
import com.example.android.meymeys.viewmodelfactory.NetworkViewModelFactory


class HomeFragment : Fragment(), AdapterView.OnItemSelectedListener {


    //Binding Variable
    private lateinit var binding: FragmentHomeBinding

    //ViewModel variable
    private lateinit var viewModel: NetworkViewModel

    //RecyclerView Adapter
    private lateinit var adapter:MemeListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)


        //Initialising viewmodel
        val application = requireNotNull(this.activity).application
        val viewModelFactory = NetworkViewModelFactory(SUBREDDIT_HOME, application)
        viewModel = ViewModelProvider(this, viewModelFactory)[NetworkViewModel::class.java]


        adapter = setUpRecyclerView()




        //Observing data coming from the internet
        viewModel.memeResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                   if(binding.categoriesSpinner.selectedItemPosition!=viewModel.spinnerPosition){
                       showProgressBar()
                       hideRecyclerView()
                       viewModel.spinnerPosition=binding.categoriesSpinner.selectedItemPosition
                   }
                }
                is Resource.Success -> {
                    adapter.differ.submitList(it.data?.memes?.toList())


                    hideProgressBar()
                    showRecyclerView()


                }
                else -> {

                    showRecyclerView()
                    hideProgressBar()

                    Toast.makeText(context, "Error Occured", Toast.LENGTH_SHORT).show()
                }
            }
        })

        //Handling gaping strategy so that list doesn't swap columns
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        layoutManager.invalidateSpanAssignments()
        binding.homeMemeList.layoutManager = layoutManager


        setUpSpinner(application)


        return binding.root
    }

    /** Shows Progress Bar */
    private fun showProgressBar() {
        binding.shimmerProgressBar.apply {
            visibility=View.VISIBLE
            this.startShimmer()

        }
        isLoading = true
    }

    /** Hides Progress Bar */
    private fun hideProgressBar() {
        binding.shimmerProgressBar.apply {
            visibility=View.GONE
            this.stopShimmer()
        }
        isLoading = false
    }

    /** Shows Recycler View */
    private fun showRecyclerView() {
        binding.homeMemeList.visibility = View.VISIBLE
    }

    /** Hides Recycler View */
    private fun hideRecyclerView() {
        binding.homeMemeList.visibility = View.INVISIBLE
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


    /** Sets up infinite scrolling for Recycler view */
    var isLoading = false
    var isLastPage = false
    var isScrolling = false
    private fun setUpScrollListener(): RecyclerView.OnScrollListener {
        return object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = binding.homeMemeList.layoutManager as StaggeredGridLayoutManager
                val firstVisibleItem = layoutManager.findFirstVisibleItemPositions(null)
                val firstVisibleItemPosition = firstVisibleItem[0]
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount

                val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
                val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
                val isNotAtBeginning = firstVisibleItemPosition > 0
                val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE

                val shouldPaginate =
                    isNotLoadingAndNotLastPage and isAtLastItem and isNotAtBeginning and isTotalMoreThanVisible and isScrolling

                if (shouldPaginate) {
                    val subreddit = binding.categoriesSpinner.selectedItem as String
                    viewModel.getExtraMemesFromInternet(Subreddits[subreddit]!!)
                    isScrolling = false
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }
        }
    }

    /** Setting up Recycler View */
    private fun setUpRecyclerView(): MemeListAdapter {
        val adapter = MemeListAdapter(object : MemeClickListener {
            override fun onclickImage(meme: Meme) {
               findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDetailFragment(meme))
            }
        },)
        val listener = setUpScrollListener()
        binding.apply {
            this.homeMemeList.adapter = adapter
            this.homeMemeList.addOnScrollListener(listener)
            this.viewModel = viewModel
            this.lifecycleOwner = this@HomeFragment
        }
        return adapter
    }


    /** Implementing onclick listeners for spinner list items */
    override fun onItemSelected(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ) {
        parent?.let {
            if (position != viewModel.spinnerPosition) {
                adapter.differ.submitList(listOf())
                val subreddit = it.getItemAtPosition(position) as String
                viewModel.getMemesFromInternet(Subreddits[subreddit]!!)
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }


}


