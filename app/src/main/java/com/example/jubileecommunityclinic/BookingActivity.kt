package com.example.jubileecommunityclinic

import DateFragment
import TimeFragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.jubileecommunityclinic.models.AppointmentRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class BookingActivity : AppCompatActivity(), DateFragment.OnDateConfirmedListener,
    TimeFragment.OnTimeConfirmedListener {

    companion object {
        private val activityToMenuItemMap = mapOf(
            HomeActivity::class.java to R.id.menu_home,
            BookingActivity::class.java to R.id.menu_booking,
            UpcomingActivity::class.java to R.id.menu_upcoming,
            PreviousActivity::class.java to R.id.menu_previous
        )
        private const val TAG = "com.example.jubileecommunityclinic.BookingActivity"
    }

    private lateinit var menu: BottomNavigationView
    private val db = FirebaseFirestore.getInstance()
    private var selectedDate: String? = null
    private var selectedTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        menu = findViewById(R.id.bottomNavigation)

        // Set the selected menu item based on the current activity
        activityToMenuItemMap[this::class.java]?.let { menu.selectedItemId = it }

        // Handling the menu
        menu.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    navigateTo(HomeActivity::class.java)
                    true
                }
                R.id.menu_booking -> {
                    navigateTo(BookingActivity::class.java)
                    true
                }
                R.id.menu_upcoming -> {
                    navigateTo(UpcomingActivity::class.java)
                    true
                }
                R.id.menu_previous -> {
                    navigateTo(PreviousActivity::class.java)
                    true
                }
                else -> false
            }
        }

        // Load the initial fragment only if it hasn't been added before
        if (savedInstanceState == null && supportFragmentManager.findFragmentById(R.id.fragmentContainer) == null) {
            loadDateFragment()
        }
    }


    private fun navigateTo(destination: Class<out AppCompatActivity>) {
        startActivity(Intent(this@BookingActivity, destination))
    }

    private fun loadDateFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, DateFragment())
            .commit()
    }

    override fun onDateConfirmed(selectedDate: String) {
        this.selectedDate = selectedDate
        // Date confirmed, load the TimeFragment
        loadTimeFragment()
    }

    private fun loadTimeFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, TimeFragment())
            .commit()
    }

    override fun onTimeConfirmed(selectedTime: String) {
        this.selectedTime = selectedTime
        // Time confirmed, handle the complete booking process
        saveAppointmentToFirestore()
    }

    private fun saveAppointmentToFirestore() {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null && selectedDate != null && selectedTime != null) {
            val uid = user.uid

            val db = FirebaseFirestore.getInstance()

            // Fetch additional user information from Firestore
            db.collection("userInfo")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // User information found, extract name and surname
                        val firstName = document.getString("firstName") ?: "default_first_name"
                        val lastName = document.getString("lastName") ?: "default_last_name"

                        val dateOfRequest = getCurrentDate()

                        val appointmentRequest = AppointmentRequest(
                            uid = uid,
                            firstName = firstName,
                            lastName = lastName,

                            requestedDate = selectedDate!!,
                            requestedTime = selectedTime!!,
                            dateOfRequest = dateOfRequest
                        )

                        db.collection("requestedAppointments")
                            .add(appointmentRequest)
                            .addOnSuccessListener { documentReference ->
                                // Appointment data saved successfully
                                Log.d(TAG, "Appointment document added with ID: ${documentReference.id}")
                                // You can perform any additional actions here, like navigating to the next screen
                            }
                            .addOnFailureListener { e ->
                                // Handle errors here
                                Log.w(TAG, "Error adding document", e)
                            }
                    } else {
                        Log.w(TAG, "No user information found in Firestore.")
                        // Handle the case where user information is not available
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error fetching user information from Firestore", e)
                    // Handle the error
                }
        } else {
            Log.w(TAG, "User is not authenticated or date/time is not selected.")
            // Handle the case where the user is not authenticated or date/time is not selected
        }
    }



    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
