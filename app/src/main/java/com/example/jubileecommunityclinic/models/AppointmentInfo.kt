package com.example.jubileecommunityclinic.models

import com.google.firebase.firestore.PropertyName

class AppointmentInfo(
    @get:PropertyName("requestedDate")
    @set:PropertyName("requestedDate")
    var requestedDate: String = "",

    @get:PropertyName("requestedTime")
    @set:PropertyName("requestedTime")
    var requestedTime: String = "",

    @get:PropertyName("dateOfRequest")
    @set:PropertyName("dateOfRequest")
    var dateOfRequest: String = ""
)
