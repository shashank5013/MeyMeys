package com.example.android.meymeys.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.android.meymeys.R

@BindingAdapter("imageURL")
fun ImageView.setImageFromURL(url:String){
    val imageURL=url.toUri().buildUpon().scheme("https").build()
    Glide.with(context).
    load(imageURL).placeholder(R.drawable.ic_meme_placeholder).
    thumbnail(0.5f).transition(DrawableTransitionOptions.withCrossFade()).
    into(this)
}

@BindingAdapter("likes")
fun TextView.setLikes(likes:Int){
    this.text=likes.toString()
}

