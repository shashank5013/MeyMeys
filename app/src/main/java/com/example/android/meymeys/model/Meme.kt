package com.example.android.meymeys.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Meme(
    val author: String,
    val nsfw: Boolean,
    val postLink: String,
    val preview: List<String>,
    val spoiler: Boolean,
    val subreddit: String,
    val title: String,
    val ups: Int,
    val url: String
):Parcelable