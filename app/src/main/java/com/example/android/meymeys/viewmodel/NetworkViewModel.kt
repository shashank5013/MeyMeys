package com.example.android.meymeys.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.meymeys.model.MemeResponse
import com.example.android.meymeys.repository.NetworkRepository
import com.example.android.meymeys.utils.COUNT
import com.example.android.meymeys.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NetworkViewModel(private val subreddit:String,application: Application) : AndroidViewModel(application){

    //List of memes
    private val _memeResponse=MutableLiveData<Resource<MemeResponse>>()
    val memeResponse:LiveData<Resource<MemeResponse>>
    get() = _memeResponse

    //Repository variable
    private val repository=NetworkRepository()

    //Spinner selected item
    var spinnerPosition=-1

    /** Gets list of memes from the internet **/
    fun getMemesFromInternet(subreddit: String)=viewModelScope.launch {
        _memeResponse.value=Resource.Loading()
        val response=repository.getMemesFromInternet(subreddit, COUNT)
        handleResponse(response)
    }

    /** Handles responses received to find success of error */
    private fun handleResponse(response:Response<MemeResponse>){
        if(response.isSuccessful){
            response.body()?.let {
                _memeResponse.value=Resource.Success(it)
            }
        }
        else{
            _memeResponse.value=Resource.Error("Failed")
        }
    }
}