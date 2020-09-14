package ru.rksi.mydiplom.Adapters

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.rksi.mydiplom.APIClasses.*
import ru.rksi.mydiplom.R
import ru.rksi.mydiplom.TokenManager
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TaskAdapter(val isTeacher: Boolean) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    private var taskList = ArrayList<TaskResponse>()

    inner class ViewHolder(itemView: View, val isTeacher: Boolean) :
        RecyclerView.ViewHolder(itemView), PopupMenu.OnMenuItemClickListener {
        private val context = itemView.context
        private var discipline: TextView = itemView.findViewById(R.id.discipline)
        private var taskText: TextView = itemView.findViewById(R.id.task_text)
        private var taskUntil: TextView = itemView.findViewById(R.id.task_until)
        private var groupRTeacher:TextView = itemView.findViewById(R.id.groupOrTeacher)
        private lateinit var task: TaskResponse
        private var user:UserInfo
        fun bind(task: TaskResponse) {
            this.task = task
            discipline.text = task.lesson.title
            taskText.text = task.text
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = task.deadline * 1000
            taskUntil.text = SimpleDateFormat("dd MMMM yyyy").format(calendar.timeInMillis)
            if(user.isTeacher)
                groupRTeacher.text = task.group.name
            else
                groupRTeacher.text = task.teacher.name
        }

        init {
            itemView.setOnClickListener()
            { view ->
                if (isTeacher) {
                    val popupMenu = PopupMenu(view.context, view)
                    popupMenu.inflate(R.menu.tasks_menu)
                    popupMenu.setOnMenuItemClickListener {
                        onMenuItemClick(it)
                    }
                    popupMenu.show()
                }
            }
            val userJson = String(
                FileInputStream(
                    File(
                        itemView.context!!.filesDir,
                        "User.dat"
                    )
                ).readBytes()
            )
            user = Json.parse(UserInfo.serializer(), userJson)

        }

        override fun onMenuItemClick(item: MenuItem?): Boolean {
            when (item.toString()) {
                "Изменить" -> {
                    val bundle = Bundle()
                    val taskString = task.toJson()
                    bundle.putString("response", taskString)
                    itemView.findNavController().navigate(R.id.createTask, bundle)
                }
                "Удалить" -> {
                    val dialog = AlertDialog.Builder(context)
                        .setTitle("Вы уверены, что хотите удалить задание?")
                        .setPositiveButton(
                            android.R.string.yes
                        ) { _, _ ->
                            removeTask()
                        }
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .create()
                    dialog.setOnShowListener {
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                            .setTextColor(context.resources.getColor(R.color.colorPrimary))
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(context.resources.getColor(R.color.colorPrimary))
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).text = "Удалить"
                    }
                    dialog.show()


                }
            }
            return true
        }

        private fun removeTask()
        {
            val retrofit = ApiClient.Instance
            val api = retrofit.create(RsueApi::class.java)
            val call = api.deleteTasks("Bearer " + TokenManager.AccessToken, task.taskId)

            call.enqueue(
                object : Callback<Void> {
                    override fun onResponse(
                        call: Call<Void>,
                        response: Response<Void>
                    ) {
                        if (response.code() == 204) {
                            removeAt(position)
                            Toast.makeText(
                                itemView.context,
                                "Задание успешно Удалено!",
                                Toast.LENGTH_SHORT
                            )
                                .show()

                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(context, "Что-то пошло не так! Проверье ваше подключение к сети.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            )
        }


    }


    fun setItems(tasks: ArrayList<TaskResponse>) {
        clearItems()
        taskList.addAll(tasks)
        notifyDataSetChanged()
    }

    public fun removeAt(position: Int) {
        taskList.removeAt(position)
        notifyDataSetChanged()
    }

    private fun clearItems() {
        taskList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.tasks_items_view, parent, false)
        return ViewHolder(view, isTeacher)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(taskList[position])
        holder.itemView.setOnLongClickListener {

            true
        }
    }

}