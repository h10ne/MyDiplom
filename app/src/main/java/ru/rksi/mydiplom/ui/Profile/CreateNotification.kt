package ru.rksi.mydiplom.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.rksi.mydiplom.APIClasses.*

import ru.rksi.mydiplom.R
import ru.rksi.mydiplom.TokenManager
import java.io.File

class CreateNotification : Fragment() {

    private lateinit var sFaculty: Spinner
    private lateinit var sCourse: Spinner
    private lateinit var sGroup: Spinner
    private lateinit var etTitle: EditText
    private lateinit var etText: EditText
    private lateinit var addBut: Button
    private lateinit var pBar: ProgressBar

    private lateinit var faculties: ArrayList<Faculty>
    private var courses: ArrayList<Course>? = null
    private var groups: ArrayList<Group>? = null

    var retrofit = ApiClient.Instance
    val api = retrofit.create(RsueApi::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_create_notification, container, false)

        sFaculty = root.findViewById(R.id.faculty)
        sCourse = root.findViewById(R.id.course)
        sGroup = root.findViewById(R.id.group)
        etTitle = root.findViewById(R.id.title)
        etText = root.findViewById(R.id.text)
        addBut = root.findViewById(R.id.addNot)
        pBar = root.findViewById(R.id.loadCircle)

        addBut.setOnClickListener { addNotification() }

        init()

        initListeners()

