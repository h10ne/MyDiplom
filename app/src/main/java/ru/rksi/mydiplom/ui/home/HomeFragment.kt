package ru.rksi.mydiplom.ui.home

import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import ru.rksi.mydiplom.R
import io.karn.notify.Notify
import ru.rksi.mydiplom.MainActivity


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var btn: Button = view.findViewById(R.id.btn)

        btn.setOnClickListener { btnClick() }
    }

    fun btnClick() {
        Notify
            .with(context!!)
            .content {
                text = "Text"
                title = "Title"
                largeIcon =
                    BitmapFactory.decodeResource(context!!.resources, R.mipmap.ic_launcher)
            }
            .meta {
                clickIntent = PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, MainActivity::class.java),
                    0)
            }
            .header {
                icon = R.mipmap.ic_launcher
                //color = Color.parseColor("#0E1060")
            }
            .show()

        val notify = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val r = RingtoneManager.getRingtone(context!!, notify)
        r.play()

    }
}