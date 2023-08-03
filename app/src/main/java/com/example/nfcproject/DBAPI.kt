package com.example.nfcproject
import com.example.nfcproject.model.APIModels.AuthData
import retrofit2.Call
import retrofit2.http.*
interface DBAPI {
    @POST("Students/SignIn")
    fun singIn(@Body student: AuthData): Call<String>
}