package ru.rksi.mydiplom.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.rksi.mydiplom.APIClasses.Mark
import ru.rksi.mydiplom.R

class MarksAdapter() :
    RecyclerView.Adapter<MarksAdapter.ViewHolder>() {
    private var marks: ArrayList<Mark> = ArrayList()
    private var isCredit = false

    class ViewHolder(itemView: View, val isCredit: Boolean) :
        RecyclerView.ViewHolder(itemView) {

        private val lesson: TextView = itemView.findViewById(R.id.lesson)
        private val teacher: TextView = itemView.findViewById(R.id.teacher)
        private val kt1: TextView = itemView.findViewById(R.id.kt1)
        private val kt2: TextView = itemView.findViewById(R.id.kt2)
        private val sum: TextView = itemView.findViewById(R.id.sum)
        private val mark: TextView = itemView.findViewById(R.id.mark)

        fun bind(mark: Mark) {
            lesson.text = mark.lesson.title
            teacher.text = mark.teacher.name
            kt1.text = "КТ-1: ${mark.checkpoint1}"
            kt2.text = "КТ-2: ${mark.checkpoint2}"
            sum.text = "Итог: ${mark.finalPercent}"
            if (isCredit) {
                when (mark.value) {
                    0.toShort(), 1.toShort(), 2.toShort() -> this.mark.text = "Незач."
                    3.toShort(), 4.toShort(), 5.toShort() -> this.mark.text = "Зач"
                }
            }
            else
            {
                when (mark.value) {
                    0.toShort(), 1.toShort(), 2.toShort() -> this.mark.text = "Неуд."
                    3.toShort() -> this.mark.text = "Удовл."
                    4.toShort() -> this.mark.text = "Хор."
                    5.toShort() -> this.mark.text = "Отл."
                }
            }
        }
    }

    fun setItems(marks: ArrayList<Mark>, isCredit: Boolean) {
        clearItems()
        this.isCredit = isCredit
        this.marks.addAll(marks)
        notifyDataSetChanged()
    }

    fun clearItems() {
        marks.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.marks_item, parent, false)
        return ViewHolder(view, isCredit)
    }

    override fun getItemCount(): Int {
        return marks.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(marks[position])
    }
}