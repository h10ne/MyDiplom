package ru.rksi.mydiplom.ui.timetable

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_set_teacher_for_timetable.*
import kotlinx.serialization.ImplicitReflectionSerializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.rksi.mydiplom.APIClasses.*
import ru.rksi.mydiplom.R
import java.io.File
import java.io.FileOutputStream

class setTeacherForTimetable : Fragment() {

    lateinit var teacherSpinner: AutoCompleteTextView
    private lateinit var loadCircle: ProgressBar
    private lateinit var applyBtn: Button
    private lateinit var api: RsueApi
    private var teachersList = ArrayList<Teacher>()


    @ImplicitReflectionSerializer
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_set_teacher_for_timetable, container, false)
        teacherSpinner = root.findViewById(R.id.teachersSpinner)
        loadCircle = root.findViewById(R.id.loadCircle)
        applyBtn = root.findViewById(R.id.applyBtnTimetable)
        val retrofit = ApiClient.Instance
        api = retrofit.create(RsueApi::class.java)
        initialize()

        return root
    }

    private fun initialize() {

        enableProgress()

        /*
        *   Подключаем вызов апи для преподов
        */
        val call: Call<ArrayList<Teacher>> = api.getTeachers()
        call.enqueue(
            object : Callback<ArrayList<Teacher>> {
                override fun onResponse(
                    call: Call<ArrayList<Teacher>>,
                    response: Response<ArrayList<Teacher>>
                ) {
                    if (response.code() != 200) {
                        Toast.makeText(
                            context,
                            "Что-то пошло не так! ${response.code()} ${response.message()}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        findNavController().popBackStack()
                        return
                    }
                    teachersList = response.body()!!
                    loadTeachers(response.body()!!)
                }

                override fun onFailure(call: Call<ArrayList<Teacher>>, t: Throwable) {
                    Toast.makeText(
                        context,
                        "Что-то пошло не так! Проверье ваше подключение к сети.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        )

        teacherSpinner.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
            if (b) {
                // Display the suggestion dropdown on focus
                teacherSpinner.showDropDown()
            }
        }

        teacherSpinner.setOnClickListener {
            teacherSpinner.showDropDown()
        }

        teacherSpinner.setOnItemClickListener { _: AdapterView<*>, _: View, _: Int, _: Long ->
            val inputMethodManager =
                requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
        }



        applyBtn.setOnClickListener { onApplyBtnClick() }

        disableProgress()
    }

    private fun loadTeachers(teachers: ArrayList<Teacher>) {
        val teachersNames = ArrayList<String>()
        teachers.forEach {
            if (it.position != "")
                teachersNames.add(it.position + " " + it.name)
        }

        //teachersSpinner.addTextChangedListener()

        val facAdapter = ArrayAdapter(
            this.requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            teachersNames
        )
        facAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        teacherSpinner.setAdapter(facAdapter)

    }

    private fun saveTeachersTimetable(schedule: Schedule, filesDir: File): Boolean {
        val schedStream = File(filesDir, "TeacherSchedule")
        val fos = FileOutputStream(schedStream)
        val selGroupSchel = schedule.toJson()
        fos.write(selGroupSchel.toByteArray())
        return true
    }

    private fun onApplyBtnClick() {
        enableProgress()
        if (teacherSpinner.text.toString().equals(0)) {
            Toast.makeText(this.context, "Преподователь должен быть выбран!", Toast.LENGTH_SHORT)
                .show()
            return
        }

        /*
        *   Записываем выбранного преподавателя в память
        */
        val filesDir: File = requireContext().filesDir

        var teacher = Teacher(-1, "", "")
        teachersList.forEach {
            if (it.position + " " + it.name == teacherSpinner.text.toString()) {
                teacher = it
                val currentUserGroup: File = File(filesDir, "Current_User_Teacher.dat")
                val selGroupString = it.toJson()

                val fos = FileOutputStream(currentUserGroup)
                fos.write(selGroupString.toByteArray())
                fos.close()
            }
        }

        /*
        *   Записываем расписание выбранного преподавателя в память
        */

        if (teacher.teacherId == -1) {
            Toast.makeText(
                requireContext(),
                "Необходимо правильно ввести преподавателя!",
                Toast.LENGTH_SHORT
            ).show()
            disableProgress()
            return
        }
        val call: Call<Schedule> = api.getTeachersSchedule(teacher.teacherId)
        call.enqueue(
            object : Callback<Schedule> {
                override fun onResponse(call: Call<Schedule>, response: Response<Schedule>) {
                    if (response.code() != 200) {
                        Toast.makeText(
                            requireContext(),
                            "Что-то пошло не так! ${response.code()} ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        disableProgress()
                        return
                    }
                    if (saveTeachersTimetable(response.body()!!, filesDir))
                        disableProgress()
                    Toast.makeText(context, "Расписание успешно изменено!", Toast.LENGTH_SHORT)
                        .show()
                    view!!.findNavController().popBackStack()
                }

                override fun onFailure(call: Call<Schedule>, t: Throwable) {
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

    private fun enableProgress() {
        loadCircle.visibility = View.VISIBLE
        teacherSpinner.isEnabled = false
        applyBtn.isEnabled = false
    }

    private fun disableProgress() {
        loadCircle.visibility = View.INVISIBLE
        teacherSpinner.isEnabled = true
        applyBtn.isEnabled = true
    }

}
