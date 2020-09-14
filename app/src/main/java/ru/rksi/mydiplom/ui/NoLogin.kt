package ru.rksi.mydiplom.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.iid.FirebaseInstanceId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import ru.rksi.mydiplom.APIClasses.*
import ru.rksi.mydiplom.R
import ru.rksi.mydiplom.TokenManager
import java.io.File
import java.io.FileOutputStream

class NoLogin : Fragment() {

    private lateinit var loginButton: Button
    private lateinit var login: TextView
    private lateinit var password: TextView
    private lateinit var retrofit: Retrofit
    private lateinit var api: RsueApi
    private lateinit var bar: ProgressBar
    private lateinit var pLine:View
    private lateinit var lLine:View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_no_login, container, false)
        loginButton = root.findViewById(R.id.loginButton)
        login = root.findViewById(R.id.loginView)
        password = root.findViewById(R.id.passwordView)
        bar = root.findViewById(R.id.progress_circular)
        pLine = root.findViewById(R.id.passwordLine)
        lLine = root.findViewById(R.id.loginLine)
        retrofit = ApiClient.Instance
        api = retrofit.create(RsueApi::class.java)

        loginButton.setOnClickListener { Login() }
        initEvents()


        return root
    }

    private fun initEvents()
    {
        login.setOnFocusChangeListener { view: View, b: Boolean ->
            if(b)
            {
                lLine.setBackgroundColor(resources.getColor(R.color.colorPrimaryLight))
            }
            else
            {
                lLine.setBackgroundColor(resources.getColor(R.color.lightGrey))
            }
        }
        password.setOnFocusChangeListener{ view: View, b: Boolean ->
            if(b)
            {
                pLine.setBackgroundColor(resources.getColor(R.color.colorPrimaryLight))
            }
            else
            {
                pLine.setBackgroundColor(resources.getColor(R.color.lightGrey))
            }
        }
    }

    private fun enableProgressBar() {
        loginButton.isEnabled = false
        login.isEnabled = false
        password.isEnabled = false
        bar.visibility = View.VISIBLE
    }

    private fun disableProgressBar() {
        loginButton.isEnabled = true
        login.isEnabled = true
        password.isEnabled = true
        bar.visibility = View.INVISIBLE
    }

    private fun Login() {
        loginButton.requestFocus()
        if (password.text.toString() == "" || login.text.toString() == "")
        {
            Toast.makeText(requireContext(), "Все поля должны быть заполнены!", Toast.LENGTH_LONG)
                .show()
            return
        }
        enableProgressBar()

        val call: Call<TokenResponse> =
            api.getToken(
                TokenBody(
                    login.text.toString(),
                    password.text.toString(),
                    FirebaseInstanceId.getInstance().token!!
                )
            )
        call.enqueue(
            object : Callback<TokenResponse> {
                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                    Toast.makeText(
                        context,
                        "Что-то пошло не так! Проверье ваше подключение к сети.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    disableProgressBar()
                }

                override fun onResponse(
                    call: Call<TokenResponse>,
                    response: Response<TokenResponse>
                ) {
                    if (response.code() == 200) {
                        getCurrentUserDat(response.body()!!)
                    } else {
                        Toast.makeText(
                            context,
                            "Что-то пошло не так! ${response.code()} ${response.message()}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        disableProgressBar()
                    }
                }
            }
        )
    }

    private fun getCurrentUserDat(tokenInfo: TokenResponse) {
        TokenManager.saveNewDatas(tokenInfo)

        val call: Call<ProfileResponse> = api.getUserinfo("Bearer ${tokenInfo.accessToken}")
        call.enqueue(
            object : Callback<ProfileResponse> {
                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Toast.makeText(
                        context,
                        "Что-то пошло не так! Проверье ваше подключение к сети.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    disableProgressBar()
                }

                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {
                    if (response.code() == 200) {
                        saveCurrentUserDat(response.body()!!)
                    } else {
                        Toast.makeText(
                            context,
                            "Что-то пошло не так! ${response.code()} ${response.message()}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        disableProgressBar()
                    }
                }
            }
        )
    }

    private fun saveCurrentUserDat(profile: ProfileResponse) {
        val isTeacher: Boolean = profile.group == null
        val id = if (isTeacher)
            profile.teacherId
        else
            profile.group.groupId
        val userInfo =
            UserInfo(id, profile.position, profile.name, profile.group, isTeacher, profile.avatar)

        val filesDir: File = requireContext().filesDir
        val userPath = File(filesDir, "User.dat")
        val fos = FileOutputStream(userPath)
        val userDat = userInfo.toJson()
        fos.write(userDat.toByteArray())
        disableProgressBar()
        val setting = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        setting.edit().putBoolean("WasOffer", false).apply()
        Toast.makeText(context, "Вход выполнен успешно!", Toast.LENGTH_SHORT)
            .show()
        findNavController().navigate(R.id.navigation_profile)
    }

}
