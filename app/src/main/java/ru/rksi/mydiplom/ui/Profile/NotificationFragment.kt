package ru.rksi.mydiplom.ui.profile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.clans.fab.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.rksi.mydiplom.APIClasses.*
import ru.rksi.mydiplom.Adapters.NotificationsAdapter

import ru.rksi.mydiplom.R
import ru.rksi.mydiplom.TokenManager
import java.io.File
import java.io.FileInputStream

class NotificationFragment : Fragment() {

    private var user: UserInfo? = null
    private lateinit var notifications: RecyclerView
    val retrofit = ApiClient.Instance
    val api = retrofit.create(RsueApi::class.java)
    private lateinit var circle: ProgressBar
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_notification, container, false)

        notifications = root.findViewById(R.id.recyclerView)
        notifications.layoutManager = LinearLayoutManager(requireContext())
        circle = root.findViewById(R.id.loadCircle)
        fab = root.findViewById(R.id.fab)

        val fis = FileInputStream(File(requireContext().filesDir, "User.dat"))
        val userJson = String(fis.readBytes())
        user = Json.parse(UserInfo.serializer(), userJson)
        if (user!!.isTeacher)
        {
            loadNotificationsStudent()
        }
        else
        {
            fab.visibility = View.GONE
            loadNotificationsStudent()
        }

        fab.setOnClickListener {
            findNavController().navigate(R.id.createNotification)
        }


        return root
    }

    private fun loadNotificationsStudent() {
        if (TokenManager.IsTokenValid())
            getNotifications()
        else {
            val call = api.updateToken(RefreshTokenRequest(TokenManager.RefreshToken))
            call.enqueue(
                object : Callback<TokenResponse> {
                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        Toast.makeText(
                            context,
                            "Что-то пошло не так! Проверье ваше подключение к сети.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    override fun onResponse(
                        call: Call<TokenResponse>,
                        response: Response<TokenResponse>
                    ) {
                        when {
                            response.code() == 200 -> {
                                TokenManager.saveNewDatas(response.body()!!)
                                getNotifications()
                            }
                            response.code() == 400 -> {
                                File(requireContext().filesDir, "User.dat").delete()
                            }
                            else -> {
                                Toast.makeText(
                                    context,
                                    "Что-то пошло не так! ${response.code()} ${response.message()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            )
        }
    }

    private fun getNotifications() {
        val call = api.getNotification("Bearer " + TokenManager.AccessToken)

        call.enqueue(object : Callback<ArrayList<NotificationResponse>> {
            override fun onFailure(call: Call<ArrayList<NotificationResponse>>, t: Throwable) {
                Toast.makeText(
                    context,
                    "Что-то пошло не так! Проверье ваше подключение к сети.",
                    Toast.LENGTH_SHORT
                )
                    .show()
                findNavController().popBackStack()
            }

            override fun onResponse(
                call: Call<ArrayList<NotificationResponse>>,
                response: Response<ArrayList<NotificationResponse>>
            ) {
                if (response.code() == 200) {
                    disableProgressBar()
                    val adapter = NotificationsAdapter()
                    adapter.setItems(response.body()!!)
                    notifications.adapter = adapter
                } else if (response.code() == 404) {
                    Toast.makeText(requireContext(), "Уведомлений для Вас нет!", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(
                        context,
                        "Что-то пошло не так! ${response.code()} ${response.message()}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    findNavController().popBackStack()
                }
            }
        })
    }

    private fun disableProgressBar() {
        loadCircle.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        MenuInflater(context).inflate(R.menu.notification_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.title) {
            "Настройки" -> requireView().findNavController().navigate(R.id.notificationSettingFragment)
        }
        return super.onOptionsItemSelected(item)
    }

}
