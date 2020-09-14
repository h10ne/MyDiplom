package ru.rksi.mydiplom.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.rksi.mydiplom.APIClasses.*
import ru.rksi.mydiplom.R

class TimetableWeekOneDayAdapter(val isTeacher: Boolean) :
    RecyclerView.Adapter<TimetableWeekOneDayAdapter.ViewHolder>() {
    private var SubjectList = ArrayList<Subject>()

    class ViewHolder(itemView: View, val isTeacher: Boolean) : RecyclerView.ViewHolder(itemView) {
        private var timeStart: TextView = itemView.findViewById(R.id.startTime)
        private var timeEnd: TextView = itemView.findViewById(R.id.endTime)
        private var name: TextView = itemView.findViewById(R.id.subject)
        private var teacher: TextView = itemView.findViewById(R.id.teacher)
        private var room: TextView = itemView.findViewById(R.id.room)
        private var type: TextView = itemView.findViewById(R.id.type)

        fun bind(subject: Subject) {
            timeStart.text = subject.startTime
            timeEnd.text = subject.endTime
            name.text = subject.title
            if (isTeacher)
                teacher.text = subject.group.name
            else
                teacher.text = subject.teacher.position + " " + subject.teacher.name
            room.text = subject.classroom.number + " " + subject.classroom.corps
            type.text = subject.typeOfWork
        }

    }


    fun setItems(subjects: ArrayList<Subject>) {
        clearItems()
        if (subjects.size != 0)
            SubjectList.addAll(subjects)
        else {
            val lesson = Subject(
                0,
                "В этот день у группы нет занятий",
                "",
                "",
                Classroom("", "", ""),
                Teacher(-1, "", ""),
                Group(-1, -1, ""),
                0,
                ""
            )
            val subj = ArrayList<Subject>()
            subj.add(lesson)
            SubjectList.addAll(subj)
        }
        notifyDataSetChanged()
    }

    fun clearItems() {
        SubjectList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_full_timetable, parent, false)
        return ViewHolder(view, isTeacher)
    }

    override fun getItemCount(): Int {
        return SubjectList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(SubjectList[position])
    }
}