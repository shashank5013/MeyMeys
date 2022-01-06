package com.example.android.meymeys.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android.meymeys.databinding.ActivityMainBinding
import com.example.android.meymeys.databinding.ListItemBinding
import com.example.android.meymeys.model.Meme

class MemeListAdapter : RecyclerView.Adapter<MemeListAdapter.ViewHolder>() {

    /** Async List Differ object which calculates difference between two lists faster
     * @param adapter Recycler View Adapter
     * @param callback DiffUtil callback object
     */
    val differ=AsyncListDiffer<Meme>(this,object : DiffUtil.ItemCallback<Meme>(){
        override fun areItemsTheSame(oldItem: Meme, newItem: Meme): Boolean {
            return oldItem.postLink==newItem.postLink
        }

        override fun areContentsTheSame(oldItem: Meme, newItem: Meme): Boolean {
            return oldItem == newItem
        }

    })

    class ViewHolder(private val binding: ListItemBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(meme:Meme){
            binding.apply {
                this.meme=meme
                executePendingBindings()
            }
        }

        companion object{
            fun from (parent:ViewGroup):ViewHolder{
                return ViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val meme=differ.currentList[position]
        holder.bind(meme)
    }

    override fun getItemCount()=differ.currentList.size


}