package com.example.android.meymeys.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.android.meymeys.model.MemeResponse
import com.example.android.meymeys.repository.NetworkRepository
import com.example.android.meymeys.utils.COUNT
import kotlinx.coroutines.launch

class NetworkViewModel(private val subreddit:String,application: Application) : AndroidViewModel(application){

    //List of memes
    private val _memeList=MutableLiveData<MemeResponse>()
    val memeList:LiveData<MemeResponse>
    get() = _memeList

    //Repository variable
    private val repository=NetworkRepository()

    private val _testString=MutableLiveData<String>()
    val testString:LiveData<String>
        get() = _testString


    init {
        getMemesFromInternet()
    }

    /** Gets list of memes from the internet **/
    private fun getMemesFromInternet()=viewModelScope.launch {
        val response=repository.getMemesFromInternet("wholesomememes", COUNT)
        if(response.isSuccessful){
            _testString.value=response.body()
        }else{
            _testString.value="Error Occurred"
        }
    }
}