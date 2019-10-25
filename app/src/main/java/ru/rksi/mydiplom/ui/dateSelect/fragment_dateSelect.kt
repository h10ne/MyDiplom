package ru.rksi.mydiplom.ui.dateSelect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.rksi.mydiplom.R
import java.text.SimpleDateFormat
import java.util.*


class fragment_dateSelect : Fragment() {

    companion object {
        fun newInstance() = fragment_dateSelect()
    }
    var today:Calendar = Calendar.getInstance()
    var counter = 0
    lateinit var tomorrow:Calendar
    lateinit var selectedDate: Calendar
    lateinit var currentDay:TextView
    lateinit var currentDate:TextView
    lateinit var btNextDay:ImageButton
    lateinit var btPrevDay:ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_date_select_fragment, container, false)
        this.currentDay = root.findViewById(R.id.dayName)
        this.currentDate = root.findViewById(R.id.textDate)
        this.btNextDay = root.findViewById(R.id.buttonNextDate)
        this.btPrevDay = root.findViewById(R.id.buttonPrevDate)
        btPrevDay.isEnabled = false
        Init()
        return root
    }

    private fun Init()
    {
        tomorrow = today.clone() as Calendar
        tomorrow.add(Calendar.DATE,1)
        selectedDate = today.clone() as Calendar
        btPrevDay.setOnClickListener{btPrevDate_Click()}
        btNextDay.setOnClickListener {btNextDate_Click()}
        setDateText()
    }

    fun btNextDate_Click()
    {
        btPrevDay.isEnabled = true
        counter++
        if(counter == 1)
            currentDay.text = "Завтра"
        else if (counter > 1)
            currentDay.text = "На дату"
        selectedDate.add(Calendar.DATE, 1)
        setDateText()
    }
    fun btPrevDate_Click()
    {
        if(counter!=0)
            counter--
        else
            return
        if(counter == 1)
            currentDay.text = "Завтра"
        else if (counter > 1)
            currentDay.text = "На дату"
        else if(counter == 0)
        {
            btPrevDay.isEnabled = false
            currentDay.text = "Сегодня"
        }
        selectedDate.add(Calendar.DATE, -1)
        setDateText()
    }

    private fun setDateText()
    {
        currentDate.text = SimpleDateFormat("EEEE, d MMMM", Locale("ru")).format(selectedDate.time)
    }

}
