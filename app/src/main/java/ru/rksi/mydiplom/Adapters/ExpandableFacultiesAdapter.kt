package ru.rksi.mydiplom.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import ru.rksi.mydiplom.APIClasses.MyFacultyFacultiesResponse
import ru.rksi.mydiplom.APIClasses.MyFacultyUserResponse
import ru.rksi.mydiplom.R

class ExpandableFacultiesAdapter(var context: Context) :
    RecyclerView.Adapter<ExpandableFacultiesAdapter.ViewHolder>() {
    private var mates: ArrayList<MyFacultyFacultiesResponse> = ArrayList()


    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private var header: LinearLayout = itemView.findViewById(R.id.headerLayout)
        private var name = header.findViewById<TextView>(R.id.facultyName)
        private var exLayout = itemView.findViewById<LinearLayout>(R.id.expandableLayout)
        private var arrow:ImageView = itemView.findViewById(R.id.arrow)
        var recycle: RecyclerView = exLayout.findViewById(R.id.teachersInFacultyRecycle)

        private var isExpanded = true
            set(value) {
                field = value
                if (value) {
                    arrow.rotation = 90F
                    exLayout.visibility = View.VISIBLE
                } else {
                    arrow.rotation = 0F
                    exLayout.visibility = View.GONE
                }
            }

        init {
            isExpanded = false
            header.setOnClickListener {
                isExpanded = !isExpanded
                recycle.adapter!!.notifyItemChanged(adapterPosition)

            }
        }


        fun bind(mate: MyFacultyFacultiesResponse) {
            val adapter = GroupmatesAdapter(true)
            adapter.setItems(mate.teachers)
            name.text = mate.faculty
            recycle.adapter = adapter
        }
    }

    fun setItems(mates: ArrayList<MyFacultyFacultiesResponse>) {
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
                .inflate(R.layout.item_expandable_faculty, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mates.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.recycle.layoutManager = LinearLayoutManager(context)
        holder.bind(mates[position])
    }
}