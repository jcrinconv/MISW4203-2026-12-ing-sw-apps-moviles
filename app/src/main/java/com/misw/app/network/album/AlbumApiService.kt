package com.misw.app.network.album

import com.misw.app.model.Album
import com.misw.app.model.AlbumRequest
import com.misw.app.model.Track
import com.misw.app.model.TrackRequest
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Body

interface AlbumApiService {

    @GET("albums")
    suspend fun getAlbums(): List<Album>

    @GET("albums/{id}")
    suspend fun getAlbumById(@Path("id") id: Int): Album

    @POST("albums")
    suspend fun createAlbum(@Body album: AlbumRequest): Album

    @POST("albums/{id}/tracks")
    suspend fun addTrack(@Path("id") albumId: Int, @Body track: TrackRequest): Track

    @GET("albums/genres")
    suspend fun getGenres(): List<String>

    @GET("albums/recordLabels")
    suspend fun getRecordLabels(): List<String>
}