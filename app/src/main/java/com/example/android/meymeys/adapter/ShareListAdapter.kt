package com.example.android.meymeys.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android.meymeys.databinding.ShareItemBinding
import com.example.android.meymeys.model.ShareItem

class ShareListAdapter(private val listener: ShareClickListener,private val uri: Uri) : RecyclerView.Adapter<ShareListAdapter.ViewHolder>(){

    /** Async List Differ object which calculates difference between two lists faster
     * @param adapter Recycler View Adapter
     * @param callback DiffUtil callback object
     */
    val differ=AsyncListDiffer(this,object : DiffUtil.ItemCallback<ShareItem>(){
        override fun areItemsTheSame(oldItem: ShareItem, newItem: ShareItem): Boolean {
            return oldItem.packageName == newItem.packageName
        }

        override fun areContentsTheSame(oldItem: ShareItem, newItem: ShareItem): Boolean {
            return oldItem == newItem
        }

    })

    /** ViewHolder class which  provides views to recycler view . Only ViewHolder class should know about views are laid out
     * @param binding ShareItemBinding variable
     */
    class ViewHolder(private val binding:ShareItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: ShareItem,listener: ShareClickListener,uri: Uri){
            binding.iconImage.setImageDrawable(item.drawable)
            binding.labelText.text=item.name
            binding.iconImage.setOnClickListener {
                listener.onClick(uri,item)
            }
        }

        /** Returns ViewHolder object */

        companion object {
            fun from(parent: ViewGroup) =
                ViewHolder(ShareItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    /** Creates a ViewHolder class object
     * @param parent parent viewgroup
     * @param viewType which view to be laid out . Useful if there are different type of views
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    /** Determines how view should be laid out . Binds the view with layout
     * @param holder ViewHolder object
     * @param position position of the current list item
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=differ.currentList[position]
        holder.bind(item,listener,uri)
    }

    /** Returns the size of current list **/
    override fun getItemCount()=differ.currentList.size

}


interface ShareClickListener{
    fun onClick(uri: Uri, shareItem:ShareItem)
}