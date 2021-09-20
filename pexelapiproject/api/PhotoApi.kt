package android.mohamed.pexelapiproject.api

import android.mohamed.pexelapiproject.dataModels.Photo
import android.mohamed.pexelapiproject.dataModels.PhotoResponse
import android.mohamed.pexelapiproject.utility.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query


interface PhotoApi {
    @GET("curated")
    suspend fun getCuratedPhotos(
        @Query("per_page")
        perPage: Int = 15,
        @Query("page")
        page: Int = 1,
        @Header("Authorization")
        apiKey: String = Constants.API_KEY
    ): Response<PhotoResponse>

    @GET("search")
    suspend fun searchPhoto(
        @Query("query") searchQuery: String,
        @Query("page") page: Int,
        @Header("Authorization")
        apiKey: String = Constants.API_KEY
    ): Response<PhotoResponse>

    @GET("photos/{id}")
    suspend fun getPhoto(
        @Path("id") id: Int,
        @Header("Authorization") apiKey: String = Constants.API_KEY
    ): Response<Photo>

}