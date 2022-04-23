package com.example.android.meymeys.utils

import android.content.Context
import android.content.Intent
import com.example.android.meymeys.model.ShareItem

const val BASE_URL="https://meme-api.herokuapp.com/gimme/"
const val COUNT=20
const val SUBREDDIT_HOME="ProgrammerHumor"
const val SUBREDDIT_TRENDING="meme"
val Subreddits= mapOf<String,String>(
    "Trending" to "memes",
    "Cricket" to "CricketShitpost",
    "Programming" to "ProgrammerHumor",
    "India" to "IndianDankMemes",
    "Anime" to "Animemes",
    "History" to "HistoryMemes",
    "Hindi" to "HindiMemes"
)
const val QUERY_PAGE_SIZE=20

/** List of activities to which image can be shared */
val shareItemList= mutableListOf<ShareItem>()
fun initShareItemList(context: Context) {
    shareItemList.clear()
    val intent= Intent(Intent.ACTION_SEND).apply {
        type="image/*"
    }

    // Creating a list of shareable activities
    val activities=context.packageManager.queryIntentActivities(intent,0)

    for(data in activities){
        shareItemList.add(
            ShareItem(
                data.loadLabel(context.packageManager).toString(),
                data.loadIcon(context.packageManager),
                data.activityInfo.packageName
            )
        )
    }
}