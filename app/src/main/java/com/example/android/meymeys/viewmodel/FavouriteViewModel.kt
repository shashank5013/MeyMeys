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
import com.example.android.meymeys.model.FavouriteMeme
import com.example.android.meymeys.model.Meme
import com.example.android.meymeys.model.MemeResponse
import com.example.android.meymeys.repository.NetworkRepository
import com.example.android.meymeys.utils.Resource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FavouriteViewModel(
    application: Application
) :AndroidViewModel(application){

    //MemeResponse variable
    private val _memeResponse=MutableLiveData<Resource<MemeResponse>>()
    val memeResponse:LiveData<Resource<MemeResponse>>
    get() = _memeResponse

    // Exception Boolean variable
    // 0 represents Exception
    // 1 represents Success
    // -1 represents neutral state
    private val _exception=MutableLiveData<Int>()
    val exception:LiveData<Int>
    get() = _exception

    //Repository variable
    private val repository=NetworkRepository()

    init {
        getMemesFromFirebase()
        subscribeToRealtimeUpdates()

    }

    /** Updates the response variable if there is some change in data in firebase */
    private fun subscribeToRealtimeUpdates() {
        val dataCollectionRef=Firebase.firestore.collection("Memes")
        dataCollectionRef.addSnapshotListener { querySnapshot, error ->
            querySnapshot?.let {
                val memeList= mutableListOf<Meme>()
                for (document in querySnapshot.documents){
                    document.toObject(FavouriteMeme::class.java)?.let { memeList.add(it.meme!!) }
                }
                _memeResponse.value=Resource.Success(MemeResponse(memeList.size,memeList))
            }
        }
    }

    /** Gets memes from firebase */
    fun getMemesFromFirebase()=viewModelScope.launch {
        _memeResponse.value=Resource.Loading()
        val uid=Firebase.auth.uid!!
        try {
            if(hasInternetConnection()){
                val data=repository.getMemesFromFirebase(uid)
                _memeResponse.value=Resource.Success(MemeResponse(data.size,data))
            }
            else{
                throw Exception("No Internet")
            }
        }catch (e:Exception){
            _memeResponse.value=Resource.Error(e.message)
        }

    }

    /** Deletes memes from firebase */
    fun deleteMemeFromFirebase(meme:FavouriteMeme)=viewModelScope.launch{
        if(hasInternetConnection()){
            try {
                repository.deleteMemeFromFirebase(meme)
                _exception.value=1
            }
            catch (e:Exception){
                _exception.value=0
            }
        }
        else{
            _exception.value=0
        }
    }

    fun resetException(){
        _exception.value=-1
    }

    /** Finds whether there is internet connectivity or not */
    private fun hasInternetConnection():Boolean{
        val connectivityManager=getApplication<BaseApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork=connectivityManager.activeNetwork?:return false
        val capabilities=connectivityManager.getNetworkCapabilities(activeNetwork)?:return false
        return when{
            capabilities.hasTransport(TRANSPORT_WIFI)->true
            capabilities.hasTransport(TRANSPORT_CELLULAR)->true
            capabilities.hasTransport(TRANSPORT_ETHERNET)->true
            else->false
        }
    }


}