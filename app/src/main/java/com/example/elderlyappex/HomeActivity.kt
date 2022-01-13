package com.example.elderlyappex

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.*
import com.example.elderlyappex.RealPathUtil.getRealPath
import com.example.elderlyappex.network.APIS
import kotlinx.android.synthetic.main.activity_home.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.lang.Exception



class HomeActivity : AppCompatActivity() {

    val TAG: String = "HomeActivity"
    private lateinit var getResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val regionRetrofit = Retrofit.Builder()
            .baseUrl("http://14.52.69.42:3000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val regionServer = regionRetrofit.create(APIS::class.java)

        regionServer.isReady(App.prefs.myId.toString()).enqueue( object : Callback<Boolean> {
            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.e("error??: ", t.message.toString())
            }

            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                Log.d("check:", response.toString())

                if(response.body()!!){
                    btn_download.setText("결과 다운로드")
                    txt_home_sign.setText(getString(R.string.home_ready))
                }else{
                    btn_download.setText("분석 시작")
                    txt_home_sign.setText(getString(R.string.home_wait))
                }
            }
        })

        checkPermission()

        getResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                var mString = it.data?.data
                Log.d(TAG,"OK? $mString")

                val realpath = getRealPath(this, mString!!)
                Log.d(TAG,"REAL $realpath")

                val file = File(realpath)
                // val requestFile = RequestBody.create("text/csv".toMediaTypeOrNull(), file.asRequestBody("text/csv".toMediaTypeOrNull()))
                val body = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody("text/csv".toMediaTypeOrNull()))
                val sendId = App.prefs.myId!!

                regionServer?.uploadCsv(sendId,body)?.enqueue( object : Callback<Boolean> {
                    override fun onFailure(
                        call: Call<Boolean>,
                        t: Throwable
                    ) {
                        Log.e("error??: ", t.message.toString())
                    }
                    override fun onResponse(
                        call: Call<Boolean>,
                        response: Response<Boolean>
                    ) {
                        Toast.makeText(applicationContext, "업로드 완료!!", Toast.LENGTH_SHORT).show()
                        Log.d(TAG,call.toString())

                        try {
                            // refresh
                            val intent = intent
                            finish() //현재 액티비티 종료 실시
                            overridePendingTransition(0, 0) //인텐트 애니메이션 없애기
                            startActivity(intent) //현재 액티비티 재실행 실시
                            overridePendingTransition(0, 0) //인텐트 애니메이션 없애기
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
            }
        }

        btn_upload.setOnClickListener{

            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            getResult.launch(intent)

        }

        btn_download.setOnClickListener{
            if(btn_download.getText() == "분석 시작"){
                regionServer.start(App.prefs.myId!!).enqueue( object : Callback<Boolean> {
                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                        Log.e("error??: ", t.message.toString())
                    }

                    override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                        Toast.makeText(applicationContext, "분석 시작!!", Toast.LENGTH_SHORT).show()
                        Log.d(TAG,call.toString())
                    }
                })
            }else{
                regionServer.getRes(App.prefs.myId!!).enqueue( object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("error??: ", t.message.toString())
                    }
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        val alert = AlertDialog.Builder(this@HomeActivity).create()
                        val factory = LayoutInflater.from(this@HomeActivity)
                        val customView = factory.inflate(R.layout.custom_layout, null)
                        val imgView = customView.findViewById<ImageView>(R.id.imageView3)

                        Glide.with(this@HomeActivity).load("http://14.52.69.42:3000/hoki.png").into(imgView);
                        alert.setView(customView)
                        alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
                            DialogInterface.OnClickListener { dialogInterface, i -> return@OnClickListener })
                        alert.show()
                        Log.d(TAG,"download")
                    }
                })
            }
        }

        btn_refresh.setOnClickListener{
            try {
                // refresh
                val intent = intent
                finish() //현재 액티비티 종료 실시
                overridePendingTransition(0, 0) //인텐트 애니메이션 없애기
                startActivity(intent) //현재 액티비티 재실행 실시
                overridePendingTransition(0, 0) //인텐트 애니메이션 없애기
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun checkPermission(){
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
            showDialog(1)
        } else {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), // 1
                1001) // 2
        }
    }
}

