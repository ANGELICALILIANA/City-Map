package com.example.citymap.di

import android.content.Context
import androidx.room.Room
import com.example.citymap.utils.Constants
import com.example.citymap.service.IApiClient
import com.example.citymap.repository.IWeatherRepository
import com.example.citymap.repository.WeatherRepository
import com.example.citymap.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideApiClient(retrofit: Retrofit): IApiClient {
        return retrofit.create(IApiClient::class.java)
    }

    @Provides
    @Singleton
    fun provideRepository(repository: WeatherRepository): IWeatherRepository {
        return repository
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }


}