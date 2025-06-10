package com.example.nutrition_app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.nutrition_app.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        val bottomNavView = findViewById<BottomNavigationView>(R.id.nav_nav)
        bottomNavView.setupWithNavController(navController)

        // Hide BottomNavigationView on welcome, register, and login screens
        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNavView.visibility = when (destination.id) {
                R.id.navigation_welcome, R.id.navigation_register, R.id.navigation_login -> View.GONE
                else -> View.VISIBLE
            }
        }
    }
}