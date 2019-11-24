package ru.rksi.mydiplom.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.rksi.mydiplom.APIClasses.Task
import ru.rksi.mydiplom.R
import kotlin.collections.ArrayList

class TaskAdapter : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    private var taskList = ArrayList<Task>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var discipline: TextView = itemView.findViewById(R.id.discipline)
        private var taskStatus: TextView = itemView.findViewById(R.id.task_status)
        private var taskText: TextView = itemView.findViewById(R.id.task_text)
        private var taskUntil: TextView = itemView.findViewById(R.id.task_until)

        fun bind(task: Task) {
            discipline.text = task.discipline
            taskStatus.text = task.taskStatus
            taskText.text = task.taskText
            taskUntil.text = task.taskUntil
        }

    }


    fun setItems(tasks: ArrayList<Task>) {
        clearItems()
        taskList.addAll(tasks)
        notifyDataSetChanged()
    }

    fun clearItems() {
        taskList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.tasks_items_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(taskList[position])
    }

}