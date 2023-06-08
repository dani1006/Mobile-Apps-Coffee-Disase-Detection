package com.example.detektifkopi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_nav)

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)


        // Set default selected menu item
        bottomNavigationView.selectedItemId = R.id.navigation_home
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
//                val intent = Intent(this, HomeActivity::class.java)
                setCurrentActivity(HomeActivity::class.java)
//                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
//            R.id.navigation_artikel -> {
//                val intent = Intent(this, HomeActivity::class.java)
//                startActivity(intent)
//                return@OnNavigationItemSelectedListener true
//            }
//            R.id.navigation_simpan -> {
//                val intent = Intent(this, HomeActivity::class.java)
//                startActivity(intent)
//                return@OnNavigationItemSelectedListener true
//            }
//            R.id.navigation_akun -> {
//                val intent = Intent(this, HomeActivity::class.java)
//                startActivity(intent)
//                return@OnNavigationItemSelectedListener true
//            }
        }
        false
    }

    private fun setCurrentActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
        startActivity(intent, options)
    }
}