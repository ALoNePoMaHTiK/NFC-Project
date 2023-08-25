package com.example.nfcproject.Services
import com.example.nfcproject.model.APIModels.DBAPI.AuthData
import com.example.nfcproject.model.APIModels.DBAPI.Checkout
import com.example.nfcproject.model.APIModels.DBAPI.Student
import com.example.nfcproject.model.APIModels.DBAPI.Tag
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
interface DBAPI {
    @Headers("X-API-KEY:ed3ffe03-5501-476b-9791-e82d54027cb3")
    @POST("Students/Auth")
    fun Auth(@Body student: AuthData): Call<Student>

    @Headers("X-API-KEY:ed3ffe03-5501-476b-9791-e82d54027cb3")
    @GET("Students/{studentId}")
    fun GetStudentById(@Path(value = "studentId", encoded = false) studentId: String): Call<Student>

    @Headers("X-API-KEY:ed3ffe03-5501-476b-9791-e82d54027cb3")
    @POST("Students/CheckAuth")
    suspend fun CheckAuth(@Body student: AuthData): Response<Student>

    @Headers("X-API-KEY:ed3ffe03-5501-476b-9791-e82d54027cb3")
    @GET("Tags/{tagId}")
    suspend fun GetNoteByTagId(@Path(value = "tagId", encoded = false) tagId: String): Response<String>

    @Headers("X-API-KEY:ed3ffe03-5501-476b-9791-e82d54027cb3")
    @PUT("Tags")
    fun updateTag(@Body tag: Tag): Call<ResponseBody>

    @Headers("X-API-KEY:ed3ffe03-5501-476b-9791-e82d54027cb3")
    @GET("Tags/{tagId}")
    fun GetTagById(@Path(value = "tagId", encoded = false) tagId: String): Call<Tag>

    @Headers("X-API-KEY:ed3ffe03-5501-476b-9791-e82d54027cb3")
    @GET("Tags/{tagId}")
    suspend fun GetTagByIdSuspend(@Path(value = "tagId", encoded = false) tagId: String) : Response<Tag>

    @Headers("X-API-KEY:ed3ffe03-5501-476b-9791-e82d54027cb3")
    @GET("Tags/{tagId}/{note}")
    suspend fun GetTagByIdAndNote(@Path(value = "tagId", encoded = false) tagId: String,
                                  @Path(value = "note", encoded = false) note: String) : Response<Tag>

    @Headers("X-API-KEY:ed3ffe03-5501-476b-9791-e82d54027cb3")
    @POST("Checkouts")
    fun CreateCheckout(@Body checkout: Checkout): Call<Checkout>
}