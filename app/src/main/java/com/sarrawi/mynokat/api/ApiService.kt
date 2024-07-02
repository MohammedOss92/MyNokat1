package com.sarrawi.mynokat.api

import com.sarrawi.mynokat.model.ImgsNokatResponse
import com.sarrawi.mynokat.model.NokatRespone
import com.sarrawi.mynokat.model.NokatTypeResponse
import com.sarrawi.mynokat.model.TotalImages
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

    @GET("nokattypes")
    suspend fun getAllNokatTypes(
        @Query("page") page: Int
    ): Response<NokatTypeResponse>

    @GET("imgnokatapi")
    suspend fun getAllImgNokatPa(
        @Query("page") page: Int
    ):Response<ImgsNokatResponse>

    @GET("imgnokatapinew")
    suspend fun getAllImgNokatPaNew(
        @Query("page") page: Int
    ):Response<ImgsNokatResponse>

    @GET("image-count")
    suspend fun ImgCount(): Response<TotalImages>



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