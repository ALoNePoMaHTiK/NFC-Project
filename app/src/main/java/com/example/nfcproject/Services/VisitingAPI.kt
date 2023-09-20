package com.example.nfcproject.Services

import com.example.nfcproject.model.APIModels.VisitingAPI.Visiting
import com.example.nfcproject.model.APIModels.DBAPI.Lesson
import retrofit2.Call
import retrofit2.http.*

interface VisitingAPI {
    @Headers(
        "human-id:00000000-0000-0000-0000-000000000001",
        "User-Agent:insomnia/2023.5.8")
    @GET("/debug-login")
    fun Auth(): Call<Void>

    @GET("/rtu_tc.rtu_attend.app.UserService/GetMeInfo")
    fun GetMeInfo():Call<String>

}