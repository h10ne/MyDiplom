package ru.rksi.mydiplom.Adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.rksi.mydiplom.APIClasses.Day
import ru.rksi.mydiplom.APIClasses.Schedule
import ru.rksi.mydiplom.R
import java.util.*


class TimetableForViewPagerAdapter(
    private val day: Day,
    private val isTeacher: Boolean,
    private val date: String
) : Fragment() {

    private val adapter = TimetableAdapter(isTeacher)
    private lateinit var currentDay: TextView
    private lateinit var currentDate: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_one_timetable, container, false)
        root.findViewById<RecyclerView>(R.id.recTimetable).adapter = adapter
        root.findViewById<RecyclerView>(R.id.recTimetable).layoutManager =
            LinearLayoutManager(activity)

        adapter.setItems(day.subjects)

        currentDay = root.findViewById(R.id.dayName)
        currentDate = root.findViewById(R.id.dateText)
        val dateParts = date.split(',')
        currentDay.text = dateParts[0].capitalize()
        currentDate.text = dateParts[1]

        return root
    }

}
