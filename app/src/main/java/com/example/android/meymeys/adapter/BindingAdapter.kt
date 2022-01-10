package com.example.android.meymeys.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("imageURL")
fun ImageView.setImageFromURL(url:String){
    val imageURL=url.toUri().buildUpon().scheme("https").build()
    Glide.with(context).load(imageURL).into(this)
}

@BindingAdapter("likes")
fun TextView.setLikes(likes:Int){
    this.text=likes.toString()
}

