package ru.rksi.mydiplom.APIClasses

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ApiClient {
    private var inc: Retrofit? = null

    var API: String = "rsue.online/"

    val Instance: Retrofit
        get() {
            if (inc == null) {
                val okHttpClient = OkHttpClient().newBuilder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build()
                inc = Retrofit.Builder()
                    .baseUrl("https://$API")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            }
            return inc!!
        }


    fun setApi(newApi: String) {
        API = newApi
        inc = Retrofit.Builder()
            .baseUrl("http://$API")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }
}