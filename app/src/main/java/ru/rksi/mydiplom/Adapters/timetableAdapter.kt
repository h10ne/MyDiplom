package ru.rksi.mydiplom.Adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.rksi.mydiplom.APIClasses.Classroom
import ru.rksi.mydiplom.APIClasses.Group
import ru.rksi.mydiplom.APIClasses.Subject
import ru.rksi.mydiplom.APIClasses.Teacher
import ru.rksi.mydiplom.R
import java.util.*
import kotlin.collections.ArrayList


class TimetableAdapter(var isTeacher: Boolean) :
    RecyclerView.Adapter<TimetableAdapter.ViewHolder>() {

    private var SubjectList = ArrayList<Subject>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var time: TextView = itemView.findViewById(R.id.time)
        private var name: TextView = itemView.findViewById(R.id.nameLesson)
        private var teacher: TextView = itemView.findViewById(R.id.teacher)
        private var room: TextView = itemView.findViewById(R.id.room)
        private var type: TextView = itemView.findViewById(R.id.type)
        private lateinit var address:String
        init {
            itemView.isLongClickable = true
            itemView.setOnLongClickListener(View.OnLongClickListener ()
            {
                when {
                    address.contains("Тургеневская") -> {
                        val uri: String =
                            java.lang.String.format(Locale.ENGLISH, "geo:%f,%f", 47.217056, 39.7109583)
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                        itemView.context.startActivity(intent)
                    }
                    address.contains("Островского") -> {
                        val uri: String =
                            java.lang.String.format(Locale.ENGLISH, "geo:%f,%f", 47.2170738, 39.7043922)
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                        itemView.context.startActivity(intent)
                    }
                    address.contains("Горького") -> {
                        val uri: String =
                            java.lang.String.format(Locale.ENGLISH, "geo:%f,%f", 47.228671, 39.7222917)
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                        itemView.context.startActivity(intent)
                    }
                }
                true
            })
        }

        fun bind(subject: Subject, isTeacher: Boolean) {
            time.text = subject.startTime + " - " + subject.endTime
            name.text = subject.title
            if (isTeacher)
                teacher.text = subject.group.name
            else
                teacher.text = "${subject.teacher.position} ${subject.teacher.name}"
            room.text = "ауд. ${subject.classroom.number} ${subject.classroom.corps}"
            type.text = subject.typeOfWork
            this.address = subject.classroom.address
        }

    }


    fun setItems(subjects: ArrayList<Subject>) {
        clearItems()
        if (subjects.size != 0)
            SubjectList.addAll(subjects)
        else {
            val emptyTitle:String = if(isTeacher)
                "В этот день у преподавателя нет занятий"
            else
                "В этот день у группы нет занятий"

            val lesson = Subject(
                0,
                emptyTitle,
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
                .inflate(R.layout.timetable_items_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return SubjectList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(SubjectList[position], isTeacher)
    }

}