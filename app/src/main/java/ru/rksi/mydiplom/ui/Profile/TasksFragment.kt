package ru.rksi.mydiplom.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.clans.fab.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_tasks.*
import ru.rksi.mydiplom.APIClasses.Task
import ru.rksi.mydiplom.Adapters.TaskAdapter
import ru.rksi.mydiplom.R


class TasksFragment : Fragment() {
    var adapter = TaskAdapter()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_tasks, container, false)


        var tv: RecyclerView = root.findViewById(R.id.tasks_view)
        tv.layoutManager = LinearLayoutManager(activity)
        tv.adapter = adapter
        init()
        var fab = root.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener{createNewTask()}

        return root
    }

    private fun createNewTask()
    {
        view!!.findNavController().navigate(R.id.createTask)
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
