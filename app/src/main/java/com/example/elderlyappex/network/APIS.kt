package com.example.elderlyappex.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.net.URI

interface APIS {
    @GET("/check")
    fun isReady(
        @Query("id") id: String
    ) : Call<Boolean>

    @GET("/start")
    fun start(
        @Query("id") id: String
    ) : Call<Boolean>

    @GET("/download")
    fun getRes(
        @Query("id") id: String
    ) : Call<ResponseBody>

    @GET("/login")
    fun login(
        @Query("id") id: String,
        @Query("pw") pw: String,
        @Query("token") token: String
    ) : Call<Boolean>

    @POST("/register")
    fun register(@Body info: User) : Call<String>

    @Multipart
    @POST("/upload")
    fun uploadCsv(
        @Part("id") id: String,
        @Part csvFile: MultipartBody.Part
    ) : Call<Boolean>
}