// DateFragment.kt
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import com.example.jubileecommunityclinic.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateFragment : Fragment() {

    private var callback: OnDateConfirmedListener? = null
    private var selectedDate: String? = null // Variable to store the selected date

    interface OnDateConfirmedListener {
        fun onDateConfirmed(selectedDate: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_date, container, false)

        val calendarView: CalendarView = view.findViewById(R.id.calendarView)
        val confirmButton: Button = view.findViewById(R.id.confirmDateButton)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Convert the selected date to the desired format
            selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth)
        }

        confirmButton.setOnClickListener {
            // Check if the selected date is not in the past
            if (!isDateInPast(selectedDate)) {
                // Notify the activity that the date is confirmed
                selectedDate?.let { date ->
                    callback?.onDateConfirmed(date)
                }
            } else {
                // Show a message or handle the case where the date is in the past
                // For example, you can display a Toast message
                showToast("Cannot select a date in the past.")
            }
        }

        return view
    }

    private fun isDateInPast(selectedDate: String?): Boolean {
        if (selectedDate.isNullOrEmpty()) {
            // If the selectedDate is null or empty, consider it as in the past
            return true
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        return selectedDate < currentDate
    }

    private fun showToast(message: String) {
        // Customize this method to display a Toast message
        // You might need to access the context for this, depending on your implementation
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Ensure the hosting activity implements the callback interface
        try {
            callback = context as OnDateConfirmedListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnDateConfirmedListener")
        }
    }
}
