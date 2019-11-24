package ru.rksi.mydiplom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_tasks.*
import ru.rksi.mydiplom.APIClasses.Task
import ru.rksi.mydiplom.Adapters.TaskAdapter
import ru.rksi.mydiplom.Adapters.TimetableAdapter

class Tasks : AppCompatActivity() {
    var adapter = TaskAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)
        title="Задания"
        var tv:RecyclerView = this.findViewById(R.id.tasks_view)
        tv.layoutManager = LinearLayoutManager(this)
        tv.adapter = adapter
        init()

    }

    private fun init()
    {
        var data = mocTasks()
        adapter.setItems(data)
    }
    private fun mocTasks():ArrayList<Task>
    {
        var list = ArrayList<Task>()
        var task1 = Task("Математика","Не выполнено","уч стр 40, задание 1,2,3","24.11.2019")
        var task2 = Task("ОП","Выполнено","прочитать про сереализацию","30.11.2019")
        list.add(task1)
        list.add(task2)
        return list
    }
}
