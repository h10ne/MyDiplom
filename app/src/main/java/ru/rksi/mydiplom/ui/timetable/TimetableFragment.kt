package ru.rksi.mydiplom.ui.timetable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.rksi.mydiplom.APIClasses.Lesson
import ru.rksi.mydiplom.Adapters.TimetableAdapter
import ru.rksi.mydiplom.R

class TimetableFragment : Fragment() {
    private var adapter = TimetableAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_timetable, container, false)
        var timetableRec:RecyclerView = root.findViewById(R.id.timetableRecycle)
        timetableRec.adapter = adapter


        var lesson1 = Lesson("8:00 - 9:30","Математика","Кечек И.А.", "412","Лекция")
        var lesson2 = Lesson("9:40 - 11:10","Физкультура","Корабан М.А.", "с/з","")
        var lesson3 = Lesson("11:30 - 13:00","Мобильная разработка","Жабинская И.А.", "325","Практика")
        var day:ArrayList<Lesson> = ArrayList()
        day.add(lesson1)
        day.add(lesson2)
        day.add(lesson3)
        day.add(lesson1)
        day.add(lesson3)
        loadLessons(day)
        timetableRec.layoutManager = LinearLayoutManager(activity)
        return root
    }

    fun loadLessons(lessons:ArrayList<Lesson>)
    {
        adapter.setItems(lessons)
    }
}