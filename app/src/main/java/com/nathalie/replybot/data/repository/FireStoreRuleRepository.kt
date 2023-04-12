package com.nathalie.replybot.data.repository

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.nathalie.replybot.data.model.Rule
import com.nathalie.replybot.utils.Constants
import kotlinx.coroutines.tasks.await

class FireStoreRuleRepository(private val ref: CollectionReference) : RuleRepository {
    override suspend fun getAllRules(): List<Rule> {
        val rules = mutableListOf<Rule>()
        val res = ref.get().await()
        for (document in res) {
            rules.add(document.toObject(Rule::class.java).copy(id = document.id))
        }

        return rules
    }

    override suspend fun getRuleById(id: String): Rule? {
        val res = ref.document(id).get().await()
        return res.toObject(Rule::class.java)?.copy(id = id)
    }

    override suspend fun addRule(rule: Rule) {
        ref.add(rule).await()
    }

    override suspend fun updateRule(id: String, rule: Rule): Rule? {
        ref.document(id).set(rule).await()
        return null
    }

    override suspend fun deleteRule(id: String) {
        ref.document(id).delete().await()
    }

    override suspend fun disableRule(id: String, disabled: Boolean) {
        ref.document(id).update("disabled", disabled).await()
    }

}