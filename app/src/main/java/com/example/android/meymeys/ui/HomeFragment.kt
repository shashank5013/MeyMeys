package com.example.android.meymeys.ui

import android.app.Application
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.android.meymeys.R
import com.example.android.meymeys.adapter.MemeClickListener
import com.example.android.meymeys.adapter.MemeListAdapter
import com.example.android.meymeys.databinding.FragmentHomeBinding
import com.example.android.meymeys.model.FavouriteMeme
import com.example.android.meymeys.model.Meme
import com.example.android.meymeys.utils.QUERY_PAGE_SIZE
import com.example.android.meymeys.utils.Resource
import com.example.android.meymeys.utils.SUBREDDIT_HOME
import com.example.android.meymeys.utils.Subreddits
import com.example.android.meymeys.viewmodel.NetworkViewModel
import com.example.android.meymeys.viewmodelfactory.NetworkViewModelFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


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
        viewModel.memeResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showProgressBar()
                    hideRecyclerView()
                    hideConnectionError()
                    viewModel.spinnerPosition = binding.categoriesSpinner.selectedItemPosition
                }
                is Resource.LoadingExtra -> {
                    hideProgressBar()
                    showRecyclerView()
                }
                is Resource.Success -> {
                    adapter.differ.submitList(it.data?.memes?.toList())

                    hideConnectionError()
                    hideProgressBar()
                    showRecyclerView()

                }
                is Resource.ErrorExtra -> {
                    hideProgressBar()
                    hideConnectionError()
                    showRecyclerView()
                }
                else -> {
                    hideRecyclerView()
                    hideProgressBar()
                    showConnectionError()
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                }
            }
        }

        //Observing Exception Variable
        viewModel.exception.observe(viewLifecycleOwner){
            if(it==0){
                Toast.makeText(requireContext(),getString(R.string.error_text),Toast.LENGTH_SHORT).show()
                viewModel.resetException()
            }
            else if(it==1){
                Toast.makeText(requireContext(),getString(R.string.success_save),Toast.LENGTH_SHORT).show()
                viewModel.resetException()
            }
        }

        //Handling gaping strategy so that list doesn't swap columns
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        layoutManager.invalidateSpanAssignments()
        binding.homeMemeList.layoutManager = layoutManager


        //Setting onClickListener on retry button of connection error layout
        binding.errorLayout.retryBtn.setOnClickListener{
            makeNetworkCall()
        }


        setUpSpinner(application)

        //Enables options menu
        setHasOptionsMenu(true)

        return binding.root
    }

    /** Inflates the options menu */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /** Action to be performed when menu item is clicked */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,findNavController())||super.onOptionsItemSelected(item)
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

    /** Hides Connection Error layout */
    private fun hideConnectionError(){
        binding.error.visibility=View.GONE
    }
    /** Shows Connection Error layout */
    private fun showConnectionError(){
        binding.error.visibility=View.VISIBLE
    }

    /** Setting up spinner for categories */
    private fun setUpSpinner(application: Application) {
        val spinnerArray = Subreddits.keys.toTypedArray()
        val spinnerAdapter =
            ArrayAdapter(application, android.R.layout.simple_spinner_item, spinnerArray).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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

            override fun onclickShare(uri: Uri) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToShareFragment(uri))
            }

            override fun onclickFavourite(meme: Meme) {
                    val uid= Firebase.auth.currentUser!!.uid
                    val finalMeme= FavouriteMeme(meme,uid)
                    viewModel.uploadMemeToFirebase(finalMeme)
            }
        },false)
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
                makeNetworkCall()
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }


    /** Makes network call with view model . Handles any exception and no internet*/
    private fun makeNetworkCall(){
        adapter.differ.submitList(listOf())
        val subreddit = binding.categoriesSpinner.selectedItem as String
        viewModel.getMemesFromInternet(Subreddits[subreddit]!!)
    }


}


