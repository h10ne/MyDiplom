package ru.rksi.mydiplom.ui.timetable

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import ru.rksi.mydiplom.R

class TimetableSettings : Fragment() {

    private lateinit var switchTTView: Switch
    private lateinit var setting: SharedPreferences
    private lateinit var switchTeacherDefault: Switch

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_timetable_settings, container, false)

        setting = root.context.getSharedPreferences("settings", MODE_PRIVATE)
        switchTeacherDefault = root.findViewById(R.id.switchDefaultIsTeacher)

        switchTTView = root.findViewById(R.id.switchViewTT)

        init()
        return root
    }

    private fun init() {

        switchTTView.isChecked = setting.getBoolean("TTRec", false)
        switchTeacherDefault.isChecked = setting.getBoolean("TeacherDefault", false)

        switchTTView.setOnCheckedChangeListener { buttonView, isChecked ->
            run {
                setting.edit().putBoolean("TTRec", isChecked).apply()
            }
        }

        switchTeacherDefault.setOnCheckedChangeListener { buttonView, isChecked ->
            run {
                setting.edit().putBoolean("TeacherDefault", isChecked).apply()
            }
        }

    }

}
