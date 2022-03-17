package com.example.android.meymeys.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.example.android.meymeys.adapter.ShareClickListener
import com.example.android.meymeys.adapter.ShareListAdapter
import com.example.android.meymeys.databinding.FragmentShareBinding
import com.example.android.meymeys.model.ShareItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ShareFragment : BottomSheetDialogFragment() {

    //binding variable
    private lateinit var binding: FragmentShareBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
       binding=FragmentShareBinding.inflate(layoutInflater,container,false)

        //Dismissing the share dialog when cancel is pressed
        binding.cancelButton.setOnClickListener{
            dismiss()
        }

        //Parent Activity
        val activity=requireActivity()

        // Image uri to be shared
        val args :ShareFragmentArgs by navArgs()
        val uri=args.imageUri


        val intent= Intent(Intent.ACTION_SEND).apply {
            type="image/*"
            putExtra(Intent.EXTRA_STREAM,uri)
        }

        // Creating a list of shareable activities
        val activities=requireContext().packageManager.queryIntentActivities(intent,0x00010000)

        val shareItemList= mutableListOf<ShareItem>()

        for(data in activities){
            shareItemList.add(
                ShareItem(
                    data.loadLabel(activity.packageManager).toString(),
                    data.loadIcon(activity.packageManager),
                    data.activityInfo.packageName
                )
            )
        }

        setUpRecyclerView(shareItemList,uri)
        return binding.root
    }

    /** Sets up recycler view and submits list
     * @param shareItemList List of activities that support share
     * @param uri uri of the image to be shared
     */
    private fun setUpRecyclerView(shareItemList: MutableList<ShareItem>,uri: Uri) {
        val adapter=ShareListAdapter(object : ShareClickListener {

            /** On click listener for share fragment dialog */
            override fun onClick(uri: Uri, shareItem: ShareItem) {
                val intent=Intent(Intent.ACTION_SEND).apply {
                    type="image/*"
                    putExtra(Intent.EXTRA_STREAM,uri)
                    setPackage(shareItem.packageName)
                }
                if(requireActivity().packageManager.resolveActivity(intent,0)!=null){
                    startActivity(intent)
                    dismiss()
                }
                else{
                    Toast.makeText(requireContext(),"Application Not Installed",Toast.LENGTH_SHORT).show()
                }
            }

        },uri)
        binding.shareList.adapter=adapter
        adapter.differ.submitList(shareItemList.toList())
    }

}