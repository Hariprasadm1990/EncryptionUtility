package com.haripm.encryptionkotlinutility.interfaces

import com.haripm.encryptionkotlinutility.model.AvengersModel
import retrofit2.http.GET
import retrofit2.Call


interface AvengersApi {
    companion object{
        const val BASE_URL: String = "https://simplifiedcoding.net/demos/"
    }

    @GET("marvel")
    fun getHeroes(): Call<List<AvengersModel>>
}