package com.sarrawi.mynokat.api

import com.sarrawi.mynokat.model.ImgsNokatResponse
import com.sarrawi.mynokat.model.NokatRespone
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("nokatapi")
    suspend fun getAllNokat(): Response<NokatRespone>

    @GET("nokatapi") // تغيير نهاية الطريق الخاصة بك
    suspend fun getAllNokatPa(

        @Query("page") page: Int
    ): Response<NokatRespone>

    @GET("imgnokatapi")
    suspend fun getAllImgNokatPa(
        @Query("page") page: Int
    ):Response<ImgsNokatResponse>

    companion object {
        var retrofitService: ApiService? = null
        fun provideRetrofitInstance(): ApiService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://www.sarrawi.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(ApiService::class.java)
            }
            return retrofitService!!
        }

    }
}