package ru.rksi.mydiplom.ui.news

import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.rksi.mydiplom.R
import io.karn.notify.Notify
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.rksi.mydiplom.APIClasses.ApiClient
import ru.rksi.mydiplom.APIClasses.News
import ru.rksi.mydiplom.APIClasses.RsueApi
import ru.rksi.mydiplom.Adapters.NewsAdapter
import ru.rksi.mydiplom.MainActivity


class HomeFragment : Fragment() {

    private lateinit var newsRecyclerView: RecyclerView
    private val client = ApiClient.Instance
    private lateinit var progressBar: ProgressBar
    private val rsueApi = client.create(RsueApi::class.java)
    private lateinit var adapter: NewsAdapter
    private var offset = 0
    private var isEnd = false
    private var canSend = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        newsRecyclerView = root.findViewById(R.id.newsView)
        adapter = NewsAdapter()
        newsRecyclerView.layoutManager = LinearLayoutManager(this.context)
        newsRecyclerView.adapter = adapter
        progressBar = root.findViewById(R.id.loadCircle)
        enableProgressBar()
        newsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1)) {
                    if(canSend)
                    {
                        canSend = false
                        loadApi(10)
                    }
                }
            }
        })
        return root
    }

    private fun disableProgressBar()
    {
        progressBar.visibility = View.GONE
        newsRecyclerView.isEnabled = true
    }

    private fun enableProgressBar()
    {
        progressBar.visibility = View.VISIBLE
        newsRecyclerView.isEnabled = false
    }

    private fun loadApi(count: Int) {
        if (isEnd)
            return
        val call: Call<ArrayList<News>> = rsueApi.getNews(offset, count)
        call.enqueue(
            object : Callback<ArrayList<News>> {
                override fun onResponse(
                    call: Call<ArrayList<News>>,
                    response: Response<ArrayList<News>>
                ) {
                    if (response.body() == null) {
                        isEnd = true
                    }
                    else
                    {
                        offset += count
                        loadNews(response.body()!!)
                        canSend = true
                        disableProgressBar()
                    }
                }

                override fun onFailure(call: Call<ArrayList<News>>, t: Throwable) {
                    Toast.makeText(context, "Что-то пошло не так! Проверье ваше подключение к сети.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )

    }

    private fun loadNews(news: ArrayList<News>) {
        adapter.addItems(news)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btn: Button = view.findViewById(R.id.btn)

        btn.setOnClickListener { btnClick() }
    }

    fun btnClick() {
        Notify
            .with(requireContext())
            .content {
                text = "Text"
                title = "Title"
                largeIcon =
                    BitmapFactory.decodeResource(requireContext().resources, R.mipmap.ic_launcher)
            }
            .meta {
                clickIntent = PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, MainActivity::class.java),
                    0
                )
            }
            .header {
                icon = R.mipmap.ic_launcher
                //color = Color.parseColor("#0E1060")
            }
            .show()

        val notify = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val r = RingtoneManager.getRingtone(context, notify)
        r.play()

    }

    override fun onResume() {
        adapter = NewsAdapter()
        newsRecyclerView.layoutManager = LinearLayoutManager(this.context)
        newsRecyclerView.adapter = adapter
        offset = 0
        isEnd = false
        loadApi(10)
        super.onResume()
    }
}