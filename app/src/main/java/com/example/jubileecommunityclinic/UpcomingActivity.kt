package com.example.jubileecommunityclinic

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jubileecommunityclinic.models.AppointmentInfo
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UpcomingActivity : AppCompatActivity() {
    companion object {
        private val activityToMenuItemMap = mapOf(
            HomeActivity::class.java to R.id.menu_home,
            BookingActivity::class.java to R.id.menu_booking,
            UpcomingActivity::class.java to R.id.menu_upcoming,
            PreviousActivity::class.java to R.id.menu_previous
        )
    }

    private lateinit var menu: BottomNavigationView
    private lateinit var loadingIndicator: View // Reference to your loading indicator view
    private lateinit var textViewAppointments: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upcoming)

        menu = findViewById(R.id.bottomNavigation)
        textViewAppointments = findViewById(R.id.textViewAppointments)
        loadingIndicator = findViewById(R.id.loadingIndicator)

        // Set the selected menu item based on the current activity
        activityToMenuItemMap[this::class.java]?.let { menu.selectedItemId = it }

        // Handling the menu
        menu.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    startActivity(Intent(this@UpcomingActivity, HomeActivity::class.java))
                    true
                }
                R.id.menu_booking -> {
                    startActivity(Intent(this@UpcomingActivity, BookingActivity::class.java))
                    true
                }
                R.id.menu_upcoming -> {
                    fetchAndDisplayAppointments()
                    true
                }
                R.id.menu_previous -> {
                    startActivity(Intent(this@UpcomingActivity, PreviousActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Fetch and display appointments when the activity is created
        fetchAndDisplayAppointments()

    }

    private fun fetchAndDisplayAppointments() {
        Log.d("UpcomingActivity", "fetchAndDisplayAppointments: Fetching appointments")

        // Show loading indicator
        loadingIndicator.visibility = View.VISIBLE

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val uid = currentUser.uid

            FirebaseFirestore.getInstance().collection("requestedAppointments")
                .whereEqualTo("uid", uid) // Assuming your document has a field "uid"
                .get()
                .addOnSuccessListener { documents ->
                    Log.d("UpcomingActivity", "fetchAndDisplayAppointments: Success")

                    val appointments = documents.toObjects(AppointmentInfo::class.java)
                    Log.d("UpcomingActivity", "Appointments: $appointments")

                    // Filter out appointments that are in the past
                    val upcomingAppointments = appointments.filter { !isDateInPast(it.requestedDate) }

                    if (upcomingAppointments.isEmpty()) {
                        // Show toast if no upcoming appointments
                        showToast("No upcoming appointments.")
                    }

                    // Update UI with fetched appointments
                    updateUIWithAppointments(upcomingAppointments)

                    // Hide loading indicator
                    loadingIndicator.visibility = View.GONE
                }
                .addOnFailureListener { exception ->
                    Log.e("UpcomingActivity", "fetchAndDisplayAppointments: Failure", exception)

                    // Handle failures

                    // Hide loading indicator
                    loadingIndicator.visibility = View.GONE
                }
        }
    }


    private fun updateUIWithAppointments(appointments: List<AppointmentInfo>) {
        // Assuming you have a TextView with the id textViewAppointments in your layout
        val appointmentsText = buildAppointmentsText(appointments)
        textViewAppointments.text = appointmentsText
    }

    private fun buildAppointmentsText(appointments: List<AppointmentInfo>): String {
        // Customize this method to format the appointment information as needed
        val builder = StringBuilder()
        for (appointment in appointments) {
            builder.append("Requested Date: ${appointment.requestedDate}\n")
            builder.append("Requested Time: ${appointment.requestedTime}\n")
            builder.append("Date of Request: ${appointment.dateOfRequest}\n")
            builder.append("\n") // Add a separator between appointments
        }
        return builder.toString()
    }

    private fun isDateInPast(requestedDate: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = getCurrentDate()
        return dateFormat.parse(requestedDate) < dateFormat.parse(currentDate)
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
