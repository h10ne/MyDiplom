package ru.rksi.mydiplom.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.rksi.mydiplom.APIClasses.FormOfControl
import ru.rksi.mydiplom.APIClasses.Performance
import ru.rksi.mydiplom.R

class PerformanceAdapter(private val context: Context) :
    RecyclerView.Adapter<PerformanceAdapter.ViewHolder>() {
    private var performances: ArrayList<Performance> = ArrayList()

    class ViewHolder(itemView: View, private val context: Context) :
        RecyclerView.ViewHolder(itemView) {

        private val semester: RecyclerView = itemView.findViewById(R.id.performanceRecycle)
        private val title: TextView = itemView.findViewById(R.id.title)

        fun bind(performance: Performance) {
            title.text =
                "Курс: ${performance.course}, семестр: ${performance.semester.number}, учебный год: ${performance.academicYear}"

            val semesterAdapter = SemesterAdapter(context)
            semester.layoutManager = LinearLayoutManager(context)
            semester.adapter = semesterAdapter

            val exam = FormOfControl("Экзамен", performance.semester.exams)
            val credit = FormOfControl("Зачёт", performance.semester.credits)
            val practice = FormOfControl("Практика", performance.semester.practices)

            val typesOfControl: ArrayList<FormOfControl> = ArrayList()
            if (exam.marks.isNotEmpty())
                typesOfControl.add(exam)
            if (credit.marks.isNotEmpty())
                typesOfControl.add(credit)
            if (practice.marks.isNotEmpty())
                typesOfControl.add(practice)

            semesterAdapter.setItems(typesOfControl)
        }
    }

    fun setItems(performances: ArrayList<Performance>) {
        clearItems()
        this.performances.addAll(performances)
        notifyDataSetChanged()
    }

    fun clearItems() {
        performances.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.performance_layout, parent, false)
        return ViewHolder(view, this.context)
    }

    override fun getItemCount(): Int {
        return performances.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(performances[position])
    }
}