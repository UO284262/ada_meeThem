package com.ada.ada_meethem

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog.Builder
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var navView: BottomNavigationView
    private lateinit var navController: NavController

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

        // AlertDialog si no hay red
        if (!isNetworkAvailable()) {
            val builder = Builder(this)
            builder.setTitle(R.string.no_network_available_alert_title)
            builder.setMessage(R.string.no_network_available)
            builder.setPositiveButton("OK", null)
            builder.create()
            builder.show()
        }
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

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager: ConnectivityManager? =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (connectivityManager != null) {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
        return false
    }

}