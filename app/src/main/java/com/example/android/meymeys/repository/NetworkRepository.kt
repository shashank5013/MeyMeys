package com.example.android.meymeys.repository

import android.util.Log
import com.example.android.meymeys.api.RetrofitInstance
import com.example.android.meymeys.model.FavouriteMeme
import com.example.android.meymeys.model.Meme
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.jvm.Throws

class NetworkRepository {

    /** Gets Memes from the internet
     * @param subreddit name
     * @param count number of memes required
     */
    suspend fun getMemesFromInternet(subreddit:String,count:Int)=RetrofitInstance.api.getMeme(subreddit,count)

    /** Gets favourite memes from firebase */
    suspend fun getMemesFromFirebase(uid:String):MutableList<Meme>{
        val memeList= mutableListOf<Meme>()
        withContext(Dispatchers.IO){
            try {
                val dataCollectionRef=Firebase.firestore.collection("Memes")
                val querySnapshot=dataCollectionRef.whereEqualTo("uid",uid).get().await()
                for (document in querySnapshot.documents){
                    document.toObject(FavouriteMeme::class.java)?.let { memeList.add(it.meme!!) }
                }
            }catch (e:Exception){
                throw e
            }
        }
        return memeList
    }

    @Throws (Exception::class)
    /** Uploads meme to firebase */
    suspend fun uploadMemeToFirebase(meme:FavouriteMeme){
        try{
            val dataCollectionRef=Firebase.firestore.collection("Memes")
            val memeQuery=dataCollectionRef
                .whereEqualTo("meme",meme.meme)
                .whereEqualTo("uid",meme.uid)
                .get()
                .await()
            if(memeQuery.isEmpty){
                dataCollectionRef.add(meme).await()
            }
        }
        catch (e:Exception){
            throw e
        }
    }

    @Throws(Exception::class)
    /** Deletes Memes from firebase */
    suspend fun deleteMemeFromFirebase(meme: FavouriteMeme){
        try{
            val dataCollectionRef=Firebase.firestore.collection("Memes")
            val memeQuery=dataCollectionRef
                .whereEqualTo("meme",meme.meme)
                .whereEqualTo("uid",meme.uid)
                .get()
                .await()
            if(!memeQuery.isEmpty){
                for(document in memeQuery.documents){
                    dataCollectionRef.document(document.id).delete().await()
                }
            }
        }
        catch (e:Exception){
            throw e
        }
    }
}