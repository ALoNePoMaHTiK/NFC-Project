package com.example.nfcproject.model.APIModels.DBAPI

import java.util.Date

data class Student(
    val studentId:String,
    val email:String,
    val password:String,
    val userId:Int,
    val groupId:String,
    val isAccepted:Boolean,
    val isAcceptRequested:Boolean) {
    constructor() : this("","","",0,"",false,false)
}