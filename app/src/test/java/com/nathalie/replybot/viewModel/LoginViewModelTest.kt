package com.nathalie.replybot.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nathalie.replybot.data.repository.AuthRepository
import com.nathalie.replybot.viewModel.auth.LoginViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @Rule
    @JvmField
    val taskExecuterRule = InstantTaskExecutorRule()

    private lateinit var loginViewModel: LoginViewModel
    private val authRepo = Mockito.mock(AuthRepository::class.java)

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        loginViewModel = LoginViewModel(authRepo)
    }


    @Test
    fun `user should be able to login with correct credential`() = runTest {
        Mockito.`when`(authRepo.login("abc@abc.com", "qwerqwer")).thenReturn(true)
        loginViewModel.email.value = "abc@abc.com"
        loginViewModel.password.value = "qwerqwer"
        loginViewModel.login()
        assertEquals(loginViewModel.loginFinish.first(), Unit)
    }

    @Test
    fun `user should not be able to login with wrong credential`() = runTest {
        Mockito.`when`(authRepo.login("abc@abc.com", "qwerqw")).thenReturn(false)
        loginViewModel.email.value = "abc@abc.com"
        loginViewModel.password.value = "qwerqw"
        loginViewModel.login()
        assertEquals(loginViewModel.error.first(), "Login Failed")
    }

    @After
    fun cleanup() {

    }
}