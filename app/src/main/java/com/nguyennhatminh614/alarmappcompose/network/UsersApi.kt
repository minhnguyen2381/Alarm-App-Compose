package com.nguyennhatminh614.alarmappcompose.network

import com.nguyennhatminh614.alarmappcompose.network.model.UserApiModel
import retrofit2.http.GET

interface UsersApi {

    @GET("/repos/square/retrofit/stargazers")
    suspend fun getUsers(): List<UserApiModel>
}