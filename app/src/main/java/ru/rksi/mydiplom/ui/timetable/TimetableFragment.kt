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
        var timeTable = mocTimeTable()
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

        var mlesson1 = Lesson(
            "8:30 — 10:00",
            "Иностранный язык",
            "ст.пр.Мартыненко Е.В.",
            "ауд.*308",
            "Практика"
        )
        var mlesson2 =
            Lesson("10:10 — 11:40", "Математика", "асс.Абдулрахман Х", "ауд.*308", "Практика")
        var mondayList: ArrayList<Lesson> = ArrayList()
        mondayList.add(mlesson1)
        mondayList.add(mlesson2)
        var monday = Day(mondayList)


        var tlesson1 = Lesson("8:30 — 10:00", "История", "доц.Чижикова Ю.А.", "ауд.*407", "Лекция")
        var tlesson2 =
            Lesson("10:10 — 11:40", "Экономическая теория", "проф.Губарь О.В", "ауд.*208", "Лекция")
        var tuesdayList: ArrayList<Lesson> = ArrayList()
        tuesdayList.add(tlesson1)
        tuesdayList.add(tlesson2)
        var tuesday = Day(tuesdayList)

        var wlesson1 = Lesson(
            "8:30 — 10:00",
            "Экономическая теория",
            "ст.пр.Попов М. В.",
            "ауд.*308",
            "Практика"
        )
        var wlesson2 =
            Lesson("10:10 — 11:40", "Математика", "доц.Чувенков А.Ф.", "ауд.*307", "Лекция")
        var wlesson3 = Lesson(
            "11:50 — 13:20",
            "Введение в специальность",
            "проф.Черненко О.Б.",
            "ауд.*404",
            "Лекция"
        )
        var wednesdayList: ArrayList<Lesson> = ArrayList()
        wednesdayList.add(wlesson1)
        wednesdayList.add(wlesson2)
        wednesdayList.add(wlesson3)
        var wednesday = Day(wednesdayList)


        var thlesson1 =
            Lesson("8:30 — 10:00", "История", "доц.Чижикова Ю.А.", "ауд.*403", "Практика")
        var thlesson2 =
            Lesson(
                "10:10 — 11:40",
                "Введение в специальность",
                "проф.Черненко О.Б.",
                "ауд.*403",
                "Практика"
            )
        var thursdayList: ArrayList<Lesson> = ArrayList()
        thursdayList.add(thlesson1)
        thursdayList.add(thlesson2)
        var thursday = Day(thursdayList)

        var flesson1 = Lesson(
            "8:30 — 10:00",
            "История государственного управления",
            "проф.Самыгин П.С.",
            "ауд.*207",
            "Лекция"
        )
        var flesson2 = Lesson(
            "10:10 — 11:40",
            "История государственного управления",
            "проф.Самыгин П.С.",
            "ауд.*311",
            "Практика"
        )
        var flesson3 = Lesson(
            "11:50 — 13:20",
            "Элективные дисциплины ( модули) по физической культуре и спорту",
            "NN-препод.",
            "ауд.*сз",
            "Практика"
        )
        var fridayList: ArrayList<Lesson> = ArrayList()
        fridayList.add(flesson1)
        fridayList.add(flesson2)
        fridayList.add(flesson3)
        var friday = Day(fridayList)

        var slesson1 = Lesson(
            "8:30 — 10:00",
            "Безопасность жизнедеятельности",
            "доц.Парада Е.В.",
            "ауд.*404",
            "Лекция"
        )
        var slesson2 =
            Lesson(
                "10:10 — 11:40",
                "Культура речи и деловое общение",
                "доц.Полякова О.А.",
                "ауд.*207",
                "Практика"
            )
        var saturdayList: ArrayList<Lesson> = ArrayList()
        saturdayList.add(slesson1)
        saturdayList.add(slesson2)
        var saturday = Day(saturdayList)

        var unevenWeekList: ArrayList<Day> = ArrayList()
        unevenWeekList.add(monday)
        unevenWeekList.add(tuesday)
        unevenWeekList.add(wednesday)
        unevenWeekList.add(thursday)
        unevenWeekList.add(friday)
        unevenWeekList.add(saturday)


        var unevenWeek = Week(unevenWeekList)

        var mlesson12 =
            Lesson("8:30 — 10:00", "Математика", "асс.Абдулрахман Х.", "ауд.*404", "Практика")
        var mlesson22 =
            Lesson(
                "10:10 — 11:40",
                "Безопасность жизнедеятельности",
                "доц.Парада Е.В",
                "ауд.*404",
                "Практика"
            )
        var mondayList2: ArrayList<Lesson> = ArrayList()
        mondayList2.add(mlesson12)
        mondayList2.add(mlesson22)
        var monday2 = Day(mondayList2)


        var tlesson12 = Lesson(
            "8:30 — 10:00",
            "Культура речи и деловое общение",
            "доц.Усенко Н.М.",
            "ауд.*207",
            "Лекция"
        )
        var tlesson22 =
            Lesson("10:10 — 11:40", "Экономическая теория", "проф.Губарь О.В", "ауд.*208", "Лекция")
        var tlesson32 = Lesson(
            "11:50 — 13:20",
            "Элективные дисциплины ( модули) по физической культуре и спорту",
            "NN-препод.",
            "ауд.*сз",
            "Практика"
        )
        var tuesdayList2: ArrayList<Lesson> = ArrayList()
        tuesdayList2.add(tlesson12)
        tuesdayList2.add(tlesson22)
        tuesdayList2.add(tlesson32)
        var tuesday2 = Day(tuesdayList2)

        var wlesson12 = Lesson(
            "8:30 — 10:00",
            "История государственного управления",
            "проф.Самыгин П.С.",
            "ауд.*403",
            "Лекция"
        )
        var wlesson22 = Lesson(
            "10:10 — 11:40",
            "История государственного управления",
            "проф.Самыгин П.С.",
            "ауд.*403",
            "Практика"
        )
        var wednesdayList2: ArrayList<Lesson> = ArrayList()
        wednesdayList2.add(wlesson12)
        wednesdayList2.add(wlesson22)
        var wednesday2 = Day(wednesdayList2)


        var thlesson12 = Lesson(
            "8:30 — 10:00",
            "Экономическая теория",
            "ст.пр.Попов М. В.",
            "ауд.*411",
            "Практика"
        )
        var thlesson22 =
            Lesson("10:10 — 11:40", "История", "доц.Чижикова Ю.А.", "ауд.*411", "Практика")
        var thlesson32 = Lesson(
            "11:50 — 13:20",
            "Иностранный язык",
            "ст.пр.Мартыненко Е.В.",
            "ауд.*403",
            "Практика"
        )

        var thursdayList2: ArrayList<Lesson> = ArrayList()
        thursdayList2.add(thlesson12)
        thursdayList2.add(thlesson22)
        thursdayList2.add(thlesson32)
        var thursday2 = Day(thursdayList2)

        var flesson12 = Lesson(
            "8:30 — 10:00",
            "Иностранный язык.",
            "ст.пр.Мартыненко Е.В.",
            "ауд.*404",
            "Практика"
        )
        var flesson22 = Lesson(
            "10:10 — 11:40",
            "Культура речи и деловое общение",
            "доц.Полякова О.А.",
            "ауд.*307",
            "Практика"
        )
        var flesson32 = Lesson(
            "11:50 — 13:20",
            "Элективные дисциплины ( модули) по физической культуре и спорту",
            "NN-препод.",
            "ауд.*сз",
            "Практика"
        )
        var fridayList2: ArrayList<Lesson> = ArrayList()
        fridayList2.add(flesson12)
        fridayList2.add(flesson22)
        fridayList2.add(flesson32)
        var friday2 = Day(fridayList2)


        var evenWeekList: ArrayList<Day> = ArrayList()
        evenWeekList.add(monday2)
        evenWeekList.add(tuesday2)
        evenWeekList.add(wednesday2)
        evenWeekList.add(thursday2)
        evenWeekList.add(friday2)
        var evenWeek = Week(evenWeekList)

        var weekList: ArrayList<Week> = ArrayList()
        weekList.add(unevenWeek)
        weekList.add(evenWeek)
        var timetableList: ArrayList<Week> = ArrayList()
        timetableList.add(unevenWeek)
        timetableList.add(evenWeek)
        var timetable = Timetable(timetableList)
        return timetable
        loadLessons(monday.lessons)
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
        var timeTable = mocTimeTable()
        updateAdapter(timeTable)
    }

    private fun setDateText() {
        currentDate.text = SimpleDateFormat("EEEE, d MMMM", Locale("ru")).format(selectedDate.time)
    }

    fun loadLessons(lessons: ArrayList<Lesson>) {
        adapter.setItems(lessons)
    }

    private fun updateAdapter(timetable: Timetable) {
        var weekNumber: Int = selectedDate.get(Calendar.WEEK_OF_YEAR)
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
            var lesson = Lesson("","В этот день у группы нет пар","","","")
            var lessons:ArrayList<Lesson> = ArrayList()
            lessons.add((lesson))
            loadLessons(lessons)
        }

    }
}