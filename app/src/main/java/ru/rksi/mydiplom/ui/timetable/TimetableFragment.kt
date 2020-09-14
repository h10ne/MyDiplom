package ru.rksi.mydiplom.ui.timetable

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.TabHost
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.rksi.mydiplom.APIClasses.*
import ru.rksi.mydiplom.Adapters.*
import ru.rksi.mydiplom.MainActivity
import ru.rksi.mydiplom.R
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TimetableFragment : Fragment() {
    private lateinit var chooseGroup: TextView
    private lateinit var chooseTeacher: TextView
    private lateinit var pager: ViewPager
    private lateinit var teacherWeak: RecyclerView
    private lateinit var studentWeek: RecyclerView
    private lateinit var tabHost: TabHost
    private lateinit var setting: SharedPreferences
    private lateinit var teacherPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_timetable, container, false)
        this.chooseGroup = root.findViewById(R.id.chooseGroupTV)
        this.chooseTeacher = root.findViewById(R.id.chooseTeacherTV)
        pager = root.findViewById(R.id.viewPagerTimetable)
        teacherWeak = root.findViewById(R.id.teacherWeeklyRecycle)
        studentWeek = root.findViewById(R.id.studentWeeklyRecycle)
        tabHost = root.findViewById(R.id.tabHost)
        setting = root.context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        teacherPager = root.findViewById(R.id.viewPagerTimetableTeacher)
        init()
        //setDateText()

        return root
    }

    private fun init() {
        initStudentSchedule()
        initTeacherSchedule()
        tabHost.setup()

        var tabSpec = tabHost.newTabSpec("groupsTab")
        tabSpec.setIndicator("Группа")
        tabSpec.setContent(R.id.studentTimetable)
        tabHost.addTab(tabSpec)

        tabSpec = tabHost.newTabSpec("prepTab")
        tabSpec.setIndicator("Преподаватель")
        tabSpec.setContent(R.id.teacherTimetable)
        tabHost.addTab(tabSpec)

        if (!setting.getBoolean("TeacherDefault", false)) {
            tabHost.currentTab = 0
            changeTitle("groupsTab")
        } else {
            changeTitle("prepTab")
            tabHost.currentTab = 1
        }

        if (setting.getBoolean(("TTRec"), false)) {
            pager.visibility = View.GONE
            teacherPager.visibility = View.GONE
        } else {
            teacherWeak.visibility = View.GONE
            studentWeek.visibility = View.GONE
        }

        tabHost.setOnTabChangedListener { tabId ->
            run {
                changeTitle(tabId)
            }
        }
    }

    private fun changeTitle(tabId: String) {
        if (tabId == "groupsTab") {
            val filesDir: File = requireContext().filesDir
            val currentTeacher = File(filesDir, "Current_User_Group.dat")
            if (currentTeacher.exists()) {
                val fos = FileInputStream(currentTeacher)
                val group = Json.parse(Group.serializer(), String(fos.readBytes()))
                (activity as MainActivity).supportActionBar?.title = "Расписание ${group.name}"
            } else {
                (activity as MainActivity).supportActionBar?.title = "Расписание"
            }
        } else {
            val filesDir: File = requireContext().filesDir
            val currentTeacher = File(filesDir, "Current_User_Teacher.dat")
            if (currentTeacher.exists()) {
                val fos = FileInputStream(currentTeacher)
                val teacher = Json.parse(Teacher.serializer(), String(fos.readBytes()))
                (activity as MainActivity).supportActionBar?.title = "Расписание ${teacher.name}"
            } else {
                (activity as MainActivity).supportActionBar?.title = "Расписание"
            }
        }
    }


    private fun initTeacherSchedule() {
        val filesDir = requireContext().filesDir
        try {
            val schedDatas = File(filesDir, "TeacherSchedule")
            if (!schedDatas.exists()) {
                throw java.lang.Exception()
            }
            val fis = FileInputStream(schedDatas)
            val schedString = String(fis.readBytes())
            val schedule: Schedule = Json.parse(Schedule.serializer(), schedString)
            addDaysInVP(schedule, true, teacherPager, childFragmentManager)

            teacherWeak.layoutManager = LinearLayoutManager(this.context)
            val adapter = TimetableWeekAdapter(this.requireContext(), true)
            teacherWeak.adapter = adapter
            val weeks = ArrayList<Week>()
            if (!schedule.isThisWeekEven) {
                weeks.add(schedule.evenWeek)
                weeks.add(schedule.unevenWeek)
            } else {
                weeks.add(schedule.unevenWeek)
                weeks.add(schedule.evenWeek)
            }
            adapter.setItems(weeks)
            chooseTeacher.visibility = View.GONE

        } catch (ex: java.lang.Exception) {

        }
    }

    private fun setDateText(selectedDate: Calendar): String {
        return SimpleDateFormat("EEEE, d MMMM", Locale("ru")).format(selectedDate.time)
    }

    private fun initStudentSchedule() {
        val filesDir = requireContext().filesDir

        try {
            val schedDatas = File(filesDir, "Schedule")
            if (!schedDatas.exists()) {
                throw java.lang.Exception()
            }

            val fis = FileInputStream(schedDatas)
            val schedString = String(fis.readBytes())
            val schedule = toSchedule(schedString)
            addDaysInVP(schedule, false, pager, childFragmentManager)
            enableObjects()

            studentWeek.layoutManager = LinearLayoutManager(this.context)
            val adapter = TimetableWeekAdapter(this.requireContext(), false)
            studentWeek.adapter = adapter
            val weeks = ArrayList<Week>()
            if (!schedule.isThisWeekEven) {
                weeks.add(schedule.evenWeek)
                weeks.add(schedule.unevenWeek)
            } else {
                weeks.add(schedule.unevenWeek)
                weeks.add(schedule.evenWeek)
            }
            adapter.setItems(weeks)
            chooseGroup.visibility = View.GONE
        } catch (ex: java.lang.Exception) {

        }
    }

    companion object ScheduleLogic {
        private fun setDateText(selectedDate: Calendar): String {
            return SimpleDateFormat("EEEE, d MMMM", Locale("ru")).format(selectedDate.time)
        }

        fun addDaysInVP(
            schedule: Schedule,
            isTeacher: Boolean,
            pager: ViewPager,
            fragmentManager: FragmentManager
        ) {
            val today = Calendar.getInstance()
            var date = setDateText(today)
            var week = today.get(Calendar.WEEK_OF_YEAR)
            var day = 7 - (8 - GregorianCalendar().get(Calendar.DAY_OF_WEEK)) % 7

            val list = ArrayList<Fragment>()
            week = if (week % 2 == 0)
                0
            else
                1

            if (day == 7) {
                week = if (week == 0)
                    1
                else
                    0
                day = 0
                today.add(Calendar.DATE, 1)
            } else
                day -= 1

            if (today.get(Calendar.DAY_OF_WEEK) == 7) {
                today.add(Calendar.DATE, 1)
            }

            val daysInEven: ArrayList<Day> = ArrayList()
            val daysInUneven: ArrayList<Day> = ArrayList()

            var counter = 0;
            val dayToCompare = Calendar.getInstance()
            dayToCompare.timeInMillis = 1587945600000

            for (i: Int in 0..5) {
                val day = SimpleDateFormat("EEEE", Locale("ru")).format(dayToCompare.timeInMillis)
                if (schedule.evenWeek.days.size - 1 < counter || schedule.evenWeek.days[counter].toTime()
                        .toLowerCase(Locale.ROOT) != day.toLowerCase(Locale.ROOT)
                ) {
                    val day = Day(day, 0, ArrayList())
                    daysInEven.add(day)
                } else {
                    daysInEven.add(schedule.evenWeek.days[counter])
                    counter++
                }
                dayToCompare.add(Calendar.DATE, 1)
            }

            dayToCompare.timeInMillis = 1587945600000
            counter = 0
            for (i: Int in 0..5) {
                val day = SimpleDateFormat("EEEE", Locale("ru")).format(dayToCompare.timeInMillis)
                if (schedule.unevenWeek.days.size - 1 < counter || schedule.unevenWeek.days[counter].toTime()
                        .toLowerCase(Locale.ROOT) != day.toLowerCase(Locale.ROOT)
                ) {
                    val day = Day(day, 0, ArrayList())
                    daysInUneven.add(day)
                } else {
                    daysInUneven.add(schedule.unevenWeek.days[counter])
                    counter++
                }
                dayToCompare.add(Calendar.DATE, 1)
            }

            for (j in day..5) {
                val day = if (week == 0) {
                    daysInUneven[j]
                } else {
                    daysInEven[j]
                }
                list.add(
                    TimetableForViewPagerAdapter(
                        day,
                        isTeacher,
                        setDateText(today)
                    )
                )
                today.add(Calendar.DATE, 1)
            }
            today.add(Calendar.DATE, 1)

            week = if (week == 0)
                1
            else
                0

            for (j in 0..5) {
                val day = if (week == 0) {
                    daysInUneven[j]
                } else {
                    daysInEven[j]
                }
                list.add(
                    TimetableForViewPagerAdapter(
                        day,
                        isTeacher,
                        setDateText(today)
                    )
                )
                today.add(Calendar.DATE, 1)
            }
            today.add(Calendar.DATE, 1)

            week = if (week == 0)
                1
            else
                0
            for (j in 0 until day) {
                var day: Day
                if (week == 0) {
                    day = daysInUneven[j]
                } else {
                    day = daysInEven[j]
                }
                list.add(
                    TimetableForViewPagerAdapter(
                        day,
                        isTeacher,
                        setDateText(today)
                    )
                )
                today.add(Calendar.DATE, 1)
            }

            pager.adapter = SlidePagerAdapter(fragmentManager, list)

        }
    }


    private fun Refresh() {
        val retrofit = ApiClient.Instance
        val api = retrofit.create(RsueApi::class.java)
        val filesDir: File = requireContext().filesDir
        val currentUserGroup = File(filesDir, "Current_User_Group.dat")
        if (!currentUserGroup.exists()) {
            Toast.makeText(this.context, "Выберите группу!", Toast.LENGTH_SHORT).show()
            return
        }
        val fis = FileInputStream(currentUserGroup)
        val groupString = String(fis.readBytes())
        val group = toGroup(groupString)
        val call: Call<Schedule> = api.getStudentSchedule(group.groupId)

        call.enqueue(
            object : Callback<Schedule> {
                override fun onResponse(call: Call<Schedule>, response: Response<Schedule>) {
                    var schedule = response.body()!!
                    val currentChed = File(filesDir, "Schedule")
                    val fos = FileOutputStream(currentChed)
                    fos.write(schedule.toJson().toByteArray())

                    val fis = FileInputStream(currentChed)
                    schedule = toSchedule(String(fis.readBytes()))
                    //updateAdapter()
                    Toast.makeText(context, "Расписание успешно обновлено!", Toast.LENGTH_SHORT)
                        .show()
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

    private fun toGroup(stringValue: String): Group {
        return Json.parse(Group.serializer(), stringValue)
    }

    private fun toSchedule(stringValue: String): Schedule {
        return Json.parse(Schedule.serializer(), stringValue)
    }

    private fun enableObjects() {
        chooseGroup.visibility = View.INVISIBLE
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        MenuInflater(context).inflate(R.menu.menu_timetable, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.title) {
            "Выбрать преподавателя" -> requireView().findNavController()
                .navigate(R.id.setTeacherForTimetable)
            "Выбрать группу" -> requireView().findNavController()
                .navigate(R.id.chooseGroupForTimetable)
            "Настройки" -> requireView().findNavController().navigate(R.id.timetableSettings)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        initStudentSchedule()
        initTeacherSchedule()
    }

}
