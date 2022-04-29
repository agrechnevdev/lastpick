package com.lastpick.data

import com.lastpick.data.model.HeroStats
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET

interface OpenDotaApi {

    @GET("api/heroStats")
    suspend fun heroStatsCoroutine(): Response<List<HeroStats>>

    @GET("api/heroStats")
    fun heroStats(): Single<List<HeroStats>>
}