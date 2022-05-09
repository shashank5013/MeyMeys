package com.example.android.meymeys.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Meme(
    var author: String="",
    var nsfw: Boolean=true,
    var postLink: String="",
    var preview: List<String>?=null,
    var spoiler: Boolean=true,
    var subreddit: String="",
    var title: String="",
    var ups: Int=0,
    var url: String=""
):Parcelable