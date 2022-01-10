package com.example.android.meymeys.api

import com.example.android.meymeys.model.MemeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MemeAPI {
    @GET("{subreddit}/{count}")
    suspend fun getMeme(
        @Path("subreddit") subreddit:String,
        @Path("count") count:Int
    ): Response<MemeResponse>
}