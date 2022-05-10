package com.example.android.meymeys.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.android.meymeys.R
import com.example.android.meymeys.databinding.FragmentUserBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


/**
 * A simple [Fragment] subclass.
 * Use the [UserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserFragment : Fragment() {

    //Auth Variable
    private lateinit var auth:FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding=FragmentUserBinding.inflate(layoutInflater,container,false)

        //Initialising auth variable
        auth=Firebase.auth

        //Data binding
        binding.user=auth.currentUser
        binding.executePendingBindings()

        val url=auth.currentUser?.photoUrl
        url?.let {
            Glide.with(binding.profileImage.context)
                .load(url)
                .placeholder(
                    AppCompatResources.getDrawable(
                        binding.profileImage.context,
                        R.drawable.ic_meme_placeholder
                    )
                )
                .into(binding.profileImage)
        }


        //Sign out onclick listener
        binding.signoutBtn.setOnClickListener {
            auth.signOut()
            GoogleSignIn.getClient(requireContext(),
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                .signOut().addOnCompleteListener{task->
                    if(task.isSuccessful){
                        findNavController().navigate(UserFragmentDirections.actionUserFragmentToLoginFragment())
                    }
                    else{
                        Snackbar.make(requireView(),getString(R.string.error_text), Snackbar.LENGTH_SHORT).show()
                    }
                }
        }

        return binding.root
    }

}