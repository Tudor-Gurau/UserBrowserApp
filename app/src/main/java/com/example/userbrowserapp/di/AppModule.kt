package com.example.userbrowserapp.di

import android.content.Context
import androidx.room.Room
import com.example.userbrowserapp.data.models.UserDatabase
import com.example.userbrowserapp.data.remote.RandomUserApi
import com.example.userbrowserapp.data.repository.RandomUserListRepositoryImpl
import com.example.userbrowserapp.domain.repository.RandomUserListRepository
import com.example.userbrowserapp.domain.util.Constants.BASE_URL
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
object AppModule {

    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context): UserDatabase {
        return Room.databaseBuilder(
            context = context,
            UserDatabase::class.java,
            UserDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideRandomUserApi(): RandomUserApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RandomUserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRandomUserRepository(
        api: RandomUserApi,
        database: UserDatabase
    ): RandomUserListRepository = RandomUserListRepositoryImpl(api, database)
}