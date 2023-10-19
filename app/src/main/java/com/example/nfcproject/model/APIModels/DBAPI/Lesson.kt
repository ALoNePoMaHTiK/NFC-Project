package com.example.nfcproject.model.APIModels.DBAPI

import java.util.*

data class Lesson(
    val lessonId:String,
    val lessonName: String,
    val groupId:String,
    val roomId:Short,
    val startDateTime:String,
    val finishDateTime:String) {
    constructor() : this("", "", "", 0,"","")
}