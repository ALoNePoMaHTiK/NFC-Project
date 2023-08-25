package com.example.nfcproject.Services

import com.example.nfcproject.model.APIModels.VisitingAPI.Visiting
import com.example.nfcproject.model.APIModels.DBAPI.Lesson
import retrofit2.Call
import retrofit2.http.*

interface VisitingAPI {
    @Headers("X-API-Token:5888EgsL2J4EYuDByQ2V63RFMF4z9r3H")
    @POST("visiting/{StudentId}/student_id")
    fun setVisitingByStudentId(@Path(value = "StudentId", encoded = false)StudentId:String, @Body visiting: Visiting): Call<String>

    @Headers("X-API-Token:5888EgsL2J4EYuDByQ2V63RFMF4z9r3H")
    @GET("job/НФЦГ-01-22/journal")
    fun getLesson(@Query("start_at") StartAt:String, @Query("finish_at") FinishAt:String, @Query("date") Date:String): Call<Lesson>
}