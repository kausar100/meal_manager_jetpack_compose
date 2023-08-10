package com.kausar.messmanagementapp.di

import android.content.Context
import com.kausar.messmanagementapp.data.shared_pref.LoginDataStore
import com.kausar.messmanagementapp.data.shared_pref.LoginPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppContext(@ApplicationContext appContext: Context) = appContext


    @Provides
    @Singleton
    fun providesLoginPreferencesImpl(context: Context): LoginPreference{
        return LoginDataStore(context)
    }
}