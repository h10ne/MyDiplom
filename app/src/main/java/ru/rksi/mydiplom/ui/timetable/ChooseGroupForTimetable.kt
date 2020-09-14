package ru.rksi.mydiplom.ui.timetable

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import kotlinx.serialization.json.Json
import retrofit2.Call
import ru.rksi.mydiplom.APIClasses.*
import ru.rksi.mydiplom.R
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class ChooseGroupForTimetable : Fragment() {

    private lateinit var sFaculty: Spinner
    private lateinit var sCourse: Spinner
    private lateinit var sGroup: Spinner
    private lateinit var applyBtn: Button
    private lateinit var api: RsueApi
    private lateinit var faculties: ArrayList<Faculty>
    private var courses: ArrayList<Course>? = null
    private var groups: ArrayList<Group>? = null
    private lateinit var progressBar: ProgressBar
    private var existingGroup: Group? = null
    private var groupExist = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_choose_group_for_timetable, container, false)

        sFaculty = root.findViewById(R.id.faculty)
        sCourse = root.findViewById(R.id.course)
        sGroup = root.findViewById(R.id.group)
        applyBtn = root.findViewById(R.id.applyBtnTimetable)
        this.progressBar = root.findViewById(R.id.loadCircle)
        loadSelectedGroup()
        init()

        return root
    }

    private fun loadSelectedGroup() {
        val file = File(requireContext().filesDir, "Current_User_Group.dat")
        if (file.exists()) {
            val fis = FileInputStream(file)
            existingGroup = Json.parse(Group.serializer(), String(fis.readBytes()))
            groupExist = true
        }
    }

    private fun loadEvents() {
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
        applyBtn.setOnClickListener { onApplyBtnClick() }
    }

    private fun saveSelectedGroup(group: Group) {
        val selGroupString = group.toJson()
        val fos = FileOutputStream(File(requireContext().filesDir, "Current_User_Group.dat"))
        fos.write(selGroupString.toByteArray())
        fos.close()
    }

    private fun onApplyBtnClick() {
        enableProgressBar()
        if (sGroup.selectedItem == null) {
            Toast.makeText(this.context, "Группа должна быть выбрана!", Toast.LENGTH_SHORT).show()
            disableProgressBar()
            return
        }
        val filesDir: File = requireContext().filesDir
        /*
        *       Сереализуем и записываем выбранную группу во внутреннее хранилище
         */
        var selGroup: Group = Group(-1, -1, "")
        groups?.forEach {
            if (it.name == sGroup.selectedItem)
                selGroup = it
        }
        if (selGroup.groupId == -1) {
            Toast.makeText(requireContext(), "Что-то пошло не так!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }
        saveSelectedGroup(selGroup)
        /*
        *       Получаем расписание с апи для выбранной группы
         */
        var isSchelWrite = false
        var schedule: Schedule
        val call: Call<Schedule> = api.getStudentSchedule(selGroup.groupId)
        call.enqueue(
            object : Callback<Schedule> {
                override fun onResponse(call: Call<Schedule>, response: Response<Schedule>) {
                    if (response.code() != 200) {
                        Toast.makeText(
                            requireContext(),
                            "Что-то пошло не так! ${response.code()} ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        disableProgressBar()
                        return
                    }
                    schedule = response.body()!!
                    isSchelWrite = writeSchedule(schedule, filesDir)
                    if (isSchelWrite) {
                        Toast.makeText(context, "Расписание успешно изменено!", Toast.LENGTH_SHORT)
                            .show()
                        view!!.findNavController().popBackStack()
                    } else {
                        Toast.makeText(context, "Что-то пошло не так!", Toast.LENGTH_SHORT).show()
                        disableProgressBar()
                    }
                }

                override fun onFailure(call: Call<Schedule>, t: Throwable) {
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

    private fun writeSchedule(schedule: Schedule, filesDir: File): Boolean {
        val schedStream = File(filesDir, "Schedule")
        val fos = FileOutputStream(schedStream)
        val selGroupSchel = schedule.toJson()
        fos.write(selGroupSchel.toByteArray())
        return true
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

    private fun init() {
        enableProgressBar()
        loadEvents()
        var retrofit = ApiClient.Instance
        api = retrofit.create(RsueApi::class.java)
        /*
        *Результат запроса для факультетов
        */
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

        if(groupExist)
        {
            var iterator = 1
            groups.forEach {
                if(it.name == existingGroup!!.name)
                    sGroup.setSelection(iterator)
                val smth = sGroup.selectedItem
                iterator++
            }
            groupExist = false
        }

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
        if(groupExist)
        {
            val selFac = existingGroup!!.name.substring(0,1).toInt()
            sFaculty.setSelection(selFac)
        }
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
        if(groupExist)
        {
            val cIndex = existingGroup!!.name.substring(1,2).toInt()
            sCourse.setSelection(cIndex)
        }
        disableProgressBar()
    }

    private fun disableProgressBar() {
        progressBar.visibility = View.INVISIBLE
        sGroup.isEnabled = true
        sCourse.isEnabled = true
        sFaculty.isEnabled = true
        applyBtn.isEnabled = true
    }

    private fun enableProgressBar() {
        progressBar.visibility = View.VISIBLE
        sGroup.isEnabled = false
        sCourse.isEnabled = false
        sFaculty.isEnabled = false
        applyBtn.isEnabled = false
    }

}
