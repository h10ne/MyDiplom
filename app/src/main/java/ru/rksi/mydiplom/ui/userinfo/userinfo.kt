package ru.rksi.mydiplom.ui.userinfo

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.rksi.mydiplom.R


class userinfo : Fragment() {

    companion object {
        fun newInstance() = userinfo()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_userinfo, container, false)
    }

}
