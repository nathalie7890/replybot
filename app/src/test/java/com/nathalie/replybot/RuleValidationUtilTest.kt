package com.nathalie.replybot

import com.nathalie.replybot.utils.RuleValidationUtil
import junit.framework.TestCase.assertEquals
import org.junit.Test

class RuleValidationUtilTest {
    @Test
    fun `should return false if rule is empty`() {
        assertEquals(RuleValidationUtil.validateRule(""), false)
    }

    @Test
    fun `return false if rule are non ASCII characters, ASCII non-printable characters, and ASCII extended characters`() {
        assertEquals(RuleValidationUtil.validateRule("€ƒ†‡‰Š\tŒ™œ©®µ"), false)
    }

    @Test
    fun `valid rule should pass the test`() {
        assertEquals(RuleValidationUtil.validateRule("hello 1234, please reply to me."), true)
    }

    @Test
    fun `should return false if reply is empty`() {
        assertEquals(RuleValidationUtil.validateReply(""), false)
    }

    @Test
    fun `return false if reply are non ASCII characters, ASCII non-printable characters, and ASCII extended characters`() {
        assertEquals(RuleValidationUtil.validateRule("€ƒ†‡‰Š\tŒ™œ©®µ"), false)
    }

    @Test
    fun `valid reply should pass the test`() {
        assertEquals(RuleValidationUtil.validateRule("Hello, I am away right now, I will get back to you as soon as possible."), true)
    }
}