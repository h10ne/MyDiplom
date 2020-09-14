package ru.rksi.mydiplom.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.clans.fab.FloatingActionButton
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.rksi.mydiplom.APIClasses.*
import ru.rksi.mydiplom.Adapters.TaskAdapter
import ru.rksi.mydiplom.R
import ru.rksi.mydiplom.TokenManager
import java.io.File
import java.io.FileInputStream


class TasksFragment : Fragment() {
    private lateinit var adapter: TaskAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var tv:RecyclerView
    val retrofit = ApiClient.Instance
    val api: RsueApi = retrofit.create(RsueApi::class.java)
    private var user: UserInfo? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_tasks, container, false)
        progressBar = root.findViewById(R.id.loadCircle)
        val userJson = String(FileInputStream(File(requireContext().filesDir, "User.dat")).readBytes())
        user = Json.parse(UserInfo.serializer(), userJson)
        tv = root.findViewById(R.id.tasks_view)
        tv.layoutManager = LinearLayoutManager(activity)
        adapter = TaskAdapter(user!!.isTeacher)
        tv.adapter = adapter
        enableProgressBar()

        init()

        val fab = root.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { createNewTask() }

        if (!user!!.isTeacher)
            fab.visibility = View.GONE
        return root
    }

    private fun disableProgressBar()
    {
        progressBar.visibility = View.GONE
        tv.isEnabled = true
    }

    private fun enableProgressBar()
    {
        progressBar.visibility = View.VISIBLE
        tv.isEnabled = false
    }

    private fun createNewTask() {
        requireView().findNavController().navigate(R.id.createTask, null)
    }

    private fun init() {
        if(TokenManager.IsTokenValid())
            getTasks()
        else
        {
            val call = api.updateToken(RefreshTokenRequest(TokenManager.RefreshToken))
            call.enqueue(
                object : Callback<TokenResponse> {
                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        Toast.makeText(
                            context,
                            "Что-то пошло не так! Проверье ваше подключение к сети.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    override fun onResponse(
                        call: Call<TokenResponse>,
                        response: Response<TokenResponse>
                    ) {
                        when {
                            response.code() == 200 -> {
                                TokenManager.saveNewDatas(response.body()!!)
                                getTasks()
                            }
                            response.code() == 400 -> {
                                File(requireContext().filesDir, "User.dat").delete()
                            }
                            else -> {
                                Toast.makeText(context,"Что-то пошло не так! ${response.code()} ${response.message()}",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            )
        }
    }

    private fun getTasks()
    {
        val call = api.getTasks("Bearer " + TokenManager.AccessToken)
        call.enqueue(object : Callback<ArrayList<TaskResponse>> {
            override fun onResponse(
                call: Call<ArrayList<TaskResponse>>,
                response: Response<ArrayList<TaskResponse>>
            ) {
                when {
                    response.code() == 404 -> {
                        Toast.makeText(context, "Задания не найдены!", Toast.LENGTH_SHORT)
                            .show()
                        disableProgressBar()
                        if(!user!!.isTeacher)
                            findNavController().popBackStack()
                    }
                    response.code() == 200 -> {
                        val list = response.body()!!
                        list.reverse()
                        adapter.setItems(list)
                        disableProgressBar()
                    }
                    else -> {
                        findNavController().popBackStack()
                        Toast.makeText(requireContext(), "Что-то пошло не так! ${response.code()} ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<TaskResponse>>, t: Throwable) {
                Toast.makeText(context, "Что-то пошло не так! Проверье ваше подключение к сети.", Toast.LENGTH_SHORT)
                    .show()
                findNavController().popBackStack()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        MenuInflater(context).inflate(R.menu.menu_timetable, menu)
    }


}
