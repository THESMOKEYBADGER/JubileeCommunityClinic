package com.example.jubileecommunityclinic

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class PreviousActivity : AppCompatActivity() {

    companion object {
        private val activityToMenuItemMap = mapOf(
            HomeActivity::class.java to R.id.menu_home,
            BookingActivity::class.java to R.id.menu_booking,
            UpcomingActivity::class.java to R.id.menu_upcoming,
            PreviousActivity::class.java to R.id.menu_previous
        )
    }

    private lateinit var menu: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previous)

        menu = findViewById(R.id.bottomNavigation)

        // Set the selected menu item based on the current activity
        activityToMenuItemMap[this::class.java]?.let { menu.selectedItemId = it }

        // Handling the menu
        menu.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    startActivity(Intent(this@PreviousActivity, HomeActivity::class.java))
                    true
                }
                R.id.menu_booking -> {
                    startActivity(Intent(this@PreviousActivity, BookingActivity::class.java))
                    true
                }
                R.id.menu_upcoming -> {
                    startActivity(Intent(this@PreviousActivity, UpcomingActivity::class.java))
                    true
                }
                R.id.menu_previous -> {
                    startActivity(Intent(this@PreviousActivity, PreviousActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }


}
