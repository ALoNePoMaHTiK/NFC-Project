package com.example.nfcproject.Services
import com.example.nfcproject.model.APIModels.DBAPI.AuthData
import com.example.nfcproject.model.APIModels.DBAPI.Checkout
import com.example.nfcproject.model.APIModels.DBAPI.Lesson
import com.example.nfcproject.model.APIModels.DBAPI.Student
import com.example.nfcproject.model.APIModels.DBAPI.Tag
import com.example.nfcproject.model.APIModels.DBAPI.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

const val API_KEY = "X-API-KEY:ed3ffe03-5501-476b-9791-e82d54027cb3"

interface DBAPI {
    @Headers(API_KEY)
    @POST("Students/Auth")
    fun Auth(@Body student: AuthData): Call<Student>

    @Headers(API_KEY)
    @POST("Students/Auth")
    suspend fun AuthSuspend(@Body student: AuthData): Response<Student>

    @Headers(API_KEY)
    @GET("Students/{studentId}")
    fun GetStudentById(@Path(value = "studentId", encoded = false) studentId: String): Call<Student>

    @Headers(API_KEY)
    @GET("Students/{studentId}")
    suspend fun GetStudentByIdSuspend(@Path(value = "studentId", encoded = false) studentId: String): Response<Student>

    @Headers(API_KEY)
    @POST("Students/CheckAuth")
    suspend fun CheckAuth(@Body student: AuthData): Response<Student>

    @Headers(API_KEY)
    @GET("Tags/{tagId}")
    suspend fun GetNoteByTagId(@Path(value = "tagId", encoded = false) tagId: String): Response<String>

    @Headers(API_KEY)
    @PUT("Tags")
    fun updateTag(@Body tag: Tag): Call<ResponseBody>

    @Headers(API_KEY)
    @GET("Tags/{tagId}")
    fun GetTagById(@Path(value = "tagId", encoded = false) tagId: String): Call<Tag>

    @Headers(API_KEY)
    @GET("Tags/{tagId}")
    suspend fun GetTagByIdSuspend(@Path(value = "tagId", encoded = false) tagId: String) : Response<Tag>

    @Headers(API_KEY)
    @GET("Tags/{tagId}/{note}")
    suspend fun GetTagByIdAndNote(@Path(value = "tagId", encoded = false) tagId: String,
                                  @Path(value = "note", encoded = false) note: String) : Response<Tag>

    @Headers(API_KEY)
    @POST("Checkouts")
    fun CreateCheckout(@Body checkout: Checkout): Call<Checkout>

    @Headers(API_KEY)
    @GET("Users/{userId}")
    suspend fun GetUserById(@Path (value = "userId", encoded = false) userId: Int): Response<User>

    //@Headers(API_KEY)
    @GET("Lessons/ByDateTime/{groupId}/{datetime}")
    suspend fun GetLesson(@Path (value = "groupId", encoded = false) groupId: String,@Path (value = "datetime", encoded = false) datetime: String): Response<Lesson>

    @GET("Lessons/ByDateTime/{groupId}/{datetime}")
    fun GetLessonSync(@Path (value = "groupId", encoded = false) groupId: String,@Path (value = "datetime", encoded = false) datetime: String): Call<Lesson>
}