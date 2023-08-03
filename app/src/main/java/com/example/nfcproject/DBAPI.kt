package com.example.nfcproject
import com.example.nfcproject.model.APIModels.AuthData
import com.example.nfcproject.model.APIModels.Student
import retrofit2.Call
import retrofit2.http.*
interface DBAPI {
    @POST("Students/SignIn")
    fun singIn(@Body student: AuthData): Call<String>

    @Headers("X-API-KEY:ed3ffe03-5501-476b-9791-e82d54027cb3")
    @POST("Students/Auth")
    fun Auth(@Body student: AuthData): Call<Student>
}