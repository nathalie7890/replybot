package com.nathalie.replybot.utils

object RuleValidationUtil {
    fun validateRule(rule: String): Boolean {
        val ruleCheck = Regex("^[\\x20-\\x7E]+$", RegexOption.IGNORE_CASE)
        if(ruleCheck.matches(rule)) return true
        return false
    }

    fun validateReply(reply: String): Boolean {
        val replyCheck = Regex("^[\\x20-\\x7E]+$", RegexOption.IGNORE_CASE)
        if(replyCheck.matches(reply)) return true
        return false
    }
}