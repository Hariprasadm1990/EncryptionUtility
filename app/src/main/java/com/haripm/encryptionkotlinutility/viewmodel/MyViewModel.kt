package com.haripm.encryptionkotlinutility.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.widget.Toast
import com.haripm.encryptionkotlinutility.interfaces.AvengersApi
import com.haripm.encryptionkotlinutility.model.AvengersModel
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Callback
import retrofit2.Response


class MyViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var heroList: MutableLiveData<List<AvengersModel>>

    fun getHeroes(): LiveData<List<AvengersModel>> {
            heroList = MutableLiveData();
            loadData()

        return heroList
    }

     fun loadData() {
         val certificatePinner = CertificatePinner.Builder()
             .add(
                 "www.simplifiedcoding.net",
                 "sha256/RUQ6Rjk6MEU6RUE6QjI6ODU6NUY6QUE6RTI6MEM6OEM6RTY6NDI6NEM6MDM6MkU6RjA6MUQ6QTM6QUQ6MjY6REE6NEU6QjU6NTc6QjE6Njk6MjM6MDY6MDc6NEQ6RDg="
             ).build()

         val okHttpClient = OkHttpClient.Builder()
             .certificatePinner(certificatePinner)
             .build()


        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(AvengersApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

        val heroApi = retrofit.create(AvengersApi::class.java)
        val call: Call<List<AvengersModel>> = heroApi.getHeroes()
        call.enqueue(object : Callback<List<AvengersModel>> {
            override fun onResponse(call: Call<List<AvengersModel>>, response: Response<List<AvengersModel>>) {
                //finally we are setting the list to our MutableLiveData
                heroList.value = response.body()
            }

            override fun onFailure(call: Call<List<AvengersModel>>, t: Throwable) {
                Toast.makeText(getApplication(), "Retrofit error is :  ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}