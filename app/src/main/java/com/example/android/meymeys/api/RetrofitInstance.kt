package com.example.android.meymeys.api

import com.example.android.meymeys.utils.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitInstance {

    private val moshi= Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val api=Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
        .create(MemeAPI::class.java)

}