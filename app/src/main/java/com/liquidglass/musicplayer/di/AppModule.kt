package com.liquidglass.musicplayer.di

import android.content.Context
import androidx.room.Room
import com.liquidglass.musicplayer.data.local.*
import com.liquidglass.musicplayer.data.remote.AuthInterceptor
import com.liquidglass.musicplayer.data.remote.SpotifyApi
import com.liquidglass.musicplayer.data.remote.SpotifyAuthApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MusicDatabase {
        return Room.databaseBuilder(
            context,
            MusicDatabase::class.java,
            "liquid_glass_music.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides fun provideTrackDao(db: MusicDatabase): TrackDao = db.trackDao()
    @Provides fun providePlaylistDao(db: MusicDatabase): PlaylistDao = db.playlistDao()
    @Provides fun provideAlbumDao(db: MusicDatabase): AlbumDao = db.albumDao()
    @Provides fun provideArtistDao(db: MusicDatabase): ArtistDao = db.artistDao()
    @Provides fun provideSearchHistoryDao(db: MusicDatabase): SearchHistoryDao = db.searchHistoryDao()
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.spotify.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://accounts.spotify.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideSpotifyApi(retrofit: Retrofit): SpotifyApi {
        return retrofit.create(SpotifyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSpotifyAuthApi(authRetrofit: Retrofit): SpotifyAuthApi {
        return authRetrofit.create(SpotifyAuthApi::class.java)
    }
}
