package com.liquidglass.musicplayer.data.remote

import retrofit2.Response
import retrofit2.http.*

interface SpotifyApi {

    @GET("v1/search")
    suspend fun search(
        @Query("q") query: String,
        @Query("type") type: String = "track,album,artist,playlist",
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("market") market: String = "US"
    ): Response<SpotifySearchResponse>

    @GET("v1/tracks/{id}")
    suspend fun getTrack(
        @Path("id") id: String,
        @Query("market") market: String = "US"
    ): Response<SpotifyTrack>

    @GET("v1/tracks")
    suspend fun getTracks(
        @Query("ids") ids: String,
        @Query("market") market: String = "US"
    ): Response<Map<String, SpotifyTrack>>

    @GET("v1/albums/{id}")
    suspend fun getAlbum(
        @Path("id") id: String,
        @Query("market") market: String = "US"
    ): Response<SpotifyAlbum>

    @GET("v1/albums/{id}/tracks")
    suspend fun getAlbumTracks(
        @Path("id") id: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("market") market: String = "US"
    ): Response<SpotifyPagingObject<SpotifyTrack>>

    @GET("v1/artists/{id}")
    suspend fun getArtist(
        @Path("id") id: String
    ): Response<SpotifyArtist>

    @GET("v1/artists/{id}/top-tracks")
    suspend fun getArtistTopTracks(
        @Path("id") id: String,
        @Query("market") market: String = "US"
    ): Response<Map<String, List<SpotifyTrack>>>

    @GET("v1/me/playlists")
    suspend fun getMyPlaylists(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<SpotifyPagingObject<SpotifyPlaylist>>

    @GET("v1/playlists/{id}")
    suspend fun getPlaylist(
        @Path("id") id: String
    ): Response<SpotifyPlaylist>

    @GET("v1/playlists/{id}/tracks")
    suspend fun getPlaylistTracks(
        @Path("id") id: String,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): Response<SpotifyPagingObject<SpotifyPlaylistTrack>>

    @GET("v1/me/tracks")
    suspend fun getSavedTracks(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<SpotifyPagingObject<SpotifyPlaylistTrack>>

    @GET("v1/browse/categories")
    suspend fun getCategories(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("country") country: String = "US"
    ): Response<SpotifyCategoriesResponse>

    @GET("v1/browse/categories/{id}/playlists")
    suspend fun getCategoryPlaylists(
        @Path("id") id: String,
        @Query("limit") limit: Int = 20
    ): Response<Map<String, SpotifyPagingObject<SpotifyPlaylist>>>

    @GET("v1/recommendations")
    suspend fun getRecommendations(
        @Query("seed_tracks") seedTracks: String,
        @Query("limit") limit: Int = 20,
        @Query("market") market: String = "US"
    ): Response<SpotifyRecommendationsResponse>

    @GET("v1/me/player/devices")
    suspend fun getDevices(): Response<SpotifyDeviceResponse>

    @GET("v1/me/player")
    suspend fun getPlaybackState(): Response<SpotifyPlaybackState>

    @PUT("v1/me/player/play")
    suspend fun play(
        @Body body: Map<String, Any>? = null
    ): Response<Unit>

    @PUT("v1/me/player/pause")
    suspend fun pause(): Response<Unit>

    @POST("v1/me/player/next")
    suspend fun next(): Response<Unit>

    @POST("v1/me/player/previous")
    suspend fun previous(): Response<Unit>

    @PUT("v1/me/player/seek")
    suspend fun seek(
        @Query("position_ms") positionMs: Long
    ): Response<Unit>

    @PUT("v1/me/player/volume")
    suspend fun setVolume(
        @Query("volume_percent") volumePercent: Int
    ): Response<Unit>

    @PUT("v1/me/player/shuffle")
    suspend fun setShuffle(
        @Query("state") state: Boolean
    ): Response<Unit>

    @PUT("v1/me/player/repeat")
    suspend fun setRepeat(
        @Query("state") state: String
    ): Response<Unit>

    @PUT("v1/me/player")
    suspend fun transferPlayback(
        @Body body: Map<String, Any>
    ): Response<Unit>
}
