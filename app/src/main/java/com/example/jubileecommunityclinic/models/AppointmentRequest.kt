// models/AppointmentRequest.kt
package com.example.jubileecommunityclinic.models

data class AppointmentRequest(
    val uid: String,           // User's UID
    // User's ID number
    val firstName: String,     // User's First Name
    val lastName: String,      // User's Last Name
    val requestedDate: String, // Requested Date
    val requestedTime: String, // Requested Time
    val dateOfRequest: String  // Date of Request
)
