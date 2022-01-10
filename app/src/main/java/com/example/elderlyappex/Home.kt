package com.example.elderlyappex

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.elderlyappex.network.APIS
import kotlinx.android.synthetic.main.activity_home.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Home : AppCompatActivity() {

    val TAG: String = "Home"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val regionRetrofit = Retrofit.Builder()
            .baseUrl("http://14.52.69.42:3000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val regionServer = regionRetrofit.create(APIS::class.java)

        val string: String = getString(R.string.home_wait)
        txt_home_sign.setText(string)

    }
}