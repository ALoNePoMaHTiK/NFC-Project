package com.example.nfcproject.model.APIModels.DBAPI

import java.util.*

data class Lesson(
    val createdAt:String,
    val date: Date,
    val disciplineId:String,
    val disciplineName: String,
    val disciplineType: Int,
    val finishAt:String,
    val id:String,
    val journalId:String,
    val startAt:String,
    val updatedAt:String) {
    constructor() : this("", Date(), "", "",0,"","","","","")
}