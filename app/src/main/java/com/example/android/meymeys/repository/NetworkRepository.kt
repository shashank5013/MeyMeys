package com.example.android.meymeys.repository

import com.example.android.meymeys.api.RetrofitInstance

class NetworkRepository {
    suspend fun getMemesFromInternet(subreddit:String,count:Int)=RetrofitInstance.api.getMeme(subreddit,count)
}