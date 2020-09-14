package ru.rksi.mydiplom.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.rksi.mydiplom.APIClasses.*
import ru.rksi.mydiplom.R
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class ProfileFragment : Fragment() {

    private lateinit var userName: TextView
    private lateinit var userRank: TextView
    private lateinit var userPhotocard: RoundedImageView
    private var user: UserInfo? = null
    private lateinit var menu: GridView
    //private lateinit var helpButton: Button

    private var NOTIFICATION_ID = -1
    private var MY_FAC_ID = -1
    private var TASK_ID = -1
    private var CHECKPOINTS_ID = -1
    private var LOGOUT_ID = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        userName = root.findViewById(R.id.profile_nameSurname)
        userRank = root.findViewById(R.id.profile_rank)
        userPhotocard = root.findViewById(R.id.userPhoto)
        menu = root.findViewById(R.id.gvMenuList)

        val filesDir: File = requireContext().filesDir
        val userPath = File(filesDir, "User.dat")


        val noLogin = !userPath.exists()
        if (noLogin) {
            //findNavController().popBackStack()
            findNavController().navigate(R.id.noLogin)
            return root
        }
        initData(userPath)
        downloadDataToGridView()
        menu.onItemClickListener =
            AdapterView.OnItemClickListener { _, view, _, id ->
                when (id) {
                    MY_FAC_ID.toLong() -> {
                        findNavController().navigate(R.id.myFacultyFragment)
                    }
                    NOTIFICATION_ID.toLong() -> {
                        findNavController().navigate(R.id.notificationFragment)
                    }
                    TASK_ID.toLong() -> {
                        requireView().findNavController().navigate(R.id.tasksFragment)
                    }
                    CHECKPOINTS_ID.toLong() -> {
                        requireView().findNavController().navigate(R.id.marksFragment)
                    }
                    LOGOUT_ID.toLong() -> {
                        val dialog = AlertDialog.Builder(requireContext())
                            .setTitle("Вы уверены, что хотите выйти?")
                            .setPositiveButton(
                                android.R.string.yes
                            ) { _, _ ->
                                File(requireContext().filesDir, "User.dat").delete()
                                findNavController().popBackStack()
                                findNavController().navigate(R.id.noLogin)
                            }
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .create()
                        dialog.setOnShowListener {
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                                .setTextColor(resources.getColor(R.color.colorPrimary))
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                .setTextColor(resources.getColor(R.color.colorPrimary))
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).text = "Выйти"
                        }
                        dialog.show()
                    }
                }
            }
        val setting = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        val wasOffer = setting.getBoolean("WasOffer", true)
        if (!wasOffer) {
            var userProfile = "студента"
            if (user!!.isTeacher)
                userProfile = "преподавателя"
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("Вы вошли под профилем $userProfile.")
                .setMessage("Сменить расписание $userProfile на актуальное для Вас и использовать его по умолчанию?")
                .setPositiveButton(
                    android.R.string.yes
                ) { _, _ ->
                    writeSchedule()
                }
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .create()
            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(resources.getColor(R.color.colorPrimary))
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(resources.getColor(R.color.colorPrimary))
            }
            dialog.show()

            setting.edit().putBoolean("WasOffer", true).apply()
        }
        return root
    }

    private fun writeSchedule() {
        val retrofit = ApiClient.Instance
        val api = retrofit.create(RsueApi::class.java)
        if (user!!.isTeacher) {

            val currentUserGroup: File = File(requireContext().filesDir, "Current_User_Teacher.dat")
            val teacher = Teacher(user!!.id, user!!.username, "")

            val fos = FileOutputStream(currentUserGroup)
            fos.write(teacher.toJson().toByteArray())
            fos.close()

            val call = api.getTeachersSchedule(user!!.id)
            call.enqueue(
                object : Callback<Schedule> {
                    override fun onResponse(call: Call<Schedule>, response: Response<Schedule>) {
                        saveSchedule(response.body()!!)
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
        } else {
            val currentUserGroup: File = File(requireContext().filesDir, "Current_User_Group.dat")
            val group = Group(user!!.group!!.groupId, user!!.group!!.number, user!!.group!!.name)

            val fos = FileOutputStream(currentUserGroup)
            fos.write(group.toJson().toByteArray())
            fos.close()

            val call = api.getStudentSchedule(user!!.id)
            call.enqueue(
                object : Callback<Schedule> {
                    override fun onResponse(call: Call<Schedule>, response: Response<Schedule>) {
                        saveSchedule(response.body()!!)
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
    }

    private fun saveSchedule(schedule: Schedule) {
        val filesDir: File = requireContext().filesDir
        val wichSchedule: String = if (user!!.isTeacher)
            "TeacherSchedule"
        else
            "Schedule"

        val setting = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        if (user!!.isTeacher)
            setting.edit().putBoolean("TeacherDefault", true).apply()
        else
            setting.edit().putBoolean("TeacherDefault", false).apply()

        val schedStream = File(filesDir, wichSchedule)
        val fos = FileOutputStream(schedStream)
        val selGroupSchel = schedule.toJson()
        fos.write(selGroupSchel.toByteArray())
        Toast.makeText(context, "Расписание успешно изменено!", Toast.LENGTH_SHORT)
            .show()
    }

    private fun initData(userPath: File) {
        try {
            val fis = FileInputStream(userPath)
            val userJson = String(fis.readBytes())
            user = Json.parse(UserInfo.serializer(), userJson)
            Picasso.get().load(user!!.avatar).fit().placeholder(R.drawable.placeholder_user)
                .centerCrop().into(userPhotocard)
            userName.text = user?.username
            if (user?.isTeacher!!)
                userRank.text = user?.position
            else
                userRank.text = user?.group!!.name
        } catch (ex: Exception) {
            val filesDir: File = requireContext().filesDir
            File(filesDir, "User.dat").delete()
            findNavController().navigate(R.id.noLogin)
        }
    }

    private fun downloadDataToGridView() {
        NOTIFICATION_ID = -1
        TASK_ID = -1
        MY_FAC_ID = -1
        LOGOUT_ID = -1
        CHECKPOINTS_ID = -1
        val data: ArrayList<HashMap<String, Any>> = ArrayList(6)

        val hs0: HashMap<String, Any> = HashMap()
        hs0["pic"] = R.drawable.notification
        hs0["name"] = "Уведомления"
        data.add(hs0)
        NOTIFICATION_ID = 0

        val hs2: HashMap<String, Any> = HashMap()
        hs2["pic"] = R.drawable.faculty
        hs2["name"] = "Мой факультет"
        data.add(hs2)
        MY_FAC_ID = 1

        val hs3: HashMap<String, Any> = HashMap()
        hs3["pic"] = R.drawable.hometask
        hs3["name"] = "Задания"
        data.add(hs3)
        TASK_ID = 2

        if (!user?.isTeacher!!) {
            val hs4: HashMap<String, Any> = HashMap()
            hs4["pic"] = R.drawable.control_points
            hs4["name"] = "Успеваемость"
            data.add(hs4)
            CHECKPOINTS_ID = 3
        }

        val hs5: HashMap<String, Any> = HashMap()
        hs5["pic"] = R.drawable.logout
        hs5["name"] = "Выйти"
        data.add(hs5)
        LOGOUT_ID = if (CHECKPOINTS_ID != -1)
            CHECKPOINTS_ID + 1
        else
            TASK_ID + 1


        val from: Array<String> = arrayOf("pic", "name")
        val to = arrayOf(R.id.picture, R.id.text)

        val simplyAdapter =
            SimpleAdapter(context, data, R.layout.profile_items_view, from, to.toIntArray())
        menu.adapter = simplyAdapter
    }
}