package com.vitaltracker.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.vitaltracker.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        val nav = (supportFragmentManager.findFragmentById(b.navHost.id) as NavHostFragment).navController
        b.bottomNav.setupWithNavController(nav)
    }
}
