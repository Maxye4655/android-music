package com.liquidglass.musicplayer.data.remote

import com.google.gson.annotations.SerializedName

data class SpotifyTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("scope") val scope: String,
    @SerializedName("expires_in") val expiresIn: Long,
    @SerializedName("refresh_token") val refreshToken: String?
)

data class SpotifySearchResponse(
    val tracks: SpotifyPagingObject<SpotifyTrack>?,
    val albums: SpotifyPagingObject<SpotifyAlbum>?,
    val artists: SpotifyPagingObject<SpotifyArtist>?,
    val playlists: SpotifyPagingObject<SpotifyPlaylist>?
)

data class SpotifyTrack(
    val id: String,
    val name: String,
    val uri: String,
    val duration_ms: Long,
    val explicit: Boolean,
    val popularity: Int,
    val preview_url: String?,
    val artists: List<SpotifyArtistSimplified>,
    val album: SpotifyAlbumSimplified
)

data class SpotifyArtistSimplified(
    val id: String,
    val name: String
)

data class SpotifyAlbumSimplified(
    val id: String,
    val name: String,
    val images: List<SpotifyImage>
)

data class SpotifyAlbum(
    val id: String,
    val name: String,
    val release_date: String,
    val total_tracks: Int,
    val images: List<SpotifyImage>,
    val artists: List<SpotifyArtistSimplified>,
    val tracks: SpotifyPagingObject<SpotifyTrack>?
)

data class SpotifyArtist(
    val id: String,
    val name: String,
    val images: List<SpotifyImage>,
    val followers: SpotifyFollowers?,
    val genres: List<String>
)

data class SpotifyPlaylist(
    val id: String,
    val name: String,
    val description: String?,
    val images: List<SpotifyImage>,
    val owner: SpotifyUser,
    val tracks: SpotifyPagingObject<SpotifyPlaylistTrack>?
)

data class SpotifyPlaylistTrack(
    val added_at: String,
    val track: SpotifyTrack?
)

data class SpotifyUser(
    val id: String,
    val display_name: String?,
    val href: String
)

data class SpotifyImage(
    val url: String,
    val width: Int?,
    val height: Int?
)

data class SpotifyPagingObject<T>(
    val href: String,
    val items: List<T>,
    val limit: Int,
    val offset: Int,
    val total: Int,
    val next: String?,
    val previous: String?
)

data class SpotifyFollowers(
    val total: Int
)

data class SpotifyCategoriesResponse(
    val categories: SpotifyPagingObject<SpotifyCategory>
)

data class SpotifyCategory(
    val id: String,
    val name: String,
    val icons: List<SpotifyImage>
)

data class SpotifyRecommendationsResponse(
    val tracks: List<SpotifyTrack>
)

data class SpotifyDeviceResponse(
    val devices: List<SpotifyDevice>
)

data class SpotifyDevice(
    val id: String,
    val name: String,
    val type: String,
    val is_active: Boolean,
    val is_restricted: Boolean
)

data class SpotifyPlaybackState(
    val is_playing: Boolean,
    val item: SpotifyTrack?,
    val progress_ms: Long,
    val device: SpotifyDevice?,
    val shuffle_state: Boolean,
    val repeat_state: String
)
