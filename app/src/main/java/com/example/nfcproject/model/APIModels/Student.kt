package com.example.nfcproject.model.APIModels

data class Student(
    val studentId:String,
    val email:String,
    val password:String,
    val userId:Int,
    val groupId:String,
    val isAccepted:Boolean,
    val isAcceptRequested:Boolean) {}