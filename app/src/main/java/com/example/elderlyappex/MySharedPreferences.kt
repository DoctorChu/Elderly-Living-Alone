package com.example.elderlyappex

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences(context: Context) {

    private val PREFS_FILENAME = "prefs"
    private val PREF_KEY_MY_ID = "myId"
    private val PREF_KEY_MY_PW = "myPw"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME,0)

    var myId: String?
        get() = prefs.getString(PREF_KEY_MY_ID, "")
        set(value) = prefs.edit().putString(PREF_KEY_MY_ID, value).apply()

    var myPw: String?
        get() = prefs.getString(PREF_KEY_MY_PW, "")
        set(value) = prefs.edit().putString(PREF_KEY_MY_PW, value).apply()
}