package ru.rksi.mydiplom.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.rksi.mydiplom.APIClasses.*
import ru.rksi.mydiplom.APIClasses.Week
import ru.rksi.mydiplom.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TimetableWeekAdapter(private val context: Context, val isTeacher: Boolean) :
    RecyclerView.Adapter<TimetableWeekAdapter.ViewHolder>() {
    private var DayList = ArrayList<Day>()

    class ViewHolder(itemView: View, private val context: Context, isTeacher: Boolean) :
        RecyclerView.ViewHolder(itemView) {
        private var dayName: TextView = itemView.findViewById(R.id.day)
        private var daySubjects: RecyclerView = itemView.findViewById(R.id.lessonInDay)
        private val adapter = TimetableWeekOneDayAdapter(isTeacher)

        fun bind(day: Day) {
            dayName.text = day.title
            var cal = Calendar.getInstance()
            cal.timeInMillis = day.date.toLong() * 1000

            var sdf = SimpleDateFormat("EEEE, dd MMMM ", Locale("ru")).format(cal.timeInMillis)
                .capitalize()
            dayName.text = sdf
            daySubjects.adapter = adapter
            daySubjects.layoutManager = LinearLayoutManager(context)
            adapter.setItems(day.subjects)
        }
    }

    fun setItems(weeks: ArrayList<Week>) {
        clearItems()
        weeks.forEach {
            it.days.forEach { day ->
                if (day.subjects.size != 0)
                    DayList.add(day)
            }
        }
        //DayList.addAll(days)
        notifyDataSetChanged()
    }

    fun clearItems() {
        DayList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.full_timetable, parent, false)
        return ViewHolder(view, this.context, isTeacher)
    }

    override fun getItemCount(): Int {
        return DayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(DayList[position])
    }
}
