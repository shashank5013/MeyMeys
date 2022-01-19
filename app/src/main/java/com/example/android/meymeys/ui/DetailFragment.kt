package com.example.android.meymeys.ui

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.android.meymeys.R
import com.example.android.meymeys.databinding.FragmentDetailBinding


class DetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding=FragmentDetailBinding.inflate(layoutInflater,container,false)

        val args : DetailFragmentArgs by navArgs()
        binding.meme=args.meme
        binding.executePendingBindings()

        //setting image view using glide and altering transitions
        Glide.with(binding.detailImage.context)
            .load(args.meme.url)
            .placeholder(AppCompatResources.getDrawable(binding.detailImage.context,R.drawable.ic_meme_placeholder))
            .into(binding.detailImage)



        return binding.root
    }

}