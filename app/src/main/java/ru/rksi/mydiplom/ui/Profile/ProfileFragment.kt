package ru.rksi.mydiplom.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.SimpleAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import ru.rksi.mydiplom.R

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addItemsToGridView()

    }

    private fun addItemsToGridView() {
        var data: ArrayList<HashMap<String, Any>> = ArrayList(6)

        var hs0: HashMap<String, Any> = HashMap()
        hs0["pic"] = R.drawable.notification
        hs0["name"] = "Уведомления"

        var hs1: HashMap<String, Any> = HashMap()
        hs1["pic"] = R.drawable.portfolio
        hs1["name"] = "Портфолио"

        var hs2: HashMap<String, Any> = HashMap()
        hs2["pic"] = R.drawable.faculty
        hs2["name"] = "Мой факультет"

        var hs4: HashMap<String, Any> = HashMap()
        hs4["pic"] = R.drawable.control_points
        hs4["name"] = "Контрольные точки"

        var hs3: HashMap<String, Any> = HashMap()
        hs3["pic"] = R.drawable.hometask
        hs3["name"] = "Задания"

        var hs6: HashMap<String, Any> = HashMap()
        hs6["pic"] = R.drawable.logout
        hs6["name"] = "Выйти"

        var from: Array<String> = arrayOf("pic", "name")
        var to = arrayOf(R.id.picture, R.id.text)
        data.add(hs0)
        data.add(hs1)
        data.add(hs2)
        data.add(hs3)
        data.add(hs4)
        data.add(hs6)

        var simplyAdapter =
            SimpleAdapter(context, data, R.layout.profile_items_view, from, to.toIntArray())
        var view: GridView = view!!.findViewById(R.id.gridViewMenuItems)
        view.adapter = simplyAdapter
    }
}