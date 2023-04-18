package com.nathalie.replybot.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nathalie.replybot.data.repository.AuthRepository
import com.nathalie.replybot.data.repository.FireStoreRuleRepository
import com.nathalie.replybot.data.repository.RuleRepository
import com.nathalie.replybot.service.AuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MyAppDependency {

    @Provides
    @Singleton
    fun getFireStore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun getFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Provides
    @Singleton
    fun getAuthRepository(auth: FirebaseAuth, db: FirebaseFirestore): AuthRepository {
        return AuthService(auth, db.collection("users"))
    }

    @Provides
    @Singleton
    fun getFireStoreRuleRepository(db: FirebaseFirestore): RuleRepository {
        return FireStoreRuleRepository(db.collection("rules"))
    }

}