package ru.rksi.mydiplom.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.rksi.mydiplom.APIClasses.*
import ru.rksi.mydiplom.R
import ru.rksi.mydiplom.TokenManager
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception

class NotificationSettingsFragment : Fragment() {

    private lateinit var tvBeforeEnd: TextView
    private lateinit var tvBeforeStart: TextView
    private lateinit var sbBeforeEnd: SeekBar
    private lateinit var sbBeforeStart: SeekBar
    private lateinit var buttonApply: Button
    private lateinit var swNewTasks: Switch
    private lateinit var swNewNews: Switch
    private lateinit var swNewMarks: Switch
    private lateinit var pBar: ProgressBar

    private var allFilters = NotificationFilters(ArrayList())
    val retrofit = ApiClient.Instance
    val api: RsueApi = retrofit.create(RsueApi::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_notification_settings, container, false)
        tvBeforeEnd = root.findViewById(R.id.tvBeforeEnd)
        tvBeforeStart = root.findViewById(R.id.tvBeforeStart)
        sbBeforeEnd = root.findViewById(R.id.seekBarBeforeEnd)
        sbBeforeStart = root.findViewById(R.id.seekBarBeforeStart)
        buttonApply = root.findViewById(R.id.buttonApplyChanges)
        swNewTasks = root.findViewById(R.id.switchNewTasks)
        swNewNews = root.findViewById(R.id.switchNewNews)
        swNewMarks = root.findViewById(R.id.switchNewMarks)
        pBar = root.findViewById(R.id.loadCircle)

        enableProgressBar()

        val filesDir: File = requireContext().filesDir
        val existSettings = File(filesDir, "NotificationSettings")
        if (existSettings.exists())
            initValues(existSettings)
        else {
            initTVBeforeEnd(0)
            initTVBeforeStart(0)
        }
        loadSettingsFromApi()
        initEvents()

        return root
    }

    private fun loadSettingsFromApi()
    {
        if (TokenManager.IsTokenValid()) {
            getFilters()
        } else {
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
                                getFilters()
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

    private fun initEvents() {
        buttonApply.setOnClickListener { saveChanges() }
        sbBeforeEnd.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                initTVBeforeEnd(i)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        sbBeforeStart.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                initTVBeforeStart(i)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun initTVBeforeEnd(i: Int) {
        val minutes: String
        when (i) {
            0 -> {
                tvBeforeEnd.text = "Не уведомлять до конца пары"
                return
            }
            in 2..4 -> minutes = "минуты"
            1 -> minutes = "минуту"
            else -> minutes = "минут"
        }
        tvBeforeEnd.text = "Уведомлять до конца пары за $i $minutes"
    }

    private fun initTVBeforeStart(i: Int) {
        val minutes: String
        when (i) {
            0 -> {
                tvBeforeStart.text = "Не уведомлять до начала пары"
                return
            }
            in 2..4 -> minutes = "минуты"
            1 -> minutes = "минуту"
            else -> minutes = "минут"
        }
        tvBeforeStart.text = "Уведомлять до начала пары за $i $minutes"
    }

    private fun saveChanges() {
        val settings = NotificationSettings()
        settings.BeforeEnd = sbBeforeEnd.progress
        settings.BeforeStart = sbBeforeEnd.progress

        val filesDir: File = requireContext().filesDir
        val notStream = File(filesDir, "NotificationSettings")
        val fos = FileOutputStream(notStream)
        val settingsStr = settings.toJson()
        fos.write(settingsStr.toByteArray())

        if (TokenManager.IsTokenValid()) {
            sendFilters()
        } else {
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
                                sendFilters()
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

    private fun initValues(existSettings: File) {
        val fis = FileInputStream(existSettings)
        val notSetString = String(fis.readBytes())
        var settings = NotificationSettings()
        try {
            settings = Json.parse(NotificationSettings.serializer(), notSetString)
        }
        catch (ex:Exception)
        {
            settings.BeforeEnd = 0
            settings.BeforeStart = 0
        }

        initTVBeforeStart(settings.BeforeStart)
        sbBeforeStart.progress = settings.BeforeStart
        initTVBeforeEnd(settings.BeforeEnd)
        sbBeforeEnd.progress = settings.BeforeEnd
    }

    private fun getFilters() {
        val call = api.getFilters("Bearer " + TokenManager.AccessToken)

        call.enqueue(object : Callback<NotificationFilters> {
            override fun onFailure(call: Call<NotificationFilters>, t: Throwable) {
                Toast.makeText(
                    context,
                    "Что-то пошло не так! Проверье ваше подключение к сети.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onResponse(
                call: Call<NotificationFilters>,
                response: Response<NotificationFilters>
            ) {
                if (response.code() == 200) {
                    initSwitchers(response.body()!!.filters)
                } else {
                    findNavController().popBackStack()
                    Toast.makeText(
                        requireContext(),
                        "Что-то пошло не так! ${response.code()} ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun sendFilters() {
        if (swNewMarks.isChecked)
            allFilters.filters.add("Performance")
        if (swNewNews.isChecked)
            allFilters.filters.add("News")
        if (swNewTasks.isChecked)
            allFilters.filters.add("Tasks")

        val call = api.sendFilters("Bearer " + TokenManager.AccessToken, allFilters)

        call.enqueue(object : Callback<NotificationFilters> {
            override fun onFailure(call: Call<NotificationFilters>, t: Throwable) {
                Toast.makeText(
                    context,
                    "Что-то пошло не так! Проверье ваше подключение к сети.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onResponse(
                call: Call<NotificationFilters>,
                response: Response<NotificationFilters>
            ) {
                if (response.code() == 204) {
                    Toast.makeText(
                        context,
                        "Настройки сохранены!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    findNavController().popBackStack()
                } else {
                    findNavController().popBackStack()
                    Toast.makeText(
                        requireContext(),
                        "Что-то пошло не так! ${response.code()} ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

    }

    private fun initSwitchers(filters: ArrayList<String>) {
        if (filters.contains("Performance"))
            swNewMarks.isChecked = true
        if (filters.contains("News"))
            swNewNews.isChecked = true
        if (filters.contains("Tasks"))
            swNewTasks.isChecked = true
        disableProgressBar()
    }

    private fun disableProgressBar() {
        swNewTasks.isEnabled = true
        swNewNews.isEnabled = true
        swNewMarks.isEnabled = true
        sbBeforeEnd.isEnabled = true
        sbBeforeStart.isEnabled = true
        pBar.visibility = View.GONE
        buttonApply.isEnabled = true
    }

    private fun enableProgressBar() {
        swNewTasks.isEnabled = false
        swNewNews.isEnabled = false
        swNewMarks.isEnabled = false
        sbBeforeEnd.isEnabled = false
        sbBeforeStart.isEnabled = false
        pBar.visibility = View.VISIBLE
        buttonApply.isEnabled = false
    }
}
