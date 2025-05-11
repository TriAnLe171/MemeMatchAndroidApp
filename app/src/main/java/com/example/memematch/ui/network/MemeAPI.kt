package com.example.memematch.ui.network

import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Field
import retrofit2.http.Headers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

data class QueryRequest(
    val query: String,
    val top_n: Int = 10, // Number of results to fetch
    val top_n_template: Int = 5 // Number of templates to fetch
)

data class MemeResponse(
    val memes: List<String>,
    val need_template: Boolean,
    val details: Map<String, Any>
)

interface MemeApi {
    @POST("recommend")
    suspend fun getRecommendations(@Body request: QueryRequest): Response<MemeResponse>

    @Multipart
    @POST("recommend/upload")
    suspend fun uploadMeme(
        @Part("context") context: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("top_n") topN: RequestBody
    ): Response<MemeResponse>
}
