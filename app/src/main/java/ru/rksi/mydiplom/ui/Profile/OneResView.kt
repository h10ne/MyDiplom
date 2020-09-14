package ru.rksi.mydiplom.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.rksi.mydiplom.APIClasses.FormOfControl
import ru.rksi.mydiplom.APIClasses.Performance
import ru.rksi.mydiplom.Adapters.SemesterAdapter
import ru.rksi.mydiplom.R

class OneResView(private val performance: Performance) : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_one_recview, container, false)
        recyclerView = root.findViewById(R.id.recView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        initMarks(performance)


        return root
    }

    fun initMarks(performance: Performance)
    {
        val adapter = SemesterAdapter(requireContext())
        recyclerView.adapter = adapter

        val exam = FormOfControl("Экзамен", performance.semester.exams)
        val credit = FormOfControl("Зачёт", performance.semester.credits)
        val practice = FormOfControl("Практика", performance.semester.practices)

        val typesOfControl: ArrayList<FormOfControl> = ArrayList()
        if (exam.marks.isNotEmpty())
            typesOfControl.add(exam)
        if (credit.marks.isNotEmpty())
            typesOfControl.add(credit)
        if (practice.marks.isNotEmpty())
            typesOfControl.add(practice)

        adapter.setItems(typesOfControl)
    }
}
