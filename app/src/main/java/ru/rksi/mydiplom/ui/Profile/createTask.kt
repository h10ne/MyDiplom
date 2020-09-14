package ru.rksi.mydiplom.ui.profile

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import ru.rksi.mydiplom.APIClasses.*
import ru.rksi.mydiplom.R
import ru.rksi.mydiplom.TokenManager
import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.collections.ArrayList


class CreateTask() : Fragment() {

    private lateinit var deadline: TextView
    private lateinit var calendar: Calendar
    private lateinit var sGroup: Spinner
    private lateinit var sLesson: Spinner
    private lateinit var taskText: EditText
    private lateinit var retrofit: Retrofit
    private lateinit var api: RsueApi
    private lateinit var user: UserInfo
    private lateinit var saveBut: Button
    private lateinit var bar: ProgressBar
    private var taskResponse: TaskResponse? = null

    private var groups: ArrayList<Group> = ArrayList()
    private var lessons: ArrayList<Lesson> = ArrayList()
    private var selectedGroup: Group? = null
    private var selectedLesson: Lesson? = null

    private var today: Long = 0
    private lateinit var pickerDialog: DatePickerDialog
    lateinit var d: OnDateSetListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_create_task, container, false)

        deadline = root.findViewById(R.id.deadline)
        sGroup = root.findViewById(R.id.groupIdSpinner)
        sLesson = root.findViewById(R.id.lessonIdSpinner)
        taskText = root.findViewById(R.id.taskEditText)
        saveBut = root.findViewById(R.id.buttonAddTask)
        bar = root.findViewById(R.id.progress_circular)

        retrofit = ApiClient.Instance
        api = retrofit.create(RsueApi::class.java)

        calendar = Calendar.getInstance()
        d = OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            deadline.text = initDate()
        }
        deadline.setOnClickListener { pickerDialog.show() }
        setupEvents()
        init()

        saveBut.setOnClickListener { addTask() }

        val bundle = arguments
        if (arguments != null) {
            taskResponse = Json.parse(TaskResponse.serializer(), bundle?.getString("response")!!)
            initExist()
        }
        return root
    }

    private fun enableProgressBar() {
        saveBut.isEnabled = false
        taskText.isEnabled = false
        deadline.isEnabled = false
        sLesson.isEnabled = false
        sGroup.isEnabled = false
        bar.visibility = View.VISIBLE
    }

    private fun disableProgressBar() {
        saveBut.isEnabled = true
        taskText.isEnabled = true
        deadline.isEnabled = true
        sLesson.isEnabled = true
        sGroup.isEnabled = true
        bar.visibility = View.INVISIBLE
    }

    private fun getLessonsForUser() {
        val call =
            api.getLessonsForUser(
                "Bearer " + TokenManager.AccessToken,
                groupId = selectedGroup!!.groupId
            )
        call.enqueue(
            object : Callback<ArrayList<Lesson>> {
                override fun onResponse(
                    call: Call<ArrayList<Lesson>>,
                    response: Response<ArrayList<Lesson>>
                ) {
                    loadItemsToLessons(response.body()!!)
                }

                override fun onFailure(call: Call<ArrayList<Lesson>>, t: Throwable) {
                    Toast.makeText(
                        context,
                        "Что-то пошло не так! Проверье ваше подключение к сети.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        )
    }

    private fun setupEvents() {
        sGroup.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedGroup = null
                groups.forEach {
                    if (sGroup.selectedItem.toString() == it.name) {
                        selectedGroup = it
                    }
                }

                if(selectedGroup == null)
                {
                    val lessonsNames: ArrayList<String> = ArrayList()
                    lessonsNames.add("Дисциплина")
                    val lessonAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        lessonsNames
                    )
                    lessonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    sLesson.adapter = lessonAdapter

                    return
                }

                if (TokenManager.IsTokenValid()) {
                    getLessonsForUser()
                } else
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
                                        getLessonsForUser()
                                    }
                                    response.code() == 400 -> {
                                        File(requireContext().filesDir, "User.dat").delete()
                                    }
                                    else -> {
                                        Toast.makeText(
                                            context,
                                            "Что-то пошло не так! ${response.code()} ${response.message()}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }


        sLesson.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                lessons.forEach {
                    if (sLesson.selectedItem.toString() == it.title)
                        selectedLesson = it
                }
            }
        }
    }

    private fun getGroupIndex(): Int {
        var index = 0
        groups.forEach {
            if (it == taskResponse?.group) {
                return index
            }
            index++
        }

        return -1
    }

    private fun initExist() {
        calendar.timeInMillis = taskResponse?.deadline!! * 1000
        deadline.text = initDate()
        taskText.setText(taskResponse?.text)
        sGroup.setSelection(getGroupIndex())
        saveBut.text = "Изменить"

    }

    private fun loadItemsToLessons(lessons: ArrayList<Lesson>) {
        this.lessons = lessons
        val groupsNames: ArrayList<String> = ArrayList()
        groupsNames.add("Дисциплина")
        lessons.forEach {
            groupsNames += it.title
        }
        val lessonAdapter = ArrayAdapter(
            this.requireContext(),
            android.R.layout.simple_spinner_item,
            groupsNames
        )
        lessonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sLesson.adapter = lessonAdapter
    }

    private fun getGroupsForUser() {
        val call = api.getGroupsForUser("Bearer " + TokenManager.AccessToken)

        call.enqueue(object : Callback<ArrayList<Group>> {
            override fun onResponse(
                call: Call<ArrayList<Group>>,
                response: Response<ArrayList<Group>>
            ) {
                if (response.code() == 200) {
                    groups = response.body()!!
                    loadGroupsToSpinner(groups)
                } else {
                    findNavController().popBackStack()
                    Toast.makeText(
                        context,
                        "Что-то пошло не так! ${response.code()} ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ArrayList<Group>>, t: Throwable) {
                Toast.makeText(
                    context,
                    "Что-то пошло не так! Проверье ваше подключение к сети.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
        )
    }

    private fun init() {
        calendar.add(Calendar.DATE, 1)
        deadline.text = initDate()
        pickerDialog = DatePickerDialog(
            this.requireContext(),
            R.style.DialogTheme,
            d,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        pickerDialog.datePicker.minDate = calendar.timeInMillis
        pickerDialog

        val filesDir: File = requireContext().filesDir
        val userPath = File(filesDir, "User.dat")
        val fis = FileInputStream(userPath)
        val userJson = String(fis.readBytes())
        user = Json.parse(UserInfo.serializer(), userJson)

        if (TokenManager.IsTokenValid()) {
            getGroupsForUser()
        } else {
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
                                getGroupsForUser()
                            }
                            response.code() == 400 -> {
                                File(requireContext().filesDir, "User.dat").delete()
                            }
                            else -> {
                                Toast.makeText(
                                    context,
                                    "Что-то пошло не так! ${response.code()} ${response.message()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            )
        }
    }

    private fun loadGroupsToSpinner(groups: ArrayList<Group>) {
        val groupsNames: ArrayList<String> = ArrayList()
        groupsNames.add("Группа")
        groups.forEach {
            groupsNames += it.name
        }
        val groupAdapter = ArrayAdapter(
            this.requireContext(),
            android.R.layout.simple_spinner_item,
            groupsNames
        )
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sGroup.adapter = groupAdapter
    }

    private fun initDate(): String {
        return DateUtils.formatDateTime(
            context,
            calendar.timeInMillis,
            DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
        )
    }

    private fun apiAddTask(response: TaskResponse) {
        val call = api.addTasks("Bearer " + TokenManager.AccessToken, response)

        call.enqueue(
            object : Callback<Void> {
                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {
                    if (response.code() == 200) {
                        Toast.makeText(
                            context,
                            "Задание успешно добавлено!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        disableProgressBar()
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(
                            context,
                            "Что-то пошло не так! ${response.code()} ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        disableProgressBar()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(
                        context,
                        "Что-то пошло не так! Проверье ваше подключение к сети.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    disableProgressBar()
                }
            }
        )
    }

    private fun apiChangeTask(response: TaskResponse) {
        val call =
            api.changeTasks("Bearer " + TokenManager.AccessToken, response, response.taskId)

        call.enqueue(
            object : Callback<Void> {
                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {
                    if (response.code() == 204) {
                        Toast.makeText(context, "Задание успешно изменено!", Toast.LENGTH_SHORT)
                            .show()
                        disableProgressBar()
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(
                            context,
                            "Что-то пошло не так! ${response.code()} ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        disableProgressBar()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(
                        context,
                        "Что-то пошло не так! Проверье ваше подключение к сети.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    disableProgressBar()
                }
            }
        )
    }

    private fun addTask() {
        enableProgressBar()
        if (selectedGroup == null || selectedLesson == null || taskText.text.toString() == "") {
            Toast.makeText(this.context, "Все поля должны быть заполнены!", Toast.LENGTH_SHORT)
                .show()
            disableProgressBar()
            return
        }
        deadline.text

        if (taskResponse == null) {
            val TResponse = TaskResponse(
                0,
                taskText.text.toString(),
                calendar.timeInMillis / 1000,
                Teacher(0, "", ""),
                selectedGroup!!,
                selectedLesson!!
            )
            if (TokenManager.IsTokenValid())
                apiAddTask(TResponse)
            else {
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
                                    apiAddTask(TResponse)
                                }
                                response.code() == 400 -> {
                                    File(requireContext().filesDir, "User.dat").delete()
                                }
                                else -> {
                                    Toast.makeText(
                                        context,
                                        "Что-то пошло не так! ${response.code()} ${response.message()}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                )
            }
        } else {
            val TResponse = TaskResponse(
                taskResponse!!.taskId,
                taskText.text.toString(),
                calendar.timeInMillis / 1000,
                taskResponse!!.teacher,
                selectedGroup!!,
                selectedLesson!!
            )
            if (TokenManager.IsTokenValid())
                apiChangeTask(TResponse)
            else {
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
                                    apiChangeTask(TResponse)
                                }
                                response.code() == 400 -> {
                                    File(requireContext().filesDir, "User.dat").delete()
                                }
                                else -> {
                                    Toast.makeText(
                                        context,
                                        "Что-то пошло не так! ${response.code()} ${response.message()}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                )
            }
        }

    }
}
