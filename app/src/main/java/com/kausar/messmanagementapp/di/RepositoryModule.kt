package com.kausar.messmanagementapp.di

import com.kausar.messmanagementapp.data.firebase_db.RealtimeDbRepository
import com.kausar.messmanagementapp.data.firebase_db.RealtimeDbRepositoryImpl
import com.kausar.messmanagementapp.data.firebase_firestore.FirebaseFirestoreRepo
import com.kausar.messmanagementapp.data.firebase_firestore.FirebaseFirestoreRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun providesRealtimeDbRepository(
        repo : RealtimeDbRepositoryImpl
    ) : RealtimeDbRepository


    @Binds
    abstract fun providesFirebaseFirestoreDbRepository(
        repo : FirebaseFirestoreRepoImpl
    ) : FirebaseFirestoreRepo

}