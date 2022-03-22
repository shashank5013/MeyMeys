package com.example.android.meymeys.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
import androidx.fragment.app.commit
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.android.meymeys.R
import com.example.android.meymeys.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    //Binding Variable
    private lateinit var binding: ActivityMainBinding

    //Contains set of top level destinations
    private lateinit var appBarConfiguration: AppBarConfiguration

    //NavController
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Setting theme back to normal
        setTheme(R.style.Theme_MeyMeys)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Setting up NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.findNavController()

        //Setting up bottom navigation view and drawer navigation view with navController
        binding.apply {
            navView.setupWithNavController(navController)
        }

        //setting up Action Bar with app bar configuration
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment,R.id.loginFragment),
            binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        setupBottomNavView()
    }

    /** Handles back press when layout is open */
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    /** Sets up  bottom navigation view and drawer menu and hides it when unnecessary */
    private fun setupBottomNavView() {
        navController.addOnDestinationChangedListener { controller, destination, arguments ->

            //Sets color of action bar on different fragments
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            when (destination.id){
                 R.id.detailFragment -> {
                     val color=ColorDrawable(Color.BLACK)
                     supportActionBar?.setBackgroundDrawable(color)
                     window.statusBarColor=Color.BLACK
                 }
                else -> {
                    val color=ColorDrawable(resources.getColor(R.color.green))
                    supportActionBar?.setBackgroundDrawable(color)
                    window.statusBarColor=resources.getColor(R.color.green)

                    //Not displaying navigation drawer in login fragment
                    if(destination.id == R.id.loginFragment){
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    }
                }
            }


        }
    }


    /** Determines how to handle up button . Also sets up Navigation Drawer **/
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }
}