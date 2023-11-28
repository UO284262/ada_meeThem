package com.ada.ada_meethem

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ada.ada_meethem.ui.CreatePlanFragment
import com.ada.ada_meethem.ui.HomeFragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    var toolBarLayout: CollapsingToolbarLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)

        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navView.setupWithNavController(navController)
        navView.selectedItemId = R.id.nav_home
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->

            /* Cuando se selecciona uno de los botones / ítems*/
            val itemId = item.itemId

            /* Según el caso, crearemos un Fragmento u otro */if (itemId == R.id.nav_home) {
            val homeFragment = HomeFragment()
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, homeFragment)
                .commit()
            return@OnNavigationItemSelectedListener true
        }
            if (itemId == R.id.nav_profile) {
                return@OnNavigationItemSelectedListener true
            }
            if (itemId == R.id.nav_plan_create) {
                val createPlanFragment = CreatePlanFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, createPlanFragment).commit()
                return@OnNavigationItemSelectedListener true
            }
            throw IllegalStateException("Unexpected value: " + item.itemId)
        }
}