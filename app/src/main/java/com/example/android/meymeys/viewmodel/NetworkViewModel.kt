package com.example.android.meymeys.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.meymeys.model.MemeResponse
import com.example.android.meymeys.repository.NetworkRepository
import com.example.android.meymeys.utils.COUNT
import kotlinx.coroutines.launch

class NetworkViewModel(private val subreddit:String,application: Application) : AndroidViewModel(application){

    //List of memes
    private val _memeResponse=MutableLiveData<MemeResponse>()
    val memeResponse:LiveData<MemeResponse>
    get() = _memeResponse

    //Repository variable
    private val repository=NetworkRepository()


    init {
        getMemesFromInternet()
    }

    /** Gets list of memes from the internet **/
    private fun getMemesFromInternet()=viewModelScope.launch {
        val response=repository.getMemesFromInternet("wholesomememes", COUNT)
        if(response.isSuccessful){
            _memeResponse.value=response.body()
        }
    }
}