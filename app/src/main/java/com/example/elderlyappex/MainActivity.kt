package com.example.elderlyappex

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import retrofit2.*
import com.example.elderlyappex.network.APIS
import com.example.elderlyappex.network.DTO
import com.example.elderlyappex.network.User
import com.example.elderlyappex.network.login
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    val TAG: String = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val regionRetrofit = Retrofit.Builder()
            .baseUrl("http://14.52.69.42:3000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val regionServer = regionRetrofit.create(APIS::class.java)
        setContentView(R.layout.activity_login)

        edit_id_login.setText(App.prefs.myId)
        edit_pw_login.setText(App.prefs.myPw)

        // 로그인 버튼
        btnLogin.setOnClickListener {

            regionServer?.login(edit_id_login.text.toString(),edit_pw_login.text.toString())?.enqueue(object : Callback<Boolean> {
                override fun onResponse(
                    call: Call<Boolean>,
                    response: Response<Boolean>
                ) {
                    Log.d("log", response.toString())

                    if(response.body()!!) {
                        App.prefs.myId = edit_id_login.text.toString()
                        App.prefs.myPw = edit_pw_login.text.toString()

                        val intent = Intent(this@MainActivity, Home::class.java)
                        dialog("success", funcOk = { startActivity(intent); finish() })
                    }else{
                        dialog("fail", funcOk = {})
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Log.d("log", t.message.toString())
                    Log.d("log", "fail")
                }
            })
        }

        // 회원가입 버튼
        btn_register_login.setOnClickListener {

            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

    }

    // 로그인 성공/실패 시 다이얼로그를 띄워주는 메소드
    fun dialog(type: String, funcOk: () -> Unit){
        var dialog = AlertDialog.Builder(this)
        Log.d("log",type)
        if(type.equals("success")){
            dialog.setTitle("로그인 성공")
            dialog.setMessage("로그인 성공!")
        }
        else if(type.equals("fail")){
            dialog.setTitle("로그인 실패")
            dialog.setMessage("아이디와 비밀번호를 확인해주세요")
        }

        var dialog_listener = object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                funcOk()
            }
        }

        dialog.setPositiveButton("확인",dialog_listener)
        dialog.show()
    }
}