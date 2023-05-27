package com.example.nfcproject

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Body

interface VisitingAPI {
    @Headers("X-API-Token:5888EgsL2J4EYuDByQ2V63RFMF4z9r3H")
    @POST("{StudentId}/student_id")
    fun setVisitingByStudentId(@Path(value = "StudentId", encoded = false)StudentId:String, @Body visiting:Visiting): Call<String>
}