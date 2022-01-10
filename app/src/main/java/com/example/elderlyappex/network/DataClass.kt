package com.example.elderlyappex.network

data class User(
    var id:String = "",
    var pw:String = "",
    var name:String = "",
    var birth:String = "",
    var phone:String = ""
)

data class DTO(var count: Int)

