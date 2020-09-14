package ru.rksi.mydiplom.Adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import ru.rksi.mydiplom.APIClasses.*
import ru.rksi.mydiplom.R
import ru.rksi.mydiplom.TokenManager

class GroupmatesAdapter(val isTeacher: Boolean = false) :
    RecyclerView.Adapter<GroupmatesAdapter.ViewHolder>() {
    private var mates: ArrayList<MyFacultyUserResponse> = ArrayList()
    private var retrofit: Retrofit = ApiClient.Instance
    private var api: RsueApi = retrofit.create(RsueApi::class.java)

    class ViewHolder(itemView: View, val isTeacher: Boolean) :
        RecyclerView.ViewHolder(itemView) {

        private val userphoto: RoundedImageView = itemView.findViewById(R.id.user_photo)
        private val number: TextView = itemView.findViewById(R.id.num)
        var teacherId = -1
        val username: TextView = itemView.findViewById(R.id.user_username)
        var photoUrl = ""
        fun bind(mate: MyFacultyUserResponse) {
            Picasso.get().load(mate.avatar).fit().centerCrop().placeholder(R.drawable.placeholder_user).into(userphoto)
            username.text = mate.name
            if (isTeacher) {
                teacherId = mate.teacherId
                photoUrl = mate.avatar
            } else {
                number.text = (adapterPosition + 1).toString()
            }
        }
    }

    fun setItems(mates: ArrayList<MyFacultyUserResponse>) {
        clearItems()
        this.mates.addAll(mates)
        notifyDataSetChanged()
    }

    fun clearItems() {
        mates.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.myfac_user_item, parent, false)
        return ViewHolder(view, isTeacher)
    }

    override fun getItemCount(): Int {
        return mates.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mates[position])
        if (!isTeacher)
            return
        holder.itemView.setOnClickListener {
            val id = holder.teacherId
            val call = api.getAllLessons("Bearer " + TokenManager.AccessToken, id)
            call.enqueue(
                object : Callback<ArrayList<Lesson>> {
                    override fun onFailure(call: Call<ArrayList<Lesson>>, t: Throwable) {
//                        Toast.makeText(context, "Что-то пошло не так! Проверье ваше подключение к сети.", Toast.LENGTH_SHORT)
//                            .show()
                        //disableProgressBar()
                    }

                    override fun onResponse(
                        call: Call<ArrayList<Lesson>>,
                        response: Response<ArrayList<Lesson>>
                    ) {
                        if (response.code() == 200) {
                            val bundle = Bundle()

                            bundle.putInt("teacherId", holder.teacherId)
                            bundle.putString("avatar", holder.photoUrl)
                            bundle.putString("name", holder.username.text.toString())
                            val lessons = ArrayList<String>()
                            response.body()!!.forEach {
                                lessons.add(it.title)
                            }
                            bundle.putStringArrayList("lessons", lessons)

                            holder.itemView.findNavController().navigate(R.id.userProfile, bundle)

                        } else {
//                            Toast.makeText(context, "Что-то пошло не так!", Toast.LENGTH_SHORT)
//                                .show()
                            //disableProgressBar()
                        }
                    }
                }
            )
        }

    }
}