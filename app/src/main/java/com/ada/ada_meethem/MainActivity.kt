package com.ada.ada_meethem

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ada.ada_meethem.database.ContactDatabase
import com.ada.ada_meethem.modelo.pinnable.ChatMessage
import com.ada.ada_meethem.modelo.pinnable.DateSurvey
import com.ada.ada_meethem.modelo.pinnable.Pinnable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var navView : BottomNavigationView
    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)

        // Aquí se indican las id de los elementos del nav
        //val appBarConfiguration = AppBarConfiguration(setOf(
        //    R.id.nav_home, R.id.nav_plan_create, R.id.nav_profile
        //))
        //setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setupWithNavController(navController)
        configureNavViewButtons()
    }

    private fun configureNavViewButtons() {
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    navController.navigate(R.id.action_global_nav_home)
                }
                R.id.nav_plan_create -> {
                    navController.navigate(R.id.action_global_nav_plan_create)
                }
                R.id.nav_profile -> {
                    navController.navigate(R.id.action_global_nav_profile)
                }
            }
            true
        }
    }

}