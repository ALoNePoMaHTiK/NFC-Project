package com.example.nfcproject.model.APIModels.DBAPI

data class User(
    val userId:Int,
    val name:String) {
    constructor() : this(1,"")
}