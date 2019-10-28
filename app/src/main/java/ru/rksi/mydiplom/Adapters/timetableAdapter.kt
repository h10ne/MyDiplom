package ru.rksi.mydiplom.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.rksi.mydiplom.APIClasses.Lesson
import ru.rksi.mydiplom.R
import kotlin.collections.ArrayList

class TimetableAdapter : RecyclerView.Adapter<TimetableAdapter.ViewHolder>() {

    private var LessonsList = ArrayList<Lesson>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var time: TextView = itemView.findViewById(R.id.time)
        private var name: TextView = itemView.findViewById(R.id.nameLesson)
        private var teacher: TextView = itemView.findViewById(R.id.teacher)
        private var room: TextView = itemView.findViewById(R.id.room)
        private var type: TextView = itemView.findViewById(R.id.type)

        fun bind(lesson: Lesson) {
            time.text = lesson.time
            name.text = lesson.name
            teacher.text = lesson.teacher
            room.text = lesson.room
            type.text = lesson.type
        }

    }


    fun setItems(lessons: ArrayList<Lesson>) {
        clearItems()
        var count = 0
        lessons.forEach {
            LessonsList.add(it)
            notifyItemChanged(count)
            count++
        }
        //journalList.addAll(journalItems)
        //notifyDataSetChanged()
    }

    fun clearItems() {
        LessonsList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.timetable, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return LessonsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(LessonsList[position])
    }

}