package ru.rksi.mydiplom

import android.content.Context
import android.widget.Toast
import io.karn.notify.Notify
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Route
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import ru.rksi.mydiplom.APIClasses.ApiClient
import ru.rksi.mydiplom.APIClasses.RefreshTokenRequest
import ru.rksi.mydiplom.APIClasses.RsueApi
import ru.rksi.mydiplom.APIClasses.TokenResponse
import java.io.File
import kotlin.concurrent.thread


class TokenManager {
    companion object TokenManager {
        private var retrofit: Retrofit = ApiClient.Instance
        private var api: RsueApi = retrofit.create(RsueApi::class.java)
        var LeaveTime: Long = 0
        lateinit var context: Context
        private var isUpdating = false

        fun saveNewDatas(resp:TokenResponse)
        {
            AccessToken = resp.accessToken
            RefreshToken = resp.refreshToken
            LeaveTime = resp.expiresIn
        }

        fun IsTokenValid():Boolean
        {
            val tsLong = System.currentTimeMillis() / 1000
            val ts = tsLong.toString()
            val lt = LeaveTime// - (3 * 60 * 60)
            if ((lt <= ts.toInt() - 30) && !isUpdating) {
                return false
            }

            return true
        }

        var AccessToken = String()
            get() {
                val tsLong = System.currentTimeMillis() / 1000
                val ts = tsLong.toString()

                if (LeaveTime >= ts.toInt() - 30) {
                    return context.getSharedPreferences("token", Context.MODE_PRIVATE)
                        .getString("token", "")!!
                }
                return context.getSharedPreferences("token", Context.MODE_PRIVATE)
                    .getString("token", "")!!

            }
            set(value) {
                field = value
                context.getSharedPreferences("token", Context.MODE_PRIVATE)
                    .edit().putString("token", value)!!.apply()
            }

        var RefreshToken = String()
            get() {
                return context.getSharedPreferences("token", Context.MODE_PRIVATE)
                    .getString("refresh_token", field)!!
            }
            set(value) {
                field = value
                context.getSharedPreferences("token", Context.MODE_PRIVATE)
                    .edit().putString("refresh_token", value)!!.apply()
            }


    }

}