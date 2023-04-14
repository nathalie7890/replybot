package com.nathalie.replybot.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nathalie.replybot.data.model.Rule
import com.nathalie.replybot.service.AuthService
import com.nathalie.replybot.utils.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FireStoreRuleRepository(private val ref: CollectionReference) :
    RuleRepository {

    var authRepo: AuthService =
        AuthService(FirebaseAuth.getInstance(), Firebase.firestore.collection("users"))

    //fetch rules from FireStore that matches the current user's id then returns a list of rules
    override suspend fun getAllRules(): List<Rule> {
        val uid = authRepo.getUid()

        val rules = mutableListOf<Rule>()
        val res = ref.whereEqualTo("userId", uid).get().await()
        for (document in res) {
            rules.add(document.toObject(Rule::class.java).copy(id = document.id))
        }
        return rules
    }

    //fetch rule that matches the provided id
    override suspend fun getRuleById(id: String): Rule? {
        val res = ref.document(id).get().await()
        return res.toObject(Rule::class.java)?.copy(id = id)
    }

    //add rule to FireStore
    override suspend fun addRule(rule: Rule) {
        ref.add(rule).await()
    }

    //update rule that matches the provided id in FireStore
    override suspend fun updateRule(id: String, rule: Rule): Rule? {
        ref.document(id).set(rule).await()
        return null
    }

    //delete rule that matches the provided id from FireStore
    override suspend fun deleteRule(id: String) {
        ref.document(id).delete().await()
    }

    //update rule's disable value
    override suspend fun disableRule(id: String, disabled: Boolean) {
        ref.document(id).update("disabled", disabled).await()
    }

}