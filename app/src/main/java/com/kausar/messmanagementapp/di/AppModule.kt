package com.kausar.messmanagementapp.di

import com.google.firebase.auth.FirebaseAuth
import com.kausar.messmanagementapp.data.firebase_auth.AuthRepository
import com.kausar.messmanagementapp.data.firebase_auth.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule{

    @Provides
    @Singleton
    fun providesFirebaseAuth()  = FirebaseAuth.getInstance()

//    @Provides
//    @Singleton
//    fun providesContext(@ApplicationContext appContext: Context)  = appContext

    @Provides
    @Singleton
    fun providesRepositoryImpl(firebaseAuth: FirebaseAuth): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth)
    }

}