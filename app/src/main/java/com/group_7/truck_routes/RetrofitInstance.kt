package com.group_7.truck_routes

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://routes.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    fun getApiService(): ApiService{
        return getInstance().create(ApiService::class.java)
    }
}