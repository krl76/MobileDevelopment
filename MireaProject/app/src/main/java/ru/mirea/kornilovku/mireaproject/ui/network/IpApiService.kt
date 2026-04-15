package ru.mirea.kornilovku.mireaproject.ui.network

import retrofit2.Call
import retrofit2.http.GET

interface IpApiService {
    @GET("json/")
    fun getIpInfo(): Call<IpInfoResponse>
}