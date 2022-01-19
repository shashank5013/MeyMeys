package com.example.android.meymeys.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.android.meymeys.R

@BindingAdapter("likes")
fun TextView.setLikes(likes:Int){
    this.text=likes.toString()
}

