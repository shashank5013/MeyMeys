package com.example.android.meymeys.viewmodelfactory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.meymeys.viewmodel.NetworkViewModel
import java.lang.IllegalArgumentException

class NetworkViewModelFactory(
    private val subreddit:String,
    private val application: Application
) :ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NetworkViewModel::class.java)){
            return NetworkViewModel(subreddit,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")    }

}