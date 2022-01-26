package com.example.android.meymeys.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.meymeys.application.BaseApplication
import com.example.android.meymeys.model.Meme
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

    //Contains all memes
    var allMemes:MemeResponse?=null

    //Repository variable
    private val repository=NetworkRepository()

    //Spinner selected item
    var spinnerPosition=-1

    /** Gets extra list of memes from the internet **/
    fun getExtraMemesFromInternet(subreddit: String)=viewModelScope.launch {
        _memeResponse.value=Resource.LoadingExtra()
        try{
            if(hasInternetConnection()){
                val response=repository.getMemesFromInternet(subreddit, COUNT)
                _memeResponse.value=handleResponse(response,true)
            }
            else{
                throw Throwable()
            }
        }catch (t:Throwable){
            _memeResponse.value=Resource.ErrorExtra("")
        }
    }

    /** Gets  list of memes from the internet **/
    fun getMemesFromInternet(subreddit: String)=viewModelScope.launch {
        _memeResponse.value=Resource.Loading()
        try{
            if(hasInternetConnection()){
                val response=repository.getMemesFromInternet(subreddit, COUNT)
                _memeResponse.value=handleResponse(response,false)
            }
            else{
                throw Throwable()
            }
        }catch (t:Throwable){
            _memeResponse.value=Resource.Error("")
        }
    }

    /** Handles responses received to find success or error */
    private fun handleResponse(response:Response<MemeResponse>,extraMemesRequired:Boolean):Resource<MemeResponse>{
        if(response.isSuccessful){
            response.body()?.let {
               if(extraMemesRequired){
                   allMemes?.memes?.addAll(it.memes)
               }else{
                   allMemes=it
               }
                return Resource.Success(allMemes?:it)
            }
        }

        return Resource.Error("Error Occurred")
    }

    /** Checks Internet Connection */
    fun hasInternetConnection():Boolean{
        val connectivityManager=getApplication<BaseApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork=connectivityManager.activeNetwork?:return false
        val capabilities=connectivityManager.getNetworkCapabilities(activeNetwork)?:return false
        return when{
             capabilities.hasTransport(TRANSPORT_WIFI) -> return true
            capabilities.hasTransport(TRANSPORT_CELLULAR) -> return true
            capabilities.hasTransport(TRANSPORT_ETHERNET) -> return true
            else -> false
        }
    }
}