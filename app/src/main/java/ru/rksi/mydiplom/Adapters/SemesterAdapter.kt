package ru.rksi.mydiplom.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.rksi.mydiplom.APIClasses.FormOfControl
import ru.rksi.mydiplom.APIClasses.Mark
import ru.rksi.mydiplom.APIClasses.Semester
import ru.rksi.mydiplom.R

class SemesterAdapter(private val context: Context) :
    RecyclerView.Adapter<SemesterAdapter.ViewHolder>() {
    private var semester: ArrayList<FormOfControl> = ArrayList()

    class ViewHolder(itemView: View, private val context: Context) :
        RecyclerView.ViewHolder(itemView) {

        private val marksRecycle: RecyclerView = itemView.findViewById(R.id.marksRecycle)
        private val title: TextView = itemView.findViewById(R.id.typeOfPerformance)

        fun bind(semester: FormOfControl) {

            title.text = semester.name

            val marksAdapter = MarksAdapter()
            marksRecycle.layoutManager = LinearLayoutManager(context)
            this.marksRecycle.adapter = marksAdapter
            var isCredit = false
            if (semester.name == "Зачёт")
                isCredit = true
            marksAdapter.setItems(semester.marks, isCredit)
        }
    }

    fun setItems(performance: ArrayList<FormOfControl>) {
        clearItems()
        this.semester.addAll(performance)
        notifyDataSetChanged()
    }

    fun clearItems() {
        semester.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.semester_layout, parent, false)
        return ViewHolder(view, this.context)
    }

    override fun getItemCount(): Int {
        return semester.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(semester[position])
    }
}