package ru.rksi.mydiplom.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.rksi.mydiplom.APIClasses.ApiClient
import ru.rksi.mydiplom.APIClasses.RsueApi
import ru.rksi.mydiplom.APIClasses.Schedule
import ru.rksi.mydiplom.APIClasses.Week
import ru.rksi.mydiplom.Adapters.TimetableWeekAdapter
import ru.rksi.mydiplom.MainActivity
import ru.rksi.mydiplom.R
import ru.rksi.mydiplom.ui.timetable.TimetableFragment

class UserProfile : Fragment() {

    private lateinit var userphoto: RoundedImageView
    private lateinit var username: TextView
    private lateinit var userStatus: TextView
    private lateinit var lessonsList: ListView
    private lateinit var pager: ViewPager
    private lateinit var recyclerView: RecyclerView

    private val retrofit = ApiClient.Instance

    private val api = retrofit.create(RsueApi::class.java)

    private val lessons = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_user_profile, container, false)

        userphoto = root.findViewById(R.id.user_photo)
        username = root.findViewById(R.id.user_username)
        userStatus = root.findViewById(R.id.user_status)
        lessonsList = root.findViewById(R.id.lessons_list)
        pager = root.findViewById(R.id.viewPagerTimetable)
        recyclerView = root.findViewById(R.id.recyclerView)

        val bundle = arguments
        if (arguments != null) {
            Picasso.get().load(bundle!!.getString("avatar")).fit().placeholder(R.drawable.placeholder_user).centerCrop().into(userphoto)
            username.text = bundle!!.getString("name")
            (activity as MainActivity).supportActionBar?.title = "Профиль ${username.text}"
            lessons.addAll(bundle.getStringArrayList("lessons")!!)
            val adapter =
                ArrayAdapter(
                    requireContext(),
                    R.layout.simple_listview_item,
                    R.id.textView,
                    lessons
                )
            lessonsList.adapter = adapter

            var totalHeight = 0
            for (i in 0 until adapter.count) {
                val listItem: View = adapter.getView(i, null, lessonsList)
                listItem.measure(0, 0)
                totalHeight += listItem.measuredHeight
            }

            val params = lessonsList.layoutParams
            params.height =
                totalHeight + lessonsList.dividerHeight * (lessonsList.count - 1)
            lessonsList.layoutParams = params

            val id = bundle.getInt("teacherId")
            val call = api.getTeachersSchedule(id)

            call.enqueue(
                object : Callback<Schedule> {
                    override fun onFailure(call: Call<Schedule>, t: Throwable) {
                        Toast.makeText(
                            context,
                            "Что-то пошло не так! Проверье ваше подключение к сети.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    override fun onResponse(
                        call: Call<Schedule>,
                        response: Response<Schedule>
                    ) {
                        if (response.code() == 200) {
                            initSchedule(response.body()!!)
                        } else {
                            Toast.makeText(
                                context,
                                "Что-то пошло не так! ${response.code()} ${response.message()}",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            )

        }

        return root
    }

    private fun initSchedule(schedule: Schedule) {
        if (!requireView().context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                .getBoolean(("TTRec"), false)
        ) {
            recyclerView.visibility = View.GONE
            TimetableFragment.ScheduleLogic.addDaysInVP(
                schedule,
                true,
                pager,
                childFragmentManager
            )
        } else {
            pager.visibility = View.GONE
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            val adapter = TimetableWeekAdapter(requireContext(), true)

            val weeks = ArrayList<Week>()
            if (!schedule.isThisWeekEven) {
                weeks.add(schedule.unevenWeek)
                weeks.add(schedule.evenWeek)
            } else {
                weeks.add(schedule.evenWeek)
                weeks.add(schedule.unevenWeek)
            }

            adapter.setItems(weeks)
            recyclerView.adapter = adapter
        }

    }


}