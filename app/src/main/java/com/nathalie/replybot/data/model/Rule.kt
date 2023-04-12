package com.nathalie.replybot.data.model

data class Rule(
    val id: String? = "",
    val keyword: String = "",
    val msg: String = "",
    val whatsapp: Boolean = false,
    val facebook: Boolean = false,
    val slack: Boolean = false,
    val disabled: Boolean = false,
    val userId: String = ""
)
