package com.kausar.messmanagementapp.di

import com.kausar.messmanagementapp.data.firebase_firestore.FirebaseFirestoreRepo
import com.kausar.messmanagementapp.data.firebase_firestore.FirebaseFirestoreRepoImpl
import com.kausar.messmanagementapp.data.shared_pref.LoginDataStore
import com.kausar.messmanagementapp.data.shared_pref.LoginPreference
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun providesFirebaseFirestoreDbRepository(
        repo: FirebaseFirestoreRepoImpl
    ): FirebaseFirestoreRepo

    @Binds
    abstract fun bindLoginPreferencesRepo(
        loginPreferencesImpl: LoginDataStore
    ): LoginPreference

}