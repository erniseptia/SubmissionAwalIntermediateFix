package app.story.mystoryappneww.api

import app.story.mystoryappneww.dataclass.Login
import app.story.mystoryappneww.dataclass.NewStory
import app.story.mystoryappneww.dataclass.Register
import app.story.mystoryappneww.dataclass.Stories
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("v1/stories")
    fun getAllStories(
        @Header("Authorization") bearer: String,
    ): Call<Stories>

    @FormUrlEncoded
    @POST("v1/register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<Register>

    @FormUrlEncoded
    @POST("v1/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<Login>

    @Multipart
    @POST("v1/stories")
    fun addStory(
        @Header("Authorization") bearer: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<NewStory>
}