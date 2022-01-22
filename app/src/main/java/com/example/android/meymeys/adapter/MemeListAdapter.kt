package com.example.android.meymeys.adapter

import android.app.Application
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.android.meymeys.R
import com.example.android.meymeys.databinding.ListItemBinding
import com.example.android.meymeys.model.Meme

class MemeListAdapter(private val listener: MemeClickListener) : RecyclerView.Adapter<MemeListAdapter.ViewHolder>() {

    /** Async List Differ object which calculates difference between two lists faster
     * @param adapter Recycler View Adapter
     * @param callback DiffUtil callback object
     */
    val differ=AsyncListDiffer(this,object : DiffUtil.ItemCallback<Meme>(){
        override fun areItemsTheSame(oldItem: Meme, newItem: Meme): Boolean {
            return oldItem.url==newItem.url
        }

        override fun areContentsTheSame(oldItem: Meme, newItem: Meme): Boolean {
            return oldItem == newItem
        }

    })

    class ViewHolder(private val binding: ListItemBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(meme:Meme,listener: MemeClickListener){
            binding.apply {
                this.meme=meme
                executePendingBindings()
                setImage(meme, listener)

            }
        }

        private fun setImage(
            meme: Meme,
            listener: MemeClickListener
        ) {
            Glide.with(binding.memeImage.context)
                .load(meme.url)
                .placeholder(
                    AppCompatResources.getDrawable(
                        binding.memeImage.context,
                        R.drawable.ic_meme_placeholder
                    )
                )
                .thumbnail(0.05f)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.memeImage.setOnClickListener { }
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.memeImage.setOnClickListener {
                            listener.onclickImage(meme, it as ImageView)
                        }
                        return false
                    }

                })
                .into(binding.memeImage)
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
        holder.bind(meme,listener)
    }

    override fun getItemCount()=differ.currentList.size



}
interface MemeClickListener{
    fun onclickImage(meme: Meme,imageView:ImageView)
}