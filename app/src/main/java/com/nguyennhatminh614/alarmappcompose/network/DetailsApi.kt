package com.nguyennhatminh614.alarmappcompose.network

import com.nguyennhatminh614.alarmappcompose.network.model.DetailsApiModel
import retrofit2.http.GET
import retrofit2.http.Path

interface DetailsApi {

    @GET("/users/{user}")
    suspend fun getDetails(@Path("user") user: String): DetailsApiModel
}