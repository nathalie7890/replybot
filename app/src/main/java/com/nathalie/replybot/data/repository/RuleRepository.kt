package com.nathalie.replybot.data.repository

import com.nathalie.replybot.data.model.Rule

interface RuleRepository {
    suspend fun getAllRules(): List<Rule>
    suspend fun getRuleById(id: String): Rule?
    suspend fun addRule(rule: Rule)
    suspend fun updateRule(id: String, rule: Rule): Rule?
    suspend fun deleteRule(id: String)
    suspend fun disableRule(id: String, disabled: Boolean)
}