package com.example.android.meymeys.adapter

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.android.meymeys.BuildConfig
import com.example.android.meymeys.R
import com.example.android.meymeys.databinding.ListItemBinding
import com.example.android.meymeys.model.Meme
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.*

class MemeListAdapter(private val listener: MemeClickListener) : RecyclerView.Adapter<MemeListAdapter.ViewHolder>() {

    // Time of last click in ms
    companion object{
        var mClickTime=0L
    }

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

    /** ViewHolder class which  provides views to recycler view . Only ViewHolder class should know about views are laid out
     * @param binding ListItemBinding variable
      */
    class ViewHolder(private val binding: ListItemBinding):RecyclerView.ViewHolder(binding.root){

        /** Binds layout to views
         * @param meme Meme object
         * @param listener listener object
         */
        fun bind(meme:Meme,listener: MemeClickListener){
            binding.apply {
                this.meme=meme
                executePendingBindings()
                setImage(meme, listener)

            }
        }

        /** Sets listener and images when image  has been loaded */
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
                        binding.shareImage.setOnClickListener {  }
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
                            listener.onclickImage(meme)
                        }
                        resource?.let {drawable->
                            val uri=shareImage(drawable)
                            binding.shareImage.setOnClickListener {
                                if(System.currentTimeMillis()-mClickTime>=2000L){
                                    listener.onclickShare(uri)
                                    mClickTime=System.currentTimeMillis()
                                }
                            }
                        }
                        return false
                    }

                })
                .into(binding.memeImage)
        }

        /** Shares jpg/gif images through android ShareSheet */
        private fun shareImage(drawable: Drawable): Uri {

            val isGif: Boolean = binding.meme!!.url.split('.').last() == "gif"


            val filePath: String = saveToDir(drawable, isGif)
            val context = binding.root.context
            val file = File(filePath)
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "image/*"
            return FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".provider", file
            )

        }

        /** Downloads image to DIR and returns the file path */
        private fun saveToDir (image: Drawable, isGif: Boolean): String {

            if(isGif){
                val byteBuffer = (image as GifDrawable).buffer
                val fileName = "MeyMeys${System.currentTimeMillis()}.gif"
                val filePath = "${binding.root.context.cacheDir}/$fileName"
                val gifFile = File(filePath)
                val output = FileOutputStream(gifFile)
                val bytes = ByteArray(byteBuffer.capacity())

                (byteBuffer.duplicate().clear() as ByteBuffer).get(bytes)
                output.write(bytes, 0 ,bytes.size)
                output.close()

                return filePath
            }

            val fileName = "MeyMeys${System.currentTimeMillis()}.jpg"
            val filePath = "${binding.root.context.cacheDir}/$fileName"

            val file = File(filePath)
            FileOutputStream(file).use { output ->
                image.toBitmap().compress(Bitmap.CompressFormat.JPEG, 100, output)
            }

            return filePath

        }


        /** Returns ViewHolder object */
        companion object{
            fun from (parent:ViewGroup):ViewHolder{
                return ViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            }
        }
    }

    /** Creates a ViewHolder class object
     * @param parent parent viewgroup
     * @param viewType which view to be laid out . Useful if there are different type of views
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    /** Determines how view should be laid out . Binds the view with layou
     * @param holder ViewHolder object
     * @param position position of the current list item
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val meme=differ.currentList[position]
        holder.bind(meme,listener)
    }

    /** Returns the size of current list */
    override fun getItemCount()=differ.currentList.size

}

/** ClickListener interface which determines what to do after image is clicked */
interface MemeClickListener{
    fun onclickImage(meme: Meme)
    fun onclickShare(uri:Uri)
}