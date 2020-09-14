package ru.rksi.mydiplom.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import ru.rksi.mydiplom.APIClasses.NotificationResponse
import ru.rksi.mydiplom.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotificationsAdapter : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {
    private var notifications: ArrayList<NotificationResponse> = ArrayList()

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val avatar: RoundedImageView = itemView.findViewById(R.id.avatar)
        private val username: TextView = itemView.findViewById(R.id.username)
        private val theme: TextView = itemView.findViewById(R.id.theme)
        private val message: TextView = itemView.findViewById(R.id.message)
        private val date: TextView = itemView.findViewById(R.id.date)

        fun bind(notification: NotificationResponse) {

            if(notification.group == null)
            {
                Picasso.get().load(notification.teacher.avatar).fit().centerCrop().placeholder(R.drawable.placeholder_user).into(avatar)
                username.text = notification.teacher.name
            } else
            {
                username.text = "Для группы ${notification.group.name}"
                username.textSize = 22F
                avatar.visibility = View.GONE
            }

            theme.text = notification.title
            message.text = notification.text
            if(notification.date!=null)
            {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = notification.date.toLong() * 1000
                date.text = SimpleDateFormat("dd MMMM yyyy").format(calendar.timeInMillis)
            }
        }
    }

    fun setItems(notifications: ArrayList<NotificationResponse>) {
        clearItems()
        this.notifications.addAll(notifications)
        notifyDataSetChanged()
    }

    fun clearItems() {
        notifications.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notifications[position])
    }
}