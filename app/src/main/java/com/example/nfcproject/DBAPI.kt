package com.example.nfcproject
import com.example.nfcproject.model.APIModels.AuthData
import com.example.nfcproject.model.APIModels.Checkout
import com.example.nfcproject.model.APIModels.Student
import com.example.nfcproject.model.APIModels.Tag
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
    @GET("Tags/{tagId}")
    fun GetTagById(@Path(value = "tagId", encoded = false) tagId: String): Call<Tag>

    @Headers("X-API-KEY:ed3ffe03-5501-476b-9791-e82d54027cb3")
    @GET("Tags/{tagId}")
    suspend fun GetTagById1(@Path(value = "tagId", encoded = false) tagId: String) : Response<Tag>

    @Headers("X-API-KEY:ed3ffe03-5501-476b-9791-e82d54027cb3")
    @POST("Checkouts")
    fun CreateCheckout(@Body checkout: Checkout): Call<Checkout>

    @Headers("X-API-KEY:ed3ffe03-5501-476b-9791-e82d54027cb3")
    @POST("Checkouts")
    fun CreateCheckout1(@Body checkout: Checkout): Response<Checkout>
}