package ru.rksi.mydiplom.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TabHost
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import ru.rksi.mydiplom.APIClasses.*
import ru.rksi.mydiplom.Adapters.ExpandableFacultiesAdapter
import ru.rksi.mydiplom.Adapters.GroupmatesAdapter
import ru.rksi.mydiplom.R
import ru.rksi.mydiplom.TokenManager
import java.io.File
import java.io.FileInputStream

class MyFacultyFragment : Fragment() {
    private lateinit var tabHost: TabHost
    private lateinit var groupmatesRecycle: RecyclerView
    private lateinit var facRecycle: RecyclerView
    private lateinit var user: UserInfo
    private lateinit var retrofit: Retrofit
    private lateinit var api: RsueApi
    private var _root:View? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (_root != null) {
            return _root
        }
        else
        {
            _root = inflater.inflate(R.layout.fragment_my_faculty, container, false)
            retrofit = ApiClient.Instance
            api = retrofit.create(RsueApi::class.java)

            val fis = FileInputStream(File(requireContext().filesDir, "User.dat"))
            val userJson = String(fis.readBytes())
            user = Json.parse(UserInfo.serializer(), userJson)

            tabHost = _root!!.findViewById(R.id.tabHost)
            groupmatesRecycle = _root!!.findViewById(R.id.groupmates)
            facRecycle = _root!!.findViewById(R.id.faculties)
            groupmatesRecycle.layoutManager = LinearLayoutManager(requireContext())
            init()
        }

        return _root
    }

    private fun init() {
        tabHost.setup()

        if (!user.isTeacher) {
            var tabSpec = tabHost.newTabSpec("myGroupTab")
            tabSpec.setIndicator("Группа")
            tabSpec.setContent(R.id.groupTab)
            tabHost.addTab(tabSpec)

            tabSpec = tabHost.newTabSpec("myPrepsTab")
            tabSpec.setIndicator("Преподаватели")
            tabSpec.setContent(R.id.teacherTab)
            tabHost.addTab(tabSpec)
        } else {
            val tabSpec = tabHost.newTabSpec("myPrepsTab")
            tabSpec.setIndicator("Преподаватели")
            tabSpec.setContent(R.id.teacherTab)
            tabHost.addTab(tabSpec)
        }

        initGroup()
    }

    private fun getFACWithTeachers()
    {
        val call = api.getFacultiesWithTeachers(
            "Bearer " + TokenManager.AccessToken
        )

        call.enqueue(
            object : Callback<ArrayList<MyFacultyFacultiesResponse>> {
                override fun onFailure(
                    call: Call<ArrayList<MyFacultyFacultiesResponse>>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        context,
                        "Что-то пошло не так! Проверье ваше подключение к сети.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    //disableProgressBar()
                }

                override fun onResponse(
                    call: Call<ArrayList<MyFacultyFacultiesResponse>>,
                    response: Response<ArrayList<MyFacultyFacultiesResponse>>
                ) {
                    if (response.code() == 200) {
                        val adapter = ExpandableFacultiesAdapter(requireContext())
                        adapter.setItems(response.body()!!)
                        facRecycle.adapter = adapter
                    } else {
                        Toast.makeText(context, "Что-то пошло не так! ${response.code()} ${response.message()}", Toast.LENGTH_SHORT)
                            .show()
                        // disableProgressBar()
                    }
                }
            }
        )
    }

    private fun initTeachers() {
        if(TokenManager.IsTokenValid())
            getFACWithTeachers()
        else
        {
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
                                getFACWithTeachers()
                            }
                            response.code() == 400 -> {
                                File(requireContext().filesDir, "User.dat").delete()
                            }
                            else -> {
                                Toast.makeText(context,"Что-то пошло не так! ${response.code()} ${response.message()}",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            )
        }
    }

    private fun getGMates()
    {
        val call = api.getGroupMates(
            "Bearer " + TokenManager.AccessToken, user.group!!.groupId
        )
        call.enqueue(
            object : Callback<ArrayList<MyFacultyUserResponse>> {
                override fun onFailure(call: Call<ArrayList<MyFacultyUserResponse>>, t: Throwable) {
                    Toast.makeText(
                        context,
                        "Что-то пошло не так! Проверье ваше подключение к сети.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    //disableProgressBar()
                }

                override fun onResponse(
                    call: Call<ArrayList<MyFacultyUserResponse>>,
                    response: Response<ArrayList<MyFacultyUserResponse>>
                ) {
                    if (response.code() == 200) {
                        val adapter = GroupmatesAdapter()
                        groupmatesRecycle.adapter = adapter
                        adapter.setItems(response.body()!!)
                        initTeachers()
                    } else {
                        Toast.makeText(context, "Что-то пошло не так! ${response.code()} ${response.message()}", Toast.LENGTH_SHORT)
                            .show()
                        // disableProgressBar()
                    }
                }
            }
        )
    }

    private fun initGroup() {
        if (user.isTeacher)
        {
            initTeachers()
            return
        }
        if(TokenManager.IsTokenValid())
            getGMates()
        else
        {
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
                                getGMates()
                            }
                            response.code() == 400 -> {
                                File(requireContext().filesDir, "User.dat").delete()
                            }
                            else -> {
                                Toast.makeText(context,"Что-то пошло не так! ${response.code()} ${response.message()}",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            )
        }
    }

    override fun onDestroyView() {
        if (_root!!.parent != null) {
            (_root!!.parent as ViewGroup).removeView(_root)
        }
        super.onDestroyView()
    }

}
