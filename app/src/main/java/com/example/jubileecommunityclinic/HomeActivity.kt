package com.example.jubileecommunityclinic

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var textViewUID: TextView
    private lateinit var textViewIDNumber: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var menu: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize Firebase
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        // Views
        textViewUID = findViewById(R.id.textViewUID)
        textViewIDNumber = findViewById(R.id.textViewIDNumber)
        menu = findViewById(R.id.bottomNavigation)

        //Handling the menu
        menu.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    startActivity(Intent(this@HomeActivity, HomeActivity::class.java))
                    true
                }
                R.id.menu_booking -> {
                    startActivity(Intent(this@HomeActivity, BookingActivity::class.java))
                    true
                }
                R.id.menu_upcoming -> {
                    startActivity(Intent(this@HomeActivity, UpcomingActivity::class.java))
                    true
                }
                R.id.menu_previous -> {
                    startActivity(Intent(this@HomeActivity, PreviousActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Display user information
        displayUserInfo()
    }

    private fun displayUserInfo() {
        val user = auth.currentUser
        if (user != null) {
            // Display UID
            val uid = user.uid
            textViewUID.text = "User UID: $uid"

            // Retrieve ID number from Firestore
            retrieveIDNumber(uid)
        }
    }

    private fun retrieveIDNumber(uid: String) {
        val userInfoRef = db.collection("userInfo").document(uid)

        userInfoRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Display ID number
                    val idNumber = document.getString("idNumber")
                    textViewIDNumber.text = "ID Number: $idNumber"
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors
                println("Error getting documents: $exception")
            }
    }

}
