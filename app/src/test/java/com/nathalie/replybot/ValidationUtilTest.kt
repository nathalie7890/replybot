package com.nathalie.replybot

import com.nathalie.replybot.utils.ValidationUtil
import junit.framework.TestCase
import org.junit.Test
class ValidationUtilTest {

    @Test
    fun `should return false if email is empty`() {
        TestCase.assertEquals(ValidationUtil.validateEmail(""), false)
    }

    @Test
    fun `should return true if @ is not included`() {
        TestCase.assertEquals(ValidationUtil.validateEmail("abc.com"), false)
    }

    @Test
    fun `email should not contains any special character other than @ and dot`() {
        TestCase.assertEquals(ValidationUtil.validateEmail("abc#$%aa@abc.com"), false)
    }

    @Test
    fun `should return false is email start with special character`() {
        TestCase.assertEquals(ValidationUtil.validateEmail(".abc@abc.com"), false)
    }
    @Test
    fun `user name should contain only alphanumeric characters`() {
        TestCase.assertEquals(ValidationUtil.validateUserName("#$%joel"), false)
    }

    @Test
    fun `valid email address should pass the test`() {
        TestCase.assertEquals(ValidationUtil.validateEmail("a@a.com"), true)
    }

    @Test
    fun `if username contains only alphanumeric characters, it should pass the test`() {
        TestCase.assertEquals(ValidationUtil.validateUserName("nathalie123"), true)
    }
}