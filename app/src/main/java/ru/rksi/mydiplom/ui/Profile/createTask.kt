package ru.rksi.mydiplom.ui.profile

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.rksi.mydiplom.R
import java.util.*


class createTask : Fragment() {

    private lateinit var deadline: TextView
    private lateinit var calendar: Calendar
    lateinit var d: OnDateSetListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_create_task, container, false)
        calendar = Calendar.getInstance()
        d = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            deadline.text = initDate()
        }
        var deadline = root.findViewById<TextView>(R.id.deadline)
        deadline.setOnClickListener { setDate() }
        var str = initDate()
        deadline.text = str
        //initDate()



        return root
    }

    fun initDate():String {
        var curDate = DateUtils.formatDateTime(
            this.context,
            calendar.timeInMillis,
            DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
        )
        return  curDate
    }

    fun setDate() {
        DatePickerDialog(
            this.context!!,
            d,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