        return root
    }

    private fun init() {
        val call: Call<ArrayList<Faculty>> = api.getFaculties()
        call.enqueue(
            object : Callback<ArrayList<Faculty>> {
                override fun onFailure(call: Call<ArrayList<Faculty>>, t: Throwable) {
                    Toast.makeText(
                        context,
                        "Что-то пошло не так! Проверье ваше подключение к сети.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                override fun onResponse(
                    call: Call<ArrayList<Faculty>>,
                    response: Response<ArrayList<Faculty>>
                ) {
                    if (response.code() == 200) {
                        loadFaculties(response.body()!!)
                        return
                    }
                    Toast.makeText(
                        context,
                        "Что-то пошло не так! Проверье ваше подключение к сети.",
                        Toast.LENGTH_SHORT
                    ).show()
                    disableProgressBar()


                }
            }
        )
    }

    private fun initListeners() {
        sFaculty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                sGroup.adapter = null
                sCourse.adapter = null
                var idFac = "Факультет"
                faculties.forEach {
                    if (sFaculty.selectedItem.toString() == it.name) {
                        idFac = it.facultyId.toString()
                    }
                }
                emptyCourse()
                emptyGroup()

                if (idFac == "Факультет") {
                    sCourse.isEnabled = false
                    return
                }

                sCourse.isEnabled = true
                sGroup.isEnabled = false
                /*
                *Результат запроса для группы
                */
                var call: Call<ArrayList<Course>> = api.getCourses(idFac.toInt())
                call.enqueue(
                    object : Callback<ArrayList<Course>> {
                        override fun onResponse(
                            call: Call<ArrayList<Course>>,
                            response: Response<ArrayList<Course>>
                        ) {
                            loadCourses(response.body()!!)
                        }

                        override fun onFailure(call: Call<ArrayList<Course>>, t: Throwable) {
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
        }

        sCourse.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                sGroup.adapter = null
                var idCourse: String = "Курс"
                courses?.forEach {
                    if (sCourse.selectedItem == it.number.toString()) {
                        idCourse = it.courseId.toString()
                    }
                }
                emptyGroup()

                if (idCourse == "Курс") {
                    sGroup.isEnabled = false
                    return
                }

                sGroup.isEnabled = true

                var call: Call<ArrayList<Group>> = api.getGroups(idCourse.toInt())
                call.enqueue(
                    object : Callback<ArrayList<Group>> {
                        override fun onResponse(
                            call: Call<ArrayList<Group>>,
                            response: Response<ArrayList<Group>>
                        ) {
                            loadGroups(response.body()!!)
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
        }
    }

    private fun loadGroups(groups: ArrayList<Group>) {
        enableProgressBar()
        this.groups = groups
        val groupsNames: ArrayList<String> = ArrayList()
        groupsNames += "Группа"
        groups.forEach {
            groupsNames += it.name
        }


        val facAdapter = ArrayAdapter(
            this.requireContext(),
            android.R.layout.simple_spinner_item,
            groupsNames
        )

        facAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sGroup.adapter = facAdapter
        disableProgressBar()
    }

    private fun loadFaculties(faculties: ArrayList<Faculty>) {
        this.faculties = faculties
        val facultiesNames: ArrayList<String> = ArrayList()
        facultiesNames.add("Факультет")
        faculties.forEach {
            facultiesNames += it.name
        }

        sCourse.isEnabled = false
        sGroup.isEnabled = false

        val facAdapter = ArrayAdapter(
            this.requireContext(),
            android.R.layout.simple_spinner_item,
            facultiesNames
        )

        facAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sFaculty.adapter = facAdapter
        disableProgressBar()
    }

    private fun loadCourses(courses: ArrayList<Course>) {
        enableProgressBar()
        this.courses = courses
        val coursesNames: ArrayList<String> = ArrayList()
        coursesNames.add("Курс")
        this.courses?.forEach {
            coursesNames += it.number.toString()
        }

        val adapter = ArrayAdapter(
            this.requireContext(),
            android.R.layout.simple_spinner_item,
            coursesNames
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sCourse.adapter = adapter
        disableProgressBar()
    }

    private fun disableProgressBar() {
        pBar.visibility = View.GONE
        addBut.isEnabled = true
    }

    private fun enableProgressBar() {
        pBar.visibility = View.VISIBLE
        addBut.isEnabled = false
    }

    private fun emptyCourse() {
        val coursesNames: ArrayList<String> = ArrayList()
        coursesNames.add("Курс")

        val adapter = ArrayAdapter(
            this.requireContext(),
            android.R.layout.simple_spinner_item,
            coursesNames
        )

        sCourse.adapter = adapter
    }

    private fun emptyGroup() {
        val groupNames: ArrayList<String> = ArrayList()
        groupNames.add("Группа")

        val adapter = ArrayAdapter(
            this.requireContext(),
            android.R.layout.simple_spinner_item,
            groupNames
        )

        sGroup.adapter = adapter
    }

    private fun addNotification() {
        enableProgressBar()
        if (TokenManager.IsTokenValid())
            sendNotification()
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
                                sendNotification()
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

    private fun sendNotification() {
        if (sGroup.selectedItem == "Группа") {
            Toast.makeText(this.context, "Группа должна быть выбрана!", Toast.LENGTH_SHORT).show()
            disableProgressBar()
            return
        }

        if (etText.text.isEmpty() || etTitle.text.isEmpty()) {
            Toast.makeText(this.context, "Группа должна быть выбрана!", Toast.LENGTH_SHORT).show()
            disableProgressBar()
            return
        }

        var gid: Int = 0
        groups!!.forEach {
            if (it.name == sGroup.selectedItem)
                gid = it.groupId
        }

        val call = api.sendNotification(
            "Bearer " + TokenManager.AccessToken,
            NotificationRequest(gid, etTitle.text.toString(), etText.text.toString())
        )
        call.enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(
                    context,
                    "Что-то пошло не так! Проверье ваше подключение к сети.",
                    Toast.LENGTH_SHORT
                )
                    .show()
                disableProgressBar()
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.code() == 204) {
                    Toast.makeText(requireContext(), "Уведомление отправлено!", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().popBackStack()
                } else
                    Toast.makeText(
                        context,
                        "Что-то пошло не так! Попробуйте еще раз! ${response.code()} ${response.message()}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                disableProgressBar()
            }
        })
    }

}
