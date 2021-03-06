package com.example.android.meymeys.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.android.meymeys.R
import com.example.android.meymeys.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception


/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    //request code for google sign intent
    private val RC_SIGN_IN=1

    //Firebase auth
    private lateinit var  auth:FirebaseAuth

    //Binding variable
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentLoginBinding.inflate(layoutInflater,container,false)

        //Initialising auth variable
        auth= Firebase.auth

        val gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient=GoogleSignIn.getClient(requireContext(),gso)

        binding.signInBtn.setOnClickListener {
            signIn(googleSignInClient)
        }
        return binding.root
    }

    /** Checks whether user is already logged in */
    override fun onStart() {
        super.onStart()
        val user=auth.currentUser
        updateUI(user)
    }

    /** launches google sign in intent */
    private fun signIn(googleSignInClient: GoogleSignInClient) {
        val intent=googleSignInClient.signInIntent
        startActivityForResult(intent,RC_SIGN_IN)
    }

    /** handles intent result*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==RC_SIGN_IN){
            val task=GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                showProgressBar()
                hideSignInBtn()
                val account=task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            }catch (e:Exception){
                showSignInBtn()
                hideProgressBar()
            }
        }
    }

    /** Authenticates google account with firebase */
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential=GoogleAuthProvider.getCredential(idToken,null)
        auth.signInWithCredential(credential).addOnCompleteListener{task->
            if(task.isSuccessful){
                updateUI(task.result.user)
            }
            else{
                showSignInBtn()
                hideProgressBar()
                Snackbar.make(requireView(),getString(R.string.error_text),Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    /** Updates UI */
    private fun updateUI(user: FirebaseUser?) {
        user?.let { findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment()) }
    }

    /** Shows the progress bar */
    private fun showProgressBar(){
        binding.progressBar.visibility=View.VISIBLE
    }

    /** Hides the progress bar */
    private fun hideProgressBar(){
        binding.progressBar.visibility=View.INVISIBLE
    }

    /** Shows the signIn button */
    private fun showSignInBtn(){
        binding.signInBtn.visibility=View.VISIBLE
    }

    /** Hides the signIn button */
    private fun hideSignInBtn(){
        binding.signInBtn.visibility=View.INVISIBLE
    }


}