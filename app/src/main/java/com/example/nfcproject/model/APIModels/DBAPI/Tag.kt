package com.example.nfcproject.model.APIModels.DBAPI

data class Tag(
    val tagId:String,
    val placementDateTime:String,
    val roomId:Short,
    val note:String,
    val isActive:Boolean) {}