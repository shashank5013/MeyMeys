package com.example.android.meymeys.ui

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
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
        binding.notifyChange()

        return binding.root
    }

}