package com.kausar.messmanagementapp.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kausar.messmanagementapp.data.firebase_auth.AuthRepository
import com.kausar.messmanagementapp.data.firebase_auth.AuthRepositoryImpl
import com.kausar.messmanagementapp.data.firebase_firestore.FirebaseFirestoreRepo
import com.kausar.messmanagementapp.data.firebase_firestore.FirebaseFirestoreRepoImpl
import com.kausar.messmanagementapp.data.firebase_storage.FirebaseStorageRepo
import com.kausar.messmanagementapp.data.firebase_storage.FirebaseStorageRepoImpl
import com.kausar.messmanagementapp.data.shared_pref.LoginPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesRepositoryImpl(firebaseAuth: FirebaseAuth, context: Context): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth, context)
    }

    @Provides
    @Singleton
    fun providesFirestoreDb(): CollectionReference =
        Firebase.firestore.collection("Meal_Information")


    @Provides
    @Singleton
    fun providesFirebaseFirestoreDbRepository(
        collectionReference: CollectionReference,
        loginPreference: LoginPreference,
        context: Context
    ): FirebaseFirestoreRepo =
        FirebaseFirestoreRepoImpl(collectionReference, loginPreference, context)


    @Provides
    @Singleton
    fun providesFirebaseStorageRef(auth: FirebaseAuth): StorageReference =
        FirebaseStorage.getInstance().getReference("Users/" + auth.currentUser?.uid)


    @Provides
    @Singleton
    fun providesFirebaseStorageRepoImpl(
        context: Context,
        storageReference: StorageReference,
        loginPreference: LoginPreference,
    ): FirebaseStorageRepo = FirebaseStorageRepoImpl(context, storageReference,loginPreference)
}