package com.example.elderlyappex

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.elderlyappex.network.APIS
import com.example.elderlyappex.network.User
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterActivity : AppCompatActivity() {

    val TAG: String = "RegisterActivity"
    var isExistBlank = false
    var isPWSame = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val regionRetrofit = Retrofit.Builder()
            .baseUrl("http://14.52.69.42:3000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val regionServer = regionRetrofit.create(APIS::class.java)

        btn_register.setOnClickListener {

            Log.d(TAG, "회원가입 완료 버튼 클릭")

            val id = edit_id.text.toString()
            val pw = edit_pw.text.toString()
            val pwRe = edit_pw_re.text.toString()
            val name = edit_name.text.toString()
            val birth = edit_birth.text.toString()
            val phone = edit_phone.text.toString()

            // 기입 누락 할 경우
            if(id.isEmpty() || pw.isEmpty() || pwRe.isEmpty() || name.isEmpty() || birth.isEmpty() || phone.isEmpty()){
                isExistBlank = true
            }
            else{
                if(pw == pwRe){
                    isPWSame = true
                }
            }
            Log.d("pwsame?? ", isPWSame.toString())
            // data class
            val userInfo = User(id,pw,name,birth,phone)

            Log.d("log",userInfo.id)

            if(!isExistBlank && isPWSame){

                regionServer?.register(userInfo)?.enqueue( object: Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        Log.d("log", "getResponse")
                        if(response.body() == "1"){
                            // 성공 토스트
                            Toast.makeText(applicationContext, "회원가입 성공", Toast.LENGTH_SHORT).show()

                            // shared 저장
                            val sharedPreference = getSharedPreferences("file name", Context.MODE_PRIVATE)
                            val editor = sharedPreference.edit()
                            editor.putString("id", id)
                            editor.putString("pw", pw)
                            editor.putString("name", name)
                            editor.putString("birth", birth)
                            editor.putString("phone", phone)
                            editor.apply()

                            // login 화면으로 이동
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                        }
                        else if(response.body() == "2"){
                            dialog("duplicate")
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.d("log", t.message.toString())
                        Log.d("log", "fail")
                    }
                })
            }
            else{
                // issue 출력
                if(isExistBlank) {
                    dialog("blank")
                    isExistBlank = false
                }
                else if(!isPWSame){
                    dialog("diff")
                    isPWSame = false
                }
            }
        }
    }

    // dialog method
    fun dialog(type: String) {
        val dialog = AlertDialog.Builder(this)

        // 누락 된 경우
        if(type.equals("blank")){
            dialog.setTitle("회원가입 실패")
            dialog.setMessage("입력 칸을 모두 채워주세요!!")
        }
        else if (type.equals("diff")){
            dialog.setTitle("회원가입 실패")
            dialog.setMessage("입력된 두 비밀번호가 다릅니다!!")
        }
        else if (type.equals("duplicate")){
            dialog.setTitle("회원가입 실패")
            dialog.setMessage("중복된 아이디입니다!!")
        }

        val dialog_listener = object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int){
                when(which){
                    DialogInterface.BUTTON_POSITIVE -> Log.d(TAG, "다이얼로그")
                }
            }
        }

        dialog.setPositiveButton("확인", dialog_listener)
        dialog.show()
    }
}