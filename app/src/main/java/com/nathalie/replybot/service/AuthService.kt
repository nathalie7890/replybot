package com.nathalie.replybot.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.nathalie.replybot.data.model.User
import kotlinx.coroutines.tasks.await

class AuthService(private val auth: FirebaseAuth, private val ref: CollectionReference) {
    //add user to FirebaseAuth and FireStore user collection
    suspend fun register(user: User) {
        val res = auth.createUserWithEmailAndPassword(user.email, user.password).await()
        res.user?.uid?.let {
            ref.document(it).set(user.copy(id = it))
        }
    }

    //login user
    suspend fun login(email: String, password: String): Boolean {
        val res = auth.signInWithEmailAndPassword(email, password).await()
        return res.user?.uid != null
    }

    //returns true if current user is not null
    fun isLoggedIn(): Boolean {
        auth.currentUser ?: return false
        return true
    }

    //signs out current user
    fun signOut() {
        auth.signOut()
    }

    //fetch current user
    suspend fun getCurrentUser(): User? {
        return auth.uid?.let {
            ref.document(it).get().await().toObject(User::class.java)
        }
    }

    //get current user's uid
    fun getUid(): String? {
        return auth.uid
    }
}