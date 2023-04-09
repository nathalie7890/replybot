package com.nathalie.replybot.data.repository

import com.google.firebase.firestore.CollectionReference
import com.nathalie.replybot.data.model.Rule
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
        TODO("Not yet implemented")
    }

    override suspend fun addRule(rule: Rule) {
        ref.add(rule).await()
    }

    override suspend fun updateRule(id: String, rule: Rule): Rule? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRule(id: String) {
        TODO("Not yet implemented")
    }


}