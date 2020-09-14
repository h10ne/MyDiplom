package ru.rksi.mydiplom.ui.profile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.dueeeke.tablayout.SlidingTabLayout
import com.google.android.material.tabs.TabLayout
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.rksi.mydiplom.APIClasses.*
import ru.rksi.mydiplom.Adapters.MarksPagerAdapter
import ru.rksi.mydiplom.R
import ru.rksi.mydiplom.TokenManager
import java.io.File
import java.io.FileInputStream
import java.lang.Exception


class MarksFragment : Fragment() {
    private lateinit var marks:RecyclerView
    private lateinit var adapter:MarksPagerAdapter
    private lateinit var progress:ProgressBar
    private lateinit var pager:ViewPager
    private lateinit var tabLayout:SlidingTabLayout
    val retrofit = ApiClient.Instance
    val api: RsueApi = retrofit.create(RsueApi::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_marks, container, false)

        marks = root.findViewById(R.id.marksRecycle)
        progress = root.findViewById(R.id.progress_circular)

        marks.layoutManager = LinearLayoutManager(this.requireContext())
        adapter = MarksPagerAdapter(childFragmentManager)
        //val adapter =
        //marks.adapter = adapter

        pager = root.findViewById(R.id.pager)
        tabLayout = root.findViewById(R.id.tabs)

        initMarks()

        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeButtonEnabled(true)

        return root
    }

    private fun initMarks()
    {

        val filesDir: File = requireContext().filesDir
        val userPath = File(filesDir, "User.dat")
        val fis = FileInputStream(userPath)
        val userJson = String(fis.readBytes())
        val user: UserInfo = Json.parse(UserInfo.serializer(), userJson)

        if(TokenManager.IsTokenValid())
            getMarks()
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
                                getMarks()
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

    private fun getMarks()
    {
        val call = api.getMarks("Bearer ${TokenManager.AccessToken}")
        call.enqueue(
            object : Callback<ArrayList<Performance>> {
                override fun onFailure(call: Call<ArrayList<Performance>>, t: Throwable) {
                    Toast.makeText(context, "Что-то пошло не так! Проверье ваше подключение к сети.", Toast.LENGTH_SHORT)
                        .show()
                    progress.visibility = View.GONE
                }

                override fun onResponse(call: Call<ArrayList<Performance>>, response: Response<ArrayList<Performance>>) {
                    try {
                        val performances:ArrayList<Performance> = response.body()!!
                        performances.reverse()

                        var semesterNumbers = 0
                        repeat(performances.size) {
                            semesterNumbers++
                        }

                        performances.forEach{
                            val resFrag = OneResView(it)
                            adapter.addFragment(resFrag, "Семестр $semesterNumbers")
                            semesterNumbers--
                        }

                        pager.adapter = adapter
                        tabLayout.setViewPager(pager)
                        //tabLayout.setupWithViewPager(pager)
                        progress.visibility = View.GONE

                    }
                    catch (ex: Exception)
                    {
                        Toast.makeText(context, "Что-то пошло не так!", Toast.LENGTH_SHORT)
                            .show()
                        progress.visibility = View.GONE
                    }
                }
            }
        )
    }
}
