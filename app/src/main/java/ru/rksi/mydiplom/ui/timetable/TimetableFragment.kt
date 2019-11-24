package ru.rksi.mydiplom.ui.timetable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.rksi.mydiplom.APIClasses.Day
import ru.rksi.mydiplom.APIClasses.Lesson
import ru.rksi.mydiplom.APIClasses.Timetable
import ru.rksi.mydiplom.APIClasses.Week
import ru.rksi.mydiplom.Adapters.TimetableAdapter
import ru.rksi.mydiplom.R
import ru.rksi.mydiplom.enums.WeekState
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TimetableFragment : Fragment() {
    private var adapter = TimetableAdapter()
    var today: Calendar = Calendar.getInstance()
    var counter = 0
    lateinit var tomorrow: Calendar
    lateinit var selectedDate: Calendar
    lateinit var currentDay: TextView
    lateinit var currentDate: TextView
    lateinit var btNextDay: ImageButton
    lateinit var btPrevDay: ImageButton
    lateinit var timetableRec: RecyclerView
    lateinit var weekState: WeekState

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_timetable, container, false)
        timetableRec = root.findViewById(R.id.timetableRecycle)
        timetableRec.adapter = adapter
        this.currentDay = root.findViewById(R.id.dayName)
        this.currentDate = root.findViewById(R.id.textDate)
        this.btNextDay = root.findViewById(R.id.buttonNextDate)
        this.btPrevDay = root.findViewById(R.id.buttonPrevDate)
        Init()
        setDateText()
        val timeTable = mocTimeTable()
        updateAdapter(timeTable)


        return root
    }

    fun Init() {
        timetableRec.layoutManager = LinearLayoutManager(activity)
        btPrevDay.isEnabled = false
        btPrevDay.isEnabled = false
        tomorrow = today.clone() as Calendar
        tomorrow.add(Calendar.DATE, 1)
        selectedDate = today.clone() as Calendar
        btPrevDay.setOnClickListener { btPrevDate_Click() }
        btNextDay.setOnClickListener { btNextDate_Click() }
        setDateText()
        mocTimeTable()
    }

    private fun mocTimeTable(): Timetable {

        val mlesson1 = Lesson(
            "8:30 — 10:00",
            "Иностранный язык",
            "ст.пр.Мартыненко Е.В.",
            "ауд.*308",
            "Практика"
        )
        val mlesson2 =
            Lesson("10:10 — 11:40", "Математика", "асс.Абдулрахман Х", "ауд.*308", "Практика")
        val mondayList: ArrayList<Lesson> = ArrayList()
        mondayList.add(mlesson1)
        mondayList.add(mlesson2)
        val monday = Day(mondayList)


        val tlesson1 = Lesson("8:30 — 10:00", "История", "доц.Чижикова Ю.А.", "ауд.*407", "Лекция")
        val tlesson2 =
            Lesson("10:10 — 11:40", "Экономическая теория", "проф.Губарь О.В", "ауд.*208", "Лекция")
        val tuesdayList: ArrayList<Lesson> = ArrayList()
        tuesdayList.add(tlesson1)
        tuesdayList.add(tlesson2)
        val tuesday = Day(tuesdayList)

        val wlesson1 = Lesson(
            "8:30 — 10:00",
            "Экономическая теория",
            "ст.пр.Попов М. В.",
            "ауд.*308",
            "Практика"
        )
        val wlesson2 =
            Lesson("10:10 — 11:40", "Математика", "доц.Чувенков А.Ф.", "ауд.*307", "Лекция")
        val wlesson3 = Lesson(
            "11:50 — 13:20",
            "Введение в специальность",
            "проф.Черненко О.Б.",
            "ауд.*404",
            "Лекция"
        )
        val wednesdayList: ArrayList<Lesson> = ArrayList()
        wednesdayList.add(wlesson1)
        wednesdayList.add(wlesson2)
        wednesdayList.add(wlesson3)
        val wednesday = Day(wednesdayList)


        val thlesson1 =
            Lesson("8:30 — 10:00", "История", "доц.Чижикова Ю.А.", "ауд.*403", "Практика")
        val thlesson2 =
            Lesson(
                "10:10 — 11:40",
                "Введение в специальность",
                "проф.Черненко О.Б.",
                "ауд.*403",
                "Практика"
            )
        val thursdayList: ArrayList<Lesson> = ArrayList()
        thursdayList.add(thlesson1)
        thursdayList.add(thlesson2)
        val thursday = Day(thursdayList)

        val flesson1 = Lesson(
            "8:30 — 10:00",
            "История государственного управления",
            "проф.Самыгин П.С.",
            "ауд.*207",
            "Лекция"
        )
        val flesson2 = Lesson(
            "10:10 — 11:40",
            "История государственного управления",
            "проф.Самыгин П.С.",
            "ауд.*311",
            "Практика"
        )
        val flesson3 = Lesson(
            "11:50 — 13:20",
            "Элективные дисциплины ( модули) по физической культуре и спорту",
            "NN-препод.",
            "ауд.*сз",
            "Практика"
        )
        val fridayList: ArrayList<Lesson> = ArrayList()
        fridayList.add(flesson1)
        fridayList.add(flesson2)
        fridayList.add(flesson3)
        val friday = Day(fridayList)

        val slesson1 = Lesson(
            "8:30 — 10:00",
            "Безопасность жизнедеятельности",
            "доц.Парада Е.В.",
            "ауд.*404",
            "Лекция"
        )
        val slesson2 =
            Lesson(
                "10:10 — 11:40",
                "Культура речи и деловое общение",
                "доц.Полякова О.А.",
                "ауд.*207",
                "Практика"
            )
        val saturdayList: ArrayList<Lesson> = ArrayList()
        saturdayList.add(slesson1)
        saturdayList.add(slesson2)
        val saturday = Day(saturdayList)

        val unevenWeekList: ArrayList<Day> = ArrayList()
        unevenWeekList.add(monday)
        unevenWeekList.add(tuesday)
        unevenWeekList.add(wednesday)
        unevenWeekList.add(thursday)
        unevenWeekList.add(friday)
        unevenWeekList.add(saturday)


        val unevenWeek = Week(unevenWeekList)

        val mlesson12 =
            Lesson("8:30 — 10:00", "Математика", "асс.Абдулрахман Х.", "ауд.*404", "Практика")
        val mlesson22 =
            Lesson(
                "10:10 — 11:40",
                "Безопасность жизнедеятельности",
                "доц.Парада Е.В",
                "ауд.*404",
                "Практика"
            )
        val mondayList2: ArrayList<Lesson> = ArrayList()
        mondayList2.add(mlesson12)
        mondayList2.add(mlesson22)
        val monday2 = Day(mondayList2)


        val tlesson12 = Lesson(
            "8:30 — 10:00",
            "Культура речи и деловое общение",
            "доц.Усенко Н.М.",
            "ауд.*207",
            "Лекция"
        )
        val tlesson22 =
            Lesson("10:10 — 11:40", "Экономическая теория", "проф.Губарь О.В", "ауд.*208", "Лекция")
        val tlesson32 = Lesson(
            "11:50 — 13:20",
            "Элективные дисциплины ( модули) по физической культуре и спорту",
            "NN-препод.",
            "ауд.*сз",
            "Практика"
        )
        val tuesdayList2: ArrayList<Lesson> = ArrayList()
        tuesdayList2.add(tlesson12)
        tuesdayList2.add(tlesson22)
        tuesdayList2.add(tlesson32)
        val tuesday2 = Day(tuesdayList2)

        val wlesson12 = Lesson(
            "8:30 — 10:00",
            "История государственного управления",
            "проф.Самыгин П.С.",
            "ауд.*403",
            "Лекция"
        )
        val wlesson22 = Lesson(
            "10:10 — 11:40",
            "История государственного управления",
            "проф.Самыгин П.С.",
            "ауд.*403",
            "Практика"
        )
        val wednesdayList2: ArrayList<Lesson> = ArrayList()
        wednesdayList2.add(wlesson12)
        wednesdayList2.add(wlesson22)
        val wednesday2 = Day(wednesdayList2)


        val thlesson12 = Lesson(
            "8:30 — 10:00",
            "Экономическая теория",
            "ст.пр.Попов М. В.",
            "ауд.*411",
            "Практика"
        )
        val thlesson22 =
            Lesson("10:10 — 11:40", "История", "доц.Чижикова Ю.А.", "ауд.*411", "Практика")
        val thlesson32 = Lesson(
            "11:50 — 13:20",
            "Иностранный язык",
            "ст.пр.Мартыненко Е.В.",
            "ауд.*403",
            "Практика"
        )

        val thursdayList2: ArrayList<Lesson> = ArrayList()
        thursdayList2.add(thlesson12)
        thursdayList2.add(thlesson22)
        thursdayList2.add(thlesson32)
        val thursday2 = Day(thursdayList2)

        val flesson12 = Lesson(
            "8:30 — 10:00",
            "Иностранный язык.",
            "ст.пр.Мартыненко Е.В.",
            "ауд.*404",
            "Практика"
        )
        val flesson22 = Lesson(
            "10:10 — 11:40",
            "Культура речи и деловое общение",
            "доц.Полякова О.А.",
            "ауд.*307",
            "Практика"
        )
        val flesson32 = Lesson(
            "11:50 — 13:20",
            "Элективные дисциплины ( модули) по физической культуре и спорту",
            "NN-препод.",
            "ауд.*сз",
            "Практика"
        )
        val fridayList2: ArrayList<Lesson> = ArrayList()
        fridayList2.add(flesson12)
        fridayList2.add(flesson22)
        fridayList2.add(flesson32)
        val friday2 = Day(fridayList2)


        val evenWeekList: ArrayList<Day> = ArrayList()
        evenWeekList.add(monday2)
        evenWeekList.add(tuesday2)
        evenWeekList.add(wednesday2)
        evenWeekList.add(thursday2)
        evenWeekList.add(friday2)
        val evenWeek = Week(evenWeekList)

        val weekList: ArrayList<Week> = ArrayList()
        weekList.add(unevenWeek)
        weekList.add(evenWeek)
        val timetableList: ArrayList<Week> = ArrayList()
        timetableList.add(unevenWeek)
        timetableList.add(evenWeek)
        val timetable = Timetable(timetableList)
        return timetable
    }

    fun btNextDate_Click() {
        btPrevDay.isEnabled = true
        counter++
        if (counter == 1)
            currentDay.text = "Завтра"
        else if (counter > 1)
            currentDay.text = "На дату"
        selectedDate.add(Calendar.DATE, 1)
        setDateText()
        var timeTable = mocTimeTable()
        updateAdapter(timeTable)
    }

    fun btPrevDate_Click() {
        if (counter != 0)
            counter--
        else
            return
        if (counter == 1)
            currentDay.text = "Завтра"
        else if (counter > 1)
            currentDay.text = "На дату"
        else if (counter == 0) {
            btPrevDay.isEnabled = false
            currentDay.text = "Сегодня"
        }
        selectedDate.add(Calendar.DATE, -1)
        setDateText()
        val timeTable = mocTimeTable()
        updateAdapter(timeTable)
    }

    private fun setDateText() {
        currentDate.text = SimpleDateFormat("EEEE, d MMMM", Locale("ru")).format(selectedDate.time)
    }

    fun loadLessons(lessons: ArrayList<Lesson>) {
        adapter.setItems(lessons)
    }

    private fun updateAdapter(timetable: Timetable) {
        val weekNumber: Int = selectedDate.get(Calendar.WEEK_OF_YEAR)
        if (weekNumber % 2 == 0)
            weekState = WeekState.EVEN
        else
            weekState = WeekState.UNEVEN
        var currentDay: Day
        var week:Int
        var day:Int
        if (weekState == WeekState.EVEN)
            week=1
        else
            week=0
        day = selectedDate.get(Calendar.DAY_OF_WEEK)
        day-=2
        try {
            currentDay = timetable.Weeks[week].days[day]
            loadLessons(currentDay.lessons)
        } catch (ex:Exception){
            val lesson = Lesson("","В этот день у группы нет пар","","","")
            val lessons:ArrayList<Lesson> = ArrayList()
            lessons.add((lesson))
            loadLessons(lessons)
        }

    }
}