package com.example.interrapidisimo.di

import com.example.citymap.Constants
import com.example.citymap.IApiClient
import com.example.citymap.IWeatherRepository
import com.example.citymap.Repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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

    /*@Singleton
    @Provides
    fun provideRoom(
        @ApplicationContext context: Context
    ): TablesDatabase {
            return Room.databaseBuilder(
                context,
                TablesDatabase::class.java,
                Constant.DATABASE_NAME
            ).build()
    }*/

    @Provides
    @Singleton
    fun provideRepository(repository: WeatherRepository): IWeatherRepository {
        return repository
    }


}