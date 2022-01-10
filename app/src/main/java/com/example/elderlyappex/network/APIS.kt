package com.example.elderlyappex.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface APIS {
    @GET("/check")
    fun getInfo() : Call<List<DTO>>

    @GET("/user")
    fun getUser() : Call<List<User>>
}