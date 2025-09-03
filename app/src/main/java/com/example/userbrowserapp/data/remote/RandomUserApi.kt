package com.example.userbrowserapp.data.remote

import com.example.userbrowserapp.data.models.UserModelResponse
import com.example.userbrowserapp.domain.util.Constants.NO_OF_USERS_PER_PAGE
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomUserApi {
    @GET("api/")
    suspend fun getUsers(
        @Query("page") page: Int,
        @Query("results") results: Int = NO_OF_USERS_PER_PAGE,
        @Query("seed") seed: String = "xyz"
    ): UserModelResponse
}